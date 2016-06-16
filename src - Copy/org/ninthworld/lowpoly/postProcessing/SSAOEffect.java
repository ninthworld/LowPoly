package org.ninthworld.lowpoly.postProcessing;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

/**
 * Created by NinthWorld on 6/7/2016.
 */
public class SSAOEffect {
    public static final float FOV = 70;
    public static final float NEAR_PLANE = 0.1f;
    public static final float FAR_PLANE = 1000;
    private Matrix4f projectionMatrix;

    private ImageRenderer renderer;
    private SSAOShader shader;

    private Vector2f[] samples;
    private Matrix4f invProjectionMatrix;

    public SSAOEffect() {
        shader = new SSAOShader();
        renderer = new ImageRenderer();
        createProjectionMatrix();
        invProjectionMatrix = Matrix4f.invert(projectionMatrix, invProjectionMatrix);


        float[] sampleValues = new float[]{
                -0.94201624f, -0.39906216f,
                0.94558609f, -0.76890725f,
                -0.09418410f, -0.92938870f,
                0.34495938f, 0.29387760f,
                -0.91588581f, 0.45771432f,
                -0.81544232f, -0.87912464f,
                -0.38277543f, 0.27676845f,
                0.97484398f, 0.75648379f,
                0.44323325f, -0.97511554f,
                0.53742981f, -0.47373420f,
                -0.26496911f, -0.41893023f,
                0.79197514f, 0.19090188f,
                -0.24188840f, 0.99706507f,
                -0.81409955f, 0.91437590f,
                0.19984126f, 0.78641367f,
                0.14383161f, -0.14100790f
        };

        samples = new Vector2f[16];
        for(int i=0; i<samples.length; i++){
            samples[i] = new Vector2f(sampleValues[i*2], sampleValues[i*2+1]);
        }
    }

    public void render(int colorTexture, int depthTexture, int normalTexture){
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture);

        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture);

        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, normalTexture);

        shader.start();
        shader.connectTextureUnits();
        shader.loadInvProjectionMatrix(invProjectionMatrix);
        shader.loadSamples(samples);
        renderer.renderQuad();
        shader.stop();
    }

    public void cleanUp(){
        shader.cleanUp();
        renderer.cleanUp();
    }

    private void createProjectionMatrix() {
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;
    }
}
