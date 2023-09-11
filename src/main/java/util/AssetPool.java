package util;

import renderer.Shader;
import renderer.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {
    // ----------------- Maps with resources : ------------------------
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();

    // ----------------------------------------------------------------

    /*
    * returns shader if we already have it in shaders map,
    * if we dont have it, then we are creating new shader and putting it into shader map
    * and then return it.
    * */
    public static Shader getShader(String resourceName) {
        File file = new File(resourceName);
        if(AssetPool.shaders.containsKey(file.getAbsolutePath())) {
            return AssetPool.shaders.get(file.getAbsolutePath());
        } else {
            Shader shader = new Shader(resourceName);
            shader.compile();
            AssetPool.shaders.put(file.getAbsolutePath(), shader);
            return shader;
        }
    }

    public static Texture getTexture(String resoruceName) {
        File file = new File(resoruceName);
        if (AssetPool.textures.containsKey(file.getAbsolutePath())) {
            return AssetPool.textures.get(file.getAbsolutePath());
        } else {
            Texture texture = new Texture(resoruceName);
            AssetPool.textures.put(file.getAbsolutePath(), texture);
            return texture;
        }
    }
}
