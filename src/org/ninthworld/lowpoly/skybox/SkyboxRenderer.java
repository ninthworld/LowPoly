package org.ninthworld.lowpoly.skybox;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.ninthworld.lowpoly.entities.Camera;
import org.ninthworld.lowpoly.models.RawModel;
import org.ninthworld.lowpoly.renderers.Loader;
import org.ninthworld.lowpoly.shaders.SkyboxShader;

/**
 * Created by NinthWorld on 6/15/2016.
 */
public class SkyboxRenderer {

    private static final float SIZE = 500f;
    private static final float[] VERTICES = {
            -SIZE,  SIZE, -SIZE,
            -SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

            -SIZE,  SIZE, -SIZE,
            SIZE,  SIZE, -SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
            SIZE, -SIZE,  SIZE
    };

    private static String[] TEXTURE_FILES = {"res/skybox/right.png", "res/skybox/left.png", "res/skybox/top.png", "res/skybox/bottom.png", "res/skybox/back.png", "res/skybox/front.png"};

    private RawModel cube;
    private int texture;
    private SkyboxShader skyboxShader;

    public SkyboxRenderer(Loader loader, Matrix4f projectionMatrix){
        cube = loader.loadToVao(VERTICES, 3);
        texture = loader.loadCubeMap(TEXTURE_FILES);
        skyboxShader = new SkyboxShader();
        skyboxShader.start();
        skyboxShader.loadProjectionMatrix(projectionMatrix);
        skyboxShader.stop();
    }

    public void renderSkybox(Camera camera){
        skyboxShader.start();
        skyboxShader.loadViewMatrix(camera);
        GL30.glBindVertexArray(cube.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getVertexCount());
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        skyboxShader.stop();
    }

    public void cleanUp(){
        skyboxShader.cleanUp();
    }
}
