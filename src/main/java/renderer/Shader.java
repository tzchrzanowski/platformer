package renderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {
    private int shaderProgramID;
    private String vertexSource;
    private String fragmentSource;
    private String filepath;

    /*
    * opening the file from shaders default.glsl for example.
    * setting regex from all lines in .glsl file.
    * */
    public Shader(String filepath) {
        this.filepath = filepath;
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            // find first pattern after #type 'pattern'
            // will return 0, cause # starts at index 0 of line, +6 means that we will get past the word #type_
            int index = source.indexOf("#type") + 6;
            // find end of line , on windows "\r\n"
            int eol = source.indexOf("\n", index);
            String firstPattern = source.substring(index, eol).trim();

            // find second pattern after #type 'pattern'
            index = source.indexOf("#type", eol) + 6;
            eol = source.indexOf("\n", index);
            String secondPattern = source.substring(index, eol).trim();

            // checkign first pattern:
            if(firstPattern.equals("vertex")) {
                vertexSource = splitString[1];
            } else if(firstPattern.equals("fragment")) {
                fragmentSource = splitString[1];
            } else {
                throw new IOException("Unexpected token " + firstPattern +  " in " + filepath);
            }

            // checking second pattern:
            if(secondPattern.equals("vertex")) {
                vertexSource = splitString[2];
            } else if(secondPattern.equals("fragment")) {
                fragmentSource = splitString[2];
            } else {
                throw new IOException("Unexpected token " + secondPattern +  " in " + filepath);
            }

            System.out.println(vertexSource);
            System.out.println(fragmentSource);

        } catch(IOException e) {
            e.printStackTrace();
            assert false: "Error: could not open file for shader: " + filepath;
        }
    }

    public void compile() {
        int vertexID, fragmentID;
        /*
         * Compile and link Shaders
         * import glCreateShader without C
         * import glShaderSource without C
         * */

        // first load and compile the vertex shader:
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        // pass the shader source code to the GPU
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);

        /* Check for error in compilation process:
         * will give 0 if fail, 1 if succeed,
         * glGetShaderi() , gives info:
         * glShaderInfoLog requires length, thats why we are getting length of error message
         */
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: " + filepath + "\n\tVertex shader compilation failed.");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : ""; // breaks out of program if error happens if assertions are enabled.
        }

        // second step is to load and compile the fragment shader:
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        // pass the shader source code to the GPU
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);

        /* Check for error in compilation process:
         * will give 0 if fail, 1 if succeed,
         * glGetShaderi() , gives info:
         * glShaderInfoLog requires length, thats why we are getting length of error message
         */
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: " + filepath + "\n\tFragment shader compilation failed.");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : ""; // breaks out of program if error happens if assertions are enabled.
        }

        /* Link shaders and check for errors*/
        shaderProgramID = glCreateProgram(); // create unique identifier for new program;
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        /* check for linking errors */
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if(success == GL_FALSE) {
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: " + filepath + "\n\tLinking of shaders failed.");
            System.out.println(glGetProgramInfoLog(shaderProgramID, len));
            assert false : "";
        }
    }

    public void use() {
        // bind shader program
        glUseProgram(shaderProgramID);
    }

    public void detach() {
        glUseProgram(0);
    }
}
