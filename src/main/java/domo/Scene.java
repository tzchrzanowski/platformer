package domo;

public abstract class Scene {
    protected Camera camera;

    /*
    * game object, physics, everything goes here
    * */
    public Scene() {}

    /*
    * Public init method, that scene can override if they need to.
    * */
    public void init() {}

    public abstract void update(float dt);
}
