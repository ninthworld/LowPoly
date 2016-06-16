package org.ninthworld.lowpoly.renderers;

import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Matrix4f;
import org.ninthworld.lowpoly.entities.Camera;
import org.ninthworld.lowpoly.entities.Entity;
import org.ninthworld.lowpoly.helper.MatrixHelper;
import org.ninthworld.lowpoly.models.RawModel;
import org.ninthworld.lowpoly.shaders.NormalShader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by NinthWorld on 6/6/2016.
 */
public class NormalRenderer {
    public static final float FOV = 70;
    public static final float NEAR_PLANE = 0.1f;
    public static final float FAR_PLANE = 1000;

    private Matrix4f projectionMatrix;

    private NormalShader normalShader = new NormalShader();

    private Map<Model, List<Entity>> entities = new HashMap<>();

    public NormalRenderer(){
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        createProjectionMatrix();

        normalShader.start();
        normalShader.loadProjectionMatrix(projectionMatrix);
        normalShader.stop();
    }

    public void render(List<Entity> entityList, Camera camera){
        for(Entity entity : entityList){
            processEntity(entity);
        }

        prepare();
        normalShader.start();
        normalShader.loadViewMatrix(camera);

        for (Model model : entities.keySet()) {
            prepareModel(model);
            List<Entity> batch = entities.get(model);
            for (Entity entity : batch) {
                prepareInstance(entity);
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }
            unbindModel();
        }

        normalShader.stop();
        entities.clear();
    }

    private void prepareModel(Model model) {
        RawModel rawModel = model.getRawModel();
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
    }

    private void unbindModel() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    private void prepareInstance(Entity entity) {
        Matrix4f transformationMatrix = MatrixHelper.createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
        normalShader.loadTransformationMatrix(transformationMatrix);
    }

    public void processEntity(Entity entity){
        Model entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);
        if(batch != null){
            batch.add(entity);
        }else{
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }

    public void cleanUp(){
        normalShader.cleanUp();
    }

    public void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
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
