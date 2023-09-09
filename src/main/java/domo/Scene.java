package domo;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {
    protected Camera camera;
    private boolean isRunning = false;
    protected List<GameObject> gameObjects = new ArrayList<>();

    /*
    * game object, physics, everything goes here
    * */
    public Scene() {}

    /*
    * Public init method, that scene can override if they need to.
    * */
    public void init() {}

    public void start() {
        for (GameObject go: gameObjects) {
            go.start();
        }
        isRunning = true;
    }

    public void addGameObjectToScene(GameObject go) {
        if (!isRunning) {
            gameObjects.add(go);
        } else {
            gameObjects.add(go);
            go.start();
        }
    }

    public abstract void update(float dt);
}
