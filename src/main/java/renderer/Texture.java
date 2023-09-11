package renderer;

import org.lwjgl.BufferUtils;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {
    private String filePath;
    private int texID;

    public Texture(String filePath) {
        this.filePath = filePath;

        // Generate texture on GPU:
        texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);

        // Set texture parameters:
        // Repeat image in both coordinates
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        // when stretching the image, we want to pixelate: texture min filter, is minimalizing.
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        // when shrinking the image we also want to pixelate:
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // load the image. need RGB data:, 1, 1 are size
        IntBuffer width    = BufferUtils.createIntBuffer(1);
        IntBuffer height   = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        // images initially are loaded in wrong order per vertices. This will make them be loaded from other way. which is correct
        stbi_set_flip_vertically_on_load(true);
        ByteBuffer image   = stbi_load(filePath, width, height, channels, 0);

        if (image != null) {
            if (channels.get(0) == 3) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0),0, GL_RGB, GL_UNSIGNED_BYTE, image);
            } else if (channels.get(0) == 4) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0),0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            } else {
                assert false : "Error: (Texture) Unknown number of channels '" + channels.get(0) + "'";
            }
            freeImageFromMemory(image);
        } else {
            assert false : "Error: (Texture) Could not load image '" + filePath + "'";
        }
  }

  private void freeImageFromMemory(ByteBuffer image) {
      stbi_image_free(image);
  }

  public void bind() {
      glBindTexture(GL_TEXTURE_2D, texID);
  }

  public void unbind() {
      glBindTexture(GL_TEXTURE_2D, 0);
  }
}
