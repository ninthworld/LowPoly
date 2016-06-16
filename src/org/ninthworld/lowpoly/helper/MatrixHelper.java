package org.ninthworld.lowpoly.helper;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.lowpoly.entities.Camera;

/**
 * Created by NinthWorld on 6/3/2016.
 */
public class MatrixHelper {

    public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        Matrix4f.translate(translation, matrix, matrix);
        Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
        return matrix;
    }

    public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        Matrix4f.translate(translation, matrix, matrix);
        Matrix4f.scale(new Vector3f(scale,scale,scale), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1,0,0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0,1,0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0,0,1), matrix, matrix);
        return matrix;
    }

    public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.setIdentity();
        Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
        Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
        Vector3f cameraPos = camera.getPosition();
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
        Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
        return viewMatrix;
    }

    public static Vector3f lerp(Vector3f v1, Vector3f v2, float ratio){
        Vector3f a = multiply(v1, 1f-ratio);
        Vector3f b = multiply(v2, ratio);
        return Vector3f.add(a, b, null);
    }

    public static Vector3f multiply(Vector3f src, float val){
        return new Vector3f(src.x * val, src.y * val, src.z * val);
    }

    public static Vector3f divide(Vector3f src, float val){
        return new Vector3f(src.x/val, src.y/val, src.z/val);
    }

    public static Vector3f triangleNormal(Vector3f v1, Vector3f v2, Vector3f v3){
        float ux = v2.x - v1.x, uy = v2.y - v1.y, uz = v2.z - v1.z;
        float vx = v3.x - v1.x, vy = v3.y - v1.y, vz = v3.z - v1.z;
        return new Vector3f(uy*vz-uz*vy, uz*vx-ux*vz, ux*vy-uy*vx);
    }
}
