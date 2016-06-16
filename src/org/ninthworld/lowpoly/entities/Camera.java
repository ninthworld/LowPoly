package org.ninthworld.lowpoly.entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by NinthWorld on 6/3/2016.
 */
public class Camera {

    private static float maxLook = 85;
    private static float mouseSensitivity = 0.08f;

    private Vector3f position;
    private float pitch = 30;
    private float yaw;
    private float roll;

    public Camera(Vector3f position){
        this.position = position;
    }

    public void move(){
        float speed = 0.8f;
        if(Keyboard.isKeyDown(Keyboard.KEY_W)){
            // position.z -= speed;
            position.x += Math.sin(Math.toRadians(yaw)) * speed;
            position.z -= Math.cos(Math.toRadians(yaw)) * speed;
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_S)){
            // position.z += speed;
            position.x -= Math.sin(Math.toRadians(yaw)) * speed;
            position.z += Math.cos(Math.toRadians(yaw)) * speed;
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_D)){
            // position.x += speed;
            position.x += Math.sin(Math.toRadians(yaw + 90)) * speed;
            position.z -= Math.cos(Math.toRadians(yaw + 90)) * speed;
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_A)){
            // position.x -= speed;
            position.x += Math.sin(Math.toRadians(yaw - 90)) * speed;
            position.z -= Math.cos(Math.toRadians(yaw - 90)) * speed;
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
            position.y += speed;
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
            position.y -= speed;
        }

        if(Mouse.isInsideWindow() && Mouse.isButtonDown(0)) {
            Mouse.setGrabbed(true);
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            Mouse.setGrabbed(false);
        }

        if(Mouse.isGrabbed()) {
            float mouseDX = Mouse.getDX();
            float mouseDY = -Mouse.getDY();
            yaw += mouseDX * mouseSensitivity;
            pitch += mouseDY * mouseSensitivity;
            pitch = Math.max(-maxLook, Math.min(maxLook, pitch));
        }

    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch){
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw){
        this.yaw = yaw;
    }

    public float getRoll() {
        return roll;
    }
}