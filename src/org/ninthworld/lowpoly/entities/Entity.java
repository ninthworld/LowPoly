package org.ninthworld.lowpoly.entities;

import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.lowpoly.models.RawModel;

/**
 * Created by NinthWorld on 6/6/2016.
 */
public class Entity {
    private RawModel rawModel;
    private Vector3f position;
    private float rotX, rotY, rotZ;
    private float scale;

    public Entity(RawModel rawModel, Vector3f position, float rotX, float rotY, float rotZ,
                  float scale) {
        this.rawModel = rawModel;
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
    }

    public void increasePosition(float dx, float dy, float dz) {
        this.position.x += dx;
        this.position.y += dy;
        this.position.z += dz;
    }

    public void increaseRotation(float dx, float dy, float dz) {
        this.rotX += dx;
        this.rotY += dy;
        this.rotZ += dz;
    }

    public RawModel getModel() {
        return rawModel;
    }

    public void setModel(RawModel rawModel) {
        this.rawModel = rawModel;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float getRotX() {
        return rotX;
    }

    public void setRotX(float rotX) {
        this.rotX = rotX;
    }

    public float getRotY() {
        return rotY;
    }

    public void setRotY(float rotY) {
        this.rotY = rotY;
    }

    public float getRotZ() {
        return rotZ;
    }

    public void setRotZ(float rotZ) {
        this.rotZ = rotZ;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
