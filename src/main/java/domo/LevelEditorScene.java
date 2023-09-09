package domo;

import components.FontRenderer;
import components.SpriteRenderer;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import renderer.Shader;
import renderer.Texture;
import util.Time;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class LevelEditorScene extends Scene {
    // --------------------------Params definition: ---------------------------------
    // Shader from renderer/Shader
    private Shader defaultShader;
    private Texture testTexture;
    // id's needed to send objects from above arrays to GPU:
    private int vaoID, vboID, eboID;
    /* position, color, */
    private float[] vertexArray = {
        // position             // color                    // UV Coordinates
         100f, -0f, 0.0f,     1.0f, 0.0f, 0.0f, 1.0f,       1, 1,   // bottom right | index --> 0
         0f,  100f, 0.0f,     0.0f, 1.0f, 0.0f, 1.0f,       0, 0,   // top left     | index --> 1
         100f,100f, 0.0f,     1.0f, 0.0f, 1.0f, 1.0f,       1, 0,   // top right    | index --> 2
         0f,    0f, 0.0f,     1.0f, 1.0f, 0.0f, 1.0f,       0, 1,   // bottom left  | index --> 3
    };
    /* list of elements to be displayed
    *  GOES in COUNTER-CLOCKWISE order.
    *  2 triangles
    *  1 at the top of the square
    *  2nd at the bottom of the square
    *            x           x
    *
    *
    *            x           x
    * numbers represent the index of vertices that we defined in vertexArray. */
    private int[] elementArray = {
            2, 1, 0, // top right triangle. counter-clockwise, top-right -> top-left -> bottom-right
            0, 1, 3  // bottom left triangle
    };

    //---------------------- definition of game objects:
    GameObject testObj;
    private boolean firstTime = false;

    // ---------------------------------- methods definitions : ------------------------------------
    public LevelEditorScene() {
    }

    /*
    * Operations used on init of the Scene component.
    * Creates scene based on shader provided
    * Triggers compiling and linking shaders in renderer/Sshader class
    * Sends objects to GPU
    * */
    @Override
    public void init() {
        //------- initiate game objects at the start of the scene----X
        System.out.println("Creating test object...");
        this.testObj = new GameObject("test obj name");
        this.testObj.addComponent(new SpriteRenderer());
        this.testObj.addComponent(new FontRenderer());
        this.addGameObjectToScene(this.testObj);
        //------------------------------------------------------------X

        /*
        * Create new camera:
        * */
        this.camera = new Camera(new Vector2f());

        /*
        * Get shader first from file
        * */
        defaultShader = new Shader("assets/shaders/default.glsl");

        /*
        * Compile and link shaders:
        * */
        defaultShader.compile();

        /*
        * Uploading texture to the GPU
        * */
        this.testTexture = new Texture("assets/Images/mario.png");
        
        /* ---------------------------------------------------------------------------------
        *  GENERATE VAO, VBO, EBO buffer objects, and send them to GPU
        * */
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID); // everything what we want to do, we are doing it to this array specifically , which is vaoID

        /*
        * create a flaot buffer of vertices:
        * uses array of vertices defined at the top of this fucntion
        * */
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip(); // flipping to make it in correct order to open GL.

        // create VBO and upload the vertex buffer:
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        /*
        * create the indecies and upload:
        * uses elementArray defined at the top of the page.
        * */
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // add the vertex attribute pointers, which explains that 3 first elements are coords and other 4 are colors
        int positionsSize = 3;
        int colorSize = 4;
        int uvSize = 2;
        int vertexSizeBytes = (positionsSize + colorSize + uvSize) * Float.BYTES;
        // first index 0, is a position
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0); // last value offset is 0 , because we are not going anywhere after this point
        glEnableVertexAttribArray(0);

        // first index 1, represents Colors now.
        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * Float.BYTES);
        glEnableVertexAttribArray(1);

        // enable attribute pointer for UV coortinates
        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeBytes, (positionsSize + colorSize) * Float.BYTES) ;

        // enable attribute vertex array:
        glEnableVertexAttribArray(2);
        //------------------------------------- End of GPU ------------------------------------------
    }

    private void moveCamera(float dt) {
        // move camera a bit to the side one step at a time.
        camera.position.y -= dt * 20.0f;
        camera.position.x -= dt * 50.0f;
    }

    /*
    * Starts to use the Shader, and then unbinds it from memory.
    * */
    @Override
    public void update(float dt) {
        // this is where you can trigger moveCamera():
        // moveCamera(dt);

        // bind shader program
        defaultShader.use();

        /*
        * Upload texture to shader:
        * */
        defaultShader.uploadTexture("TEX_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0); // now activate slot 0
        testTexture.bind(); // put texture zero into the slot

        // before we bind everything, we upload ..
        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
        defaultShader.uploadFloat("uTime", Time.getTime()); // passing time to it, we will do time-based animations in shader using this.

        // bind the VAO vertex that we are using,
        glBindVertexArray(vaoID);

        // enable the vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0); // we are starting at 0,

        // now unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
                                       
        glBindVertexArray(0);
        defaultShader.detach();


        //------- operations on game objects:
        if (!firstTime) {
            System.out.println("Creating game object!");
            GameObject go = new GameObject("Game test 2 object");
            go.addComponent(new SpriteRenderer());
            this.addGameObjectToScene(go);
            firstTime = true;
         }

        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }

    }
}
