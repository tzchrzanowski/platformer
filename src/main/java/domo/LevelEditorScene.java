package domo;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class LevelEditorScene extends Scene {
    private String vertexShaderSrc = "#version 410" +
            "\n" +
            "layout (location=0) in vec3 aPos;\n" +
            "layout (location=1) in vec4 aColor;\n" +
            "\n" +
            "out vec4 fColor;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    fColor = aColor;\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "}";

    private String fragmentShaderSrc = "#version 410" +
            "\n" +
            "in vec4 fColor;\n" +
            "\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    color = fColor;\n" +
            "}";

    /* since we pass data from cpu to gpu. we need some identifiers: */
    private int vertexID, fragmentID, shaderProgram;

    /* position, color, */
    private float[] vertexArray = {
        // position             // color
         0.5f, -0.5f, 0.0f,     1.0f, 0.0f, 0.0f, 1.0f, // bottom right | index --> 0
        -0.5f,  0.5f, 0.0f,     0.0f, 1.0f, 0.0f, 1.0f, // top left     | index --> 1
         0.5f,  0.5f, 0.0f,     0.0f, 0.0f, 1.0f, 1.0f, // top right    | index --> 2
        -0.5f, -0.5f, 0.0f,     1.0f, 1.0f, 0.0f, 1.0f, // bottom left  | index --> 3
    };

    /* list of elements to be displayed
    *  GOES in COUNTER-CLOCKWISE order.
    *  2 triangles
    *  1 at the top of the square
    *  2nd at the bottom of the square
                x           x


                x           x
    * numbers represent the index of vertices that we defined in vertexArray.
    * */
    private int[] elementArray = {
            2, 1, 0, // top right triangle. counter-clockwise, top-right -> top-left -> bottom-right
            0, 1, 3  // bottom left triangle
    };

    // id's needed to send objects from above arrays to GPU:
    private int vaoID, vboID, eboID;

    public LevelEditorScene() {
    }

    @Override
    public void init() {
        /*
        * Compile and link Shaders
        * import glCreateShader without C
        * import glShaderSource without C
        * */

        // first load and compile the vertex shader:
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        // pass the shader source code to the GPU
        glShaderSource(vertexID, vertexShaderSrc);
        glCompileShader(vertexID);

        /* Check for error in compilation process:
         * will give 0 if fail, 1 if succeed,
         * glGetShaderi() , gives info:
         * glShaderInfoLog requires length, thats why we are getting length of error message
         */
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: defaultShader.glsl\n\tVertex shader compilation failed.");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : ""; // breaks out of program if error happens if assertions are enabled.
        }

        // second step is to load and compile the fragment shader:
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        // pass the shader source code to the GPU
        glShaderSource(fragmentID, fragmentShaderSrc);
        glCompileShader(fragmentID);

        /* Check for error in compilation process:
         * will give 0 if fail, 1 if succeed,
         * glGetShaderi() , gives info:
         * glShaderInfoLog requires length, thats why we are getting length of error message
         */
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: defaultShader.glsl\n\tFragment shader compilation failed.");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : ""; // breaks out of program if error happens if assertions are enabled.
        }

        /* Link shaders and check for errors*/
        shaderProgram = glCreateProgram(); // create unique identifier for new program;
        glAttachShader(shaderProgram, vertexID);
        glAttachShader(shaderProgram, fragmentID);
        glLinkProgram(shaderProgram);

        /* check for linking errors */
        success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if(success == GL_FALSE) {
            int len = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: defaultShader.glsl\n\tLinking of shaders failed.");
            System.out.println(glGetProgramInfoLog(shaderProgram, len));
            assert false : "";
        }

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
        int floatSizeBytes = 4; // we explain that every elements are floats, and size of a flaot is 4 bytes
        int vertexSizeBytes = (positionsSize + colorSize) * floatSizeBytes;
        // first index 0, is a position
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0); // last value offset is 0 , because we are not going anywhere after this point
        glEnableVertexAttribArray(0);

        // first index 1, represents Colors now.
        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * floatSizeBytes);
        glEnableVertexAttribArray(1);

        //-------------------------------------------------------------------------------
    }

    @Override
    public void update(float dt) {
        // bind shader program
        glUseProgram(shaderProgram);
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
        glUseProgram(0);
    }
}
