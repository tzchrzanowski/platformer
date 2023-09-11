package domo;

import components.SpriteRenderer;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

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
        this.camera = new Camera(new Vector2f());
        int xOffset = 10;
        int yOffset = 10;
        // total width of square:
        float totalWidth = (float)(600 - xOffset * 2);
        float totalHeight = (float)(300 - xOffset * 2);
        float sizeX = totalWidth / 100.0f;
        float sizeY = totalHeight / 100.0f;

        // creating 10 000 objects for test:
        for (int x=0; x < 100; x++) {
            for (int y=0; y<100; y++) {
                float xPos = xOffset + (x * sizeX);
                float yPos = yOffset + (y * sizeY);

                GameObject go = new GameObject("Obje" + x + "" + y, new Transform(new Vector2f(xPos, yPos), new Vector2f(sizeX, sizeY)));
                go.addComponent(new SpriteRenderer(new Vector4f(xPos / totalWidth, yPos / totalHeight, 1 , 1)));
                this.addGameObjectToScene(go);
            }
        }

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
