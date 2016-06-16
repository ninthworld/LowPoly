package org.ninthworld.lowpoly.guis;

import org.lwjgl.util.vector.Vector2f;

/**
 * Created by NinthWorld on 6/7/2016.
 */
public class GuiTexture {

    private int texture;
    private Vector2f position;
    private Vector2f scale;

    public GuiTexture(int texture, Vector2f position, Vector2f scale) {
        this.texture = texture;
        this.position = position;
        this.scale = scale;
    }

    public int getTexture() {
        return texture;
    }

    public Vector2f getPosition() {
        return position;
    }

    public Vector2f getScale() {
        return scale;
    }
}
