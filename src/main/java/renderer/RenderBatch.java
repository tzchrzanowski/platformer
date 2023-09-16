package renderer;

import components.SpriteRenderer;
import domo.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch {

    // ------------------------ STATIC PROPERTIES ----------------------------
    /*
    * Vertex structure layout that will never change:
    *
    * Pos                   Color                           tex coords          tex id
    * float, float,         float, float, float, float      float, float        float
    * */
    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int TEX_COORDS_SIZE = 2;
    private final int TEX_ID_SIZE = 1;

    // offsets are position of parameter that we want to take, by calc from the beggining of array
    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.BYTES;

    private final int VERTEX_SIZE = 9; // we have 9 floats inside of one vertex. based on floats up.
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;
    // -----------------------------------------------------------------------

    // ------------------------ DYNAMIC PROPERTIES ----------------------------
    private SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;
    private int[] texSlots = {0, 1, 2, 3, 4, 5, 6, 7};
    private List<Texture> textures;
    private int vaoID;
    private int vboID;
    private int maxBatchSize;
    private Shader shader;
    // -----------------------------------------------------------------------

    /* Takes param of how many will batch contain...*/
    public RenderBatch(int maxBatchSize) {
        shader = AssetPool.getShader("assets/shaders/default.glsl");
        this.sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;

        // 4 vertices quads:
        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        this.numSprites = 0;
        this.hasRoom = true;
        this.textures = new ArrayList<>();
    }

    /*
    * creating data on GPU
    * */
    public void start() {
        // Generate and bind a Vertex Array Object
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Allocate space for vertices:
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Create and upload indices buffer:, to reduce vertex duplication
        int eboID = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Enable the buffer attribute pointers, for Position and Color
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
        glEnableVertexAttribArray(3);
    }

    public void addSprite(SpriteRenderer spr) {
        // Get index and add renderObject:
        int index = this.numSprites;
        // we want ot put it at the end of current array.
        this.sprites[index] = spr;
        this.numSprites++;

        // before we load this sprite, we want to add texture to the local list of textures ( batches containing all images).
        if (spr.getTexture() != null) {
            if (!textures.contains(spr.getTexture())) {
                textures.add(spr.getTexture());
            }
        }

        // Add properties to local verticies array
        loadVertexProperties(index);

        if(numSprites >= this.maxBatchSize) {
            this.hasRoom = false;
        }
    }

    public void render() {
        // For now, we will rebuffer all data every frame...
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        // use shader
        shader.use();
        shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());

        // binding textures:
        for (int i=0; i< textures.size(); i++) {
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }

        shader.uploadIntArray("uTextures", texSlots);

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        // draw elements:
        glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);

        // disable everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        for (int i=0; i< textures.size(); i++) {
            textures.get(i).unbind();
        }
        shader.detach();
    }

    /*
    * Create 4 vertices per quad
    * */
    private void loadVertexProperties(int index) {
        SpriteRenderer sprite = this.sprites[index];

        /*
        * Finde the offset within array ( 4 vertices per sprite)
        * float float________float float float float    , next one:
        * */
        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = sprite.getColor();
        Vector2f[] texCoords = sprite.getTexCoords();

        int textureId = 0;
        /*
        * [0, texture, texture, texture, ... ] looping until we find texture that matches..
        * 0 is special slot that just saves space so we can add textures + 1 in relation to this one
        * */
        if (sprite.getTexture() != null) {
            for (int i=0; i< textures.size(); i++) {
                if (textures.get(i) == sprite.getTexture()) {
                    textureId = i + 1;
                    break;
                }
            }
        }

        /*
        * add vertice with the appropriate properties;
        *
        *       X          X
        *
        *       X          X
        * */
        float xAdd = 1.0f;
        float yAdd = 1.0f;
        for (int i=0; i< 4; i++) {
            if (i==1) {
                yAdd = 0.0f;
            } else if (i==2) {
                xAdd = 0.0f;
            } else if (i==3) {
                yAdd = 1.0f;
            }

            // load positions :
            vertices[offset] = sprite.gameObject.transform.position.x + (xAdd * sprite.gameObject.transform.scale.x);
            vertices[offset + 1] = sprite.gameObject.transform.position.y + (yAdd * sprite.gameObject.transform.scale.y);

            // load color:
            vertices[offset+2] = color.x;
            vertices[offset+3] = color.y;
            vertices[offset+4] = color.z;
            vertices[offset+5] = color.w;

            // load texture coordinates
            vertices[offset+6] = texCoords[i].x;
            vertices[offset+7] = texCoords[i].y;

            // load id
            vertices[offset+8] = textureId;

            offset += VERTEX_SIZE;
        }
    }

    private int[] generateIndices() {
        // 6 indices per quad ( 3 per triangle )
        int [] elements = new int[6* maxBatchSize];
        for (int i=0; i< maxBatchSize; i++) {
            loadElementIndices(elements, i);
        }
        return elements;
    }

    /*
    * mapping the values to the triangles in sprite.
    * */
    private void loadElementIndices(int[] elements, int index) {
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;
        // 3, 2, 0, 0, 2, 1          7, 6, 4, 4, 6, 5
        // creating Triangle 1
        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex + 1 ] = offset + 2;
        elements[offsetArrayIndex + 2 ] = offset + 0;

        // creating Triangle 2
        elements[offsetArrayIndex + 3 ] = offset + 0;
        elements[offsetArrayIndex + 4 ] = offset + 2;
        elements[offsetArrayIndex + 5 ] = offset + 1;
    }

    public boolean hasRoom () {
        return this.hasRoom;
    }

    public boolean hasTextureRoom() {
        return this.textures.size() < 8;
    }

    public boolean hasTexture(Texture tex) {
        return this.textures.contains(tex);
    }
}
