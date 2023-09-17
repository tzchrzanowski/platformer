package domo;

import components.Sprite;
import components.SpriteRenderer;
import components.SpriteSheet;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

import static org.lwjgl.opengl.GL11.glGetIntegerv;
import static org.lwjgl.opengl.GL20.GL_MAX_TEXTURE_IMAGE_UNITS;

public class LevelEditorScene extends Scene {

    private GameObject obj1;
    private SpriteSheet sprites;

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
        // getting resources:
        loadResources();

        // set up camera position:
        this.camera = new Camera(new Vector2f(-250, 0));

        this.sprites = AssetPool.getSpriteSheet("assets/Images/spritesheet.png");
        /*
        * add objects to scene, mario and gumbas textures:
        * */
        obj1 = new GameObject("Mario obj", new Transform(new Vector2f(100, 100), new Vector2f(256, 256)));
        obj1.addComponent(new SpriteRenderer(sprites.getSprite(0)));
        this.addGameObjectToScene(obj1);

        GameObject obj2 = new GameObject("Gumbas obj", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)));
        obj2.addComponent(new SpriteRenderer(sprites.getSprite(10)));
        this.addGameObjectToScene(obj2);
        // -----------------------------------------------------------
    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");

        AssetPool.addSpriteSheet("assets/Images/spritesheet.png",
                new SpriteSheet(AssetPool.getTexture("assets/Images/spritesheet.png"), 16, 16, 26, 0));
    }

    private int spriteIndex = 0;
    private float spriteFLipTime = 0.2f;
    private float spriteFlipTimeLeft = 0.0f;
    /*
    * Starts to use the Shader, and then unbinds it from memory.
    * */
    @Override
    public void update(float dt) {
        /*
        * this is where you can trigger moveCamera():
        * *
        // moveCamera(dt);

        /*
         * log FPS:
         */
        // System.out.println("FPS: " + (1.0f / dt));

        /*
        * change animation of single texture:
        * */
        spriteFlipTimeLeft -= dt;
        if(spriteFlipTimeLeft <= 0) {
            spriteFlipTimeLeft = spriteFLipTime;
            spriteIndex++;
            if (spriteIndex > 4) {
                spriteIndex = 0;
            }
            obj1.getComponent(SpriteRenderer.class).setSprite(sprites.getSprite(spriteIndex ));
        }

        for ( GameObject go : this.gameObjects) {
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
