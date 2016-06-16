package org.ninthworld.lowpoly.entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.lowpoly.helper.MatrixHelper;
import org.ninthworld.lowpoly.terrain.Terrain;

/**
 * Created by NinthWorld on 6/16/2016.
 */
public class TankEntity extends Entity {

    public Entity tankHead;
    public Entity tankBody;
    public Entity tankHead_loaded;
    public Entity tankBody_loaded;

    public TankEntity(Entity tankHead, Entity tankBody, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(null, position, rotX, rotY, rotZ, scale);
        this.tankBody = new Entity(null, MatrixHelper.multiply(position, 1), rotX, rotY, rotZ, scale);
        this.tankHead = new Entity(null, MatrixHelper.multiply(position, 1), rotX, rotY, rotZ, scale);
        this.tankBody_loaded = tankBody;
        this.tankHead_loaded = tankHead;
    }

    public void updateParts(){
        tankHead_loaded.setPosition(Vector3f.add(this.getPosition(), tankHead.getPosition(), null));
//        tankHead_loaded.setRotX(this.getRotX() + tankHead.getRotX());
//        tankHead_loaded.setRotY(this.getRotY() + tankHead.getRotY());
//        tankHead_loaded.setRotZ(this.getRotZ() + tankHead.getRotZ());
        tankHead_loaded.setRotX(this.getRotX() + tankHead.getRotX());
        tankHead_loaded.setRotY(tankHead.getRotY());
        tankHead_loaded.setRotZ(this.getRotZ() + tankHead.getRotZ());

        tankBody_loaded.setPosition(Vector3f.add(this.getPosition(), tankBody.getPosition(), null));
        tankBody_loaded.setRotX(this.getRotX() + tankBody.getRotX());
        tankBody_loaded.setRotY(this.getRotY() + tankBody.getRotY());
        tankBody_loaded.setRotZ(this.getRotZ() + tankBody.getRotZ());
    }

    private static float maxLook = 85;
    private static float mouseSensitivity = 0.08f;
    private static float rotSpeed = 1.4f;
    private static float moveSpeed = 0.8f;
    private static float camDist = 32f;
    public void move(Terrain terrain, Camera camera){
        if(Keyboard.isKeyDown(Keyboard.KEY_A)){
            increaseRotation(0, rotSpeed, 0);
        }else if(Keyboard.isKeyDown(Keyboard.KEY_D)){
            increaseRotation(0, -rotSpeed, 0);
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_W)){
            increasePosition((float) Math.sin(Math.PI*getRotY()/180f)*moveSpeed, 0, (float) Math.cos(Math.PI*getRotY()/180f)*moveSpeed);
        }else if(Keyboard.isKeyDown(Keyboard.KEY_S)){
            increasePosition((float) -Math.sin(Math.PI*getRotY()/180f)*moveSpeed, 0, (float) -Math.cos(Math.PI*getRotY()/180f)*moveSpeed);
        }

        int radius = 5;
        float height = 0;
        for(int i=0; i<=radius*2; i++){
            for(int j=0; j<=radius*2; j++){
                int x = i - radius;
                int z = j - radius;
                height += terrain.getHeight(getPosition().x + x, getPosition().z + z);
            }
        }
        height /= (float) Math.pow(radius*2 + 1, 2);
        getPosition().setY(height);

        Vector3f vec1 = new Vector3f(getPosition().x + 2, terrain.getHeight(getPosition().x + 2, getPosition().z + 2), getPosition().z + 2);
        Vector3f vec2 = new Vector3f(getPosition().x - 2, terrain.getHeight(getPosition().x - 2, getPosition().z + 2), getPosition().z + 2);
        Vector3f vec3 = new Vector3f(getPosition().x - 2, terrain.getHeight(getPosition().x - 2, getPosition().z - 2), getPosition().z - 2);

        Vector3f normal = MatrixHelper.triangleNormal(vec1, vec2, vec3);
        setRotX(normal.x*2);
        setRotZ(-normal.z*2);

        if(Mouse.isInsideWindow() && Mouse.isButtonDown(0)) {
            Mouse.setGrabbed(true);
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            Mouse.setGrabbed(false);
        }

        if(Mouse.isGrabbed()) {
            float mouseDX = Mouse.getDX();
            float mouseDY = -Mouse.getDY();
            tankHead.increaseRotation(0, -mouseDX * mouseSensitivity, 0);

            float pitch = camera.getPitch();
            pitch += mouseDY * mouseSensitivity;
            pitch = Math.max(-maxLook, Math.min(maxLook, pitch));
            camera.setPitch(pitch);
        }

        camera.getPosition().set((float) -Math.sin(Math.PI*tankHead.getRotY()/180f)*camDist + getPosition().x, getPosition().y + camera.getPitch()/2f + camDist/4f, (float) -Math.cos(Math.PI*tankHead.getRotY()/180f)*camDist + getPosition().z);
        camera.setYaw(-tankHead.getRotY() - 180);


        updateParts();
    }
}
