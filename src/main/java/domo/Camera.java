package domo;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {
    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;
    public Vector2f position; // we need to know where it is in the world.

    public Camera(Vector2f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        adjustProjection();
    }

    /*
     * screen size is defined here:
     * operates on units, not pixels
     */
    private void setupScreenSizeForCamera() {
        projectionMatrix.ortho(0.0f, 32.0f * 40.0f, 0.0f, 32.0f * 21.0f, 0.0f, 100.0f);
    }

    /*
    * Adjust camera projection, to be either 3d or 2d.
    * Ortographic, or Perspective.
    * */
    public void adjustProjection() {
        // sensitive bug method. returns identity matrix.
        projectionMatrix.identity();
        this.setupScreenSizeForCamera();
    }

    /*
    * Defines where camera is in the world
    * vector3f operatez on x, y, z coordinates
    * Camera is looking at negative 1 in the Z direction , z-front.
    * */
    public Matrix4f getViewMatrix() {
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        this.viewMatrix.identity();

        // where the camera is located at:
        viewMatrix = viewMatrix.lookAt(
                    new Vector3f(position.x, position.y, 20.0f), // camera is located here in a world space
                    cameraFront.add(position.x, position.y, 0.0f), // where camera is looking at, where is the center
                    cameraUp // which direction is UP
                );
        return this.viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }
}
