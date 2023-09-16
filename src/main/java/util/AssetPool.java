package util;

import components.Sprite;
import components.SpriteSheet;
import renderer.Shader;
import renderer.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {
    // ----------------- Maps with resources : ------------------------
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static Map<String, SpriteSheet> spriteSheets = new HashMap<>();

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
    
    public static void addSpriteSheet(String resourceName, SpriteSheet spriteSheet) {
         File file = new File(resourceName);
         // if we don't have this file, then add it:
         if (!AssetPool.spriteSheets.containsKey(file.getAbsolutePath())) {
            AssetPool.spriteSheets.put(file.getAbsolutePath(), spriteSheet);
         }
    }

    public static SpriteSheet getSpriteSheet(String resourceName) {
        File file = new File(resourceName);
        // if we don't have this file, then add it:
        if (!AssetPool.spriteSheets.containsKey(file.getAbsolutePath())) {
            assert false : "Error: Tried to access spriteSheet that has not been added: " + resourceName;
         }
        return AssetPool.spriteSheets.getOrDefault(file.getAbsolutePath(), null);
    }
}
