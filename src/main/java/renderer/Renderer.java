package renderer;

import components.SpriteRenderer;
import domo.GameObject;

import java.util.ArrayList;
import java.util.List;

/*
* Renders all the objects.
* */
public class Renderer {
    // ------------------------ STATIC PROPERTIES ----------------------------
    private final int MAX_BATCH_SIZE = 1000; // batching more items together increases FPS. Making it smaller will reduce FPS alot
    // -----------------------------------------------------------------------
    // ------------------------ DYNAMIC PROPERTIES ----------------------------
    private List<RenderBatch> batches;
    // -----------------------------------------------------------------------

    public Renderer() {
        this.batches = new ArrayList<>();
    }

    public void add(GameObject go) {
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        if(spr!= null) {
            add(spr);
        }
    }

    public void add(SpriteRenderer spr) {
        boolean added = false;
        for (RenderBatch batch : batches) {
            if (batch.hasRoom()) {
                batch.addSprite(spr);
                added = true;
                break;
            }
        }

        if(!added) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE);
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(spr);
        }
    }

    public void render() {
        for (RenderBatch batch: batches) {
            batch.render();
        }
    }
}
