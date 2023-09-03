package domo;

import static org.lwjgl.opengl.GL20.*;

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

    }

    @Override
    public void update(float dt) {
    }
}
