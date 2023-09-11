package domo;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {
    //--------------------- Params ---------------------
    private int width;
    private int height;
    private String title;
    private long glfwWindow;

    public float r, g, b, a;
    private boolean fadeToBlack = false;

    // only one instance of window:
    private static Window window;

    // setting up current scene variable, as Scene type.
    private static Scene currentScene = null;
    //--------------------------------------------------


    /* creates only onw Window class with this constructor:
     * Initially set to standard HD definition 1920x1080
     */
    private Window (){
        this.width = 1920;
        this.height = 1080;
        this.title = "Platformer";

        r=1;
        g=1;
        b=1;
        a=1;
    }

    /*
    * changes scene accordingly to passed scene number
    * every scene is initialized before its run
    * there is no default scene
    * */
    public static void changeScene(int newSceneIndex) {
        switch (newSceneIndex) {
            case 0:
                currentScene = new LevelEditorScene();
                currentScene.init();
                currentScene.start();
                break;
            case 1:
                currentScene = new LevelScene();
                currentScene.init();
                currentScene.start();
                break;
            default:
                assert false : "Unknown scene with index : " + newSceneIndex;
                break;
        }
    }

    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }
        return Window.window;
    }

    public static Scene getScene() {
        return get().currentScene;
    }

    public void run() {
        System.out.println("Konichiwa! LWJGL version: " + Version.getVersion() + "!" + " GL version: " + GLFW_VERSION_MAJOR + GLFW_VERSION_MINOR + GLFW_VERSION_REVISION );
        init();
        loop();

        // Free the momory once loop has exit:
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and then free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init() {
        /* Setting up an error callback,
         * creates a printing method that says where error is at:
         */
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize Graphic Library FrameWork:
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW.");
        }

        /* ------------------------------------------------- IMPORTANT before creating window.
         * create hints for version 410 core on M1 mac.
         * on some machines might need to set version to 320 and use forward compat , and core profile
         * version in glsl file might need to be as '#version 320 es' or '#version 320 core' or '#version 420'
         *
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
         */
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);

        /* Create the window,
         * returns a long number, which a place in memory where this window is stored.
         * last two parameters are Monitor, and Sharing. Uses default monitor.
         */
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new RuntimeException("Failed to create the GLFW Window");
        }

        // :: shortcut for lambda function x->(x)
        // mouse listeners from MouseListener helper
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);

        // Keyboard key buttons clicked listeners from KeyListener:
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        // Make the OpenGL context current:
        glfwMakeContextCurrent(glfwWindow);

        // enable v-sync. swap every single frame. Creates uniform Framerate depending on your monitor.
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(glfwWindow);

        // next line is important to make it work!:

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        /* Configure GLFW
         * defines if windows should be visible, etc..
         */
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        Window.changeScene(0);
    }

    public void loop () {
        // time when frame started
        float beginTime = (float)glfwGetTime();
        // time when frame ended
        float endTime; // = Time.getTime();
        float dt = -1.0f;

        while (!glfwWindowShouldClose(glfwWindow)) {
            // Poll events, key events, mouse events:
            glfwPollEvents();

            // colors represent R G B + alpha
            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // update color only when time passes. not initially. lag of 2 frames.
            if (dt >= 0) {
                currentScene.update(dt);
            }

            glfwSwapBuffers(glfwWindow);

            /*
             * endTime gets the time that is right now after all operations in loop are already completed
             * dt is delta time of that time passed
             * beginTime is current time after operation is finished.
             */
            endTime = (float)glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }
}
