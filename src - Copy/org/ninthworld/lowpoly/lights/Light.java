package org.ninthworld.lowpoly.lights;

import org.lwjgl.util.vector.Vector3f;

/**
 * Created by NinthWorld on 6/3/2016.
 */
public class Light {

    private Vector3f position;
    private Vector3f color;

    public Light(Vector3f position, Vector3f color){
        this.position = position;
        this.color = color;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position){
        this.position = position;
    }

    public Vector3f getColor() {
        return color;
    }
}
