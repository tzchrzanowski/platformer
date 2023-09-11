package domo;

import components.SpriteRenderer;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

import static org.lwjgl.opengl.GL11.glGetIntegerv;
import static org.lwjgl.opengl.GL20.GL_MAX_TEXTURE_IMAGE_UNITS;

public class LevelEditorScene extends Scene {
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
        this.camera = new Camera(new Vector2f(-250, 0));

        /*
        * add objects to scene, mario and gumbas textures:
        * */
        GameObject obj1 = new GameObject("Mario obj", new Transform(new Vector2f(100, 100), new Vector2f(256, 256)));
        obj1.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/images/mario.png")));
        this.addGameObjectToScene(obj1);

        GameObject obj2 = new GameObject("Gumbas obj", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)));
        obj2.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/images/gumbas.png")));
        this.addGameObjectToScene(obj2);
        // -----------------------------------------------------------

        // getting resources:
        loadResources();
    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");
    }


    /*
    * Starts to use the Shader, and then unbinds it from memory.
    * */
    @Override
    public void update(float dt) {
        // this is where you can trigger moveCamera():
        // moveCamera(dt);

        // log FPS:
        System.out.println("FPS: " + (1.0f / dt));

        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }
        this.renderer.render();
    }

    /*
    * move camera a bit to the side one step at a time.
    * */
    private void moveCamera(float dt) {
        camera.position.y -= dt * 20.0f;
        camera.position.x -= dt * 50.0f;
    }

}
