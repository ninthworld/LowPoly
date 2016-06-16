package org.ninthworld.lowpoly.renderers;

import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Matrix4f;
import org.ninthworld.lowpoly.entities.Camera;
import org.ninthworld.lowpoly.entities.Entity;
import org.ninthworld.lowpoly.helper.MatrixHelper;
import org.ninthworld.lowpoly.lights.Light;
import org.ninthworld.lowpoly.models.RawModel;
import org.ninthworld.lowpoly.shaders.MainShader;
import org.ninthworld.lowpoly.shaders.NormalShader;
import org.ninthworld.lowpoly.shaders.ShaderProgram;
import org.ninthworld.lowpoly.shadows.ShadowShader;
import org.ninthworld.lowpoly.terrain.Terrain;
import org.ninthworld.lowpoly.shaders.TerrainShader;

import java.util.List;
import java.util.Map;

/**
 * Created by NinthWorld on 6/6/2016.
 */
public class MasterRenderer {
    public static final float FOV = 70;
    public static final float NEAR_PLANE = 0.1f;
    public static final float FAR_PLANE = 1000;

    private Matrix4f projectionMatrix;

    private MainShader mainShader;
    private TerrainShader terrainShader;
    private NormalShader normalShader;

//    private EntityRenderer renderer;
//    private TerrainRenderer terrainRenderer;
//    private ShadowMapMasterRenderer shadowMapRenderer;

    public MasterRenderer(Loader loader, Camera cam){
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glShadeModel(GL11.GL_FLAT);
        createProjectionMatrix();

        mainShader = new MainShader();
        mainShader.start();
        mainShader.loadProjectionMatrix(projectionMatrix);
        mainShader.stop();

        terrainShader = new TerrainShader();
        terrainShader.start();
        terrainShader.loadProjectionMatrix(projectionMatrix);
        terrainShader.stop();

        normalShader = new NormalShader();
        normalShader.start();
        normalShader.loadProjectionMatrix(projectionMatrix);
        normalShader.stop();

//        renderer = new EntityRenderer(shader, projectionMatrix);
//        terrainRenderer = new TerrainRenderer(loader, terrainShader, projectionMatrix);
//        shadowMapRenderer = new ShadowMapMasterRenderer(cam, terrainRenderer);
    }

    public void renderNormals(Map<RawModel, List<Entity>> entities, Terrain terrain, Camera camera){
        prepare();

        normalShader.start();
        normalShader.loadViewMatrix(camera);
        renderEntities(entities, camera, normalShader);
        renderTerrain(terrain, camera, normalShader);
        renderTerrainEntities(terrain, camera, normalShader);
        normalShader.stop();
    }

    public void renderScene(Map<RawModel, List<Entity>> entities, Terrain terrain, Light light, Camera camera){
        prepare();

        //GL13.glActiveTexture(GL13.GL_TEXTURE0);
        //GL11.glBindTexture(GL11.GL_TEXTURE_2D, getShadowMapTexture());

        mainShader.start();
        mainShader.connectTextureUnits();
        mainShader.loadLight(light);
        mainShader.loadViewMatrix(camera);
        //mainShader.loadToShadowSpaceMatrix(shadowMapRenderer.getToShadowMapSpaceMatrix());
        renderEntities(entities, camera, mainShader);
        renderTerrainEntities(terrain, camera, mainShader);
        mainShader.stop();

        terrainShader.start();
        terrainShader.connectTextureUnits();
        terrainShader.loadLight(light);
        terrainShader.loadViewMatrix(camera);
        //terrainShader.loadToShadowSpaceMatrix(shadowMapRenderer.getToShadowMapSpaceMatrix());
        renderTerrain(terrain, camera, terrainShader);
        terrainShader.stop();
    }

    public void renderShadowMap(Map<RawModel, List<Entity>> entities, Terrain terrain, Light light){

    }

    private void renderTerrain(Terrain terrain, Camera camera, ShaderProgram shader){

    }

    private void renderTerrainEntities(Terrain terrain, Camera camera, ShaderProgram shader){

    }

    private void renderEntities(Map<RawModel, List<Entity>> entities, Camera camera, ShaderProgram shader){
        for(RawModel rawModel : entities.keySet()){
            prepareRawModel(rawModel);

            for(Entity entity : entities.get(rawModel)){
                prepareEntity(entity, shader);
                GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }

            unbindRawModel();
        }
    }

    private void prepareRawModel(RawModel rawModel) {
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
    }

    private void unbindRawModel() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    private void prepareEntity(Entity entity, ShaderProgram shader) {
        Matrix4f transformationMatrix = MatrixHelper.createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
        if(shader instanceof MainShader){
            ((MainShader) shader).loadTransformationMatrix(transformationMatrix);
        }else if(shader instanceof TerrainShader){
            ((TerrainShader) shader).loadTransformationMatrix(transformationMatrix);
        }else if(shader instanceof NormalShader){
            ((NormalShader) shader).loadTransformationMatrix(transformationMatrix);
        }
    }

    private void prepareChunkEntity(Entity entity, ShaderProgram shader) {
        Matrix4f transformationMatrix = MatrixHelper.createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
        if(shader instanceof MainShader){
            ((MainShader) shader).loadTransformationMatrix(transformationMatrix);
        }else if(shader instanceof TerrainShader){
            ((TerrainShader) shader).loadTransformationMatrix(transformationMatrix);
        }else if(shader instanceof NormalShader){
            ((NormalShader) shader).loadTransformationMatrix(transformationMatrix);
        }
    }

    public void cleanUp(){
        mainShader.cleanUp();
        terrainShader.cleanUp();
        normalShader.cleanUp();
    }

//    public void render(List<Entity> entityList, Light sun, Camera camera){
//        for(Entity entity : entityList){
//            processEntity(entity);
//        }
//
//        prepare();
//        terrainShader.start();
//        terrainShader.connectTextureUnits();
//        terrainShader.loadLight(sun);
//        terrainShader.loadViewMatrix(camera);
//        terrainRenderer.render(camera, shadowMapRenderer.getToShadowMapSpaceMatrix());
//        terrainShader.stop();
//
//        shader.start();
//        shader.connectTextureUnits();
//        shader.loadLight(sun);
//        shader.loadViewMatrix(camera);
//        renderer.render(entities, shadowMapRenderer.getToShadowMapSpaceMatrix());
//        shader.stop();
//        entities.clear();
//    }
//
//    public void processEntity(Entity entity){
//        Model entityModel = entity.getModel();
//        List<Entity> batch = entities.get(entityModel);
//        if(batch != null){
//            batch.add(entity);
//        }else{
//            List<Entity> newBatch = new ArrayList<>();
//            newBatch.add(entity);
//            entities.put(entityModel, newBatch);
//        }
//    }
//
//    public void renderShadowMap(List<Entity> entityList, Light sun){
//        for(Entity entity : entityList){
//            processEntity(entity);
//        }
//
//        shadowMapRenderer.render(entities, sun);
//        entities.clear();
//    }

//    public int getShadowMapTexture(){
//        return shadowMapRenderer.getShadowMap();
//    }
//
//    public void cleanUp(){
//        terrainShader.cleanUp();
//        shader.cleanUp();
//        shadowMapRenderer.cleanUp();
//    }

    public void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(209/255f, 237/255f, 240/255f, 1);

//        GL13.glActiveTexture(GL13.GL_TEXTURE0);
//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, getShadowMapTexture());
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
