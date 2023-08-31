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
    private int width;
    private int height;
    private String title;
    private long glfwWindow;

    // only one instance of window:
    private static Window window;

    /* creates only onw Window class with this constructor:
     * Initially set to standard HD definition 1920x1080
     */
    private Window (){
        this.width = 1920;
        this.height = 1080;
        this.title = "Platformer";
    }

    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }
        return Window.window;
    }

    public void run() {
        System.out.println("Konichiwa! LWJGL version: " + Version.getVersion() + "!" );
        init();
        loop();
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

        /* Configure GLFW
         * defines if windows should be visible, etc..
         */
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        /* Create the window,
         * returns a long number, which a place in memory where this window is stored.
         * last two parameters are Monitor, and Sharing. Uses default monitor.
         */
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new RuntimeException("Failed to create the GLFW Window");
        }

        // Make the OpenGL context current:
        glfwMakeContextCurrent(glfwWindow);

        // enable v-sync. swap every single frame.
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

    }

    public void loop () {
//        // This line is critical for LWJGL's interoperation with GLFW's
//        // OpenGL context, or any context that is managed externally.
//        // LWJGL detects the context that is current in the current thread,
//        // creates the GLCapabilities instance and makes the OpenGL
//        // bindings available for use.
//        GL.createCapabilities();

        // colors represent R G B + alpha
        glClearColor(1.0f, 0.0f, 0.0f, 1.0f);

        while (!glfwWindowShouldClose(glfwWindow)) {
            // Poll events, key events, mouse events:
            glfwPollEvents();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            glfwSwapBuffers(glfwWindow);
        }
    }
}
