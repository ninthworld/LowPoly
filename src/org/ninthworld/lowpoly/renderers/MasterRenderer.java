package org.ninthworld.lowpoly.renderers;

import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.lowpoly.shadows.ShadowBox;
import org.ninthworld.lowpoly.terrain.ChunkEntity;
import org.ninthworld.lowpoly.entities.Camera;
import org.ninthworld.lowpoly.entities.Entity;
import org.ninthworld.lowpoly.helper.MatrixHelper;
import org.ninthworld.lowpoly.lights.Light;
import org.ninthworld.lowpoly.models.RawModel;
import org.ninthworld.lowpoly.shaders.*;
import org.ninthworld.lowpoly.shadows.ShadowMapMasterRenderer;
import org.ninthworld.lowpoly.skybox.SkyboxRenderer;
import org.ninthworld.lowpoly.terrain.Terrain;

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

    public ShadowMapMasterRenderer shadowMapRenderer;
    public SkyboxRenderer skyboxRenderer;

    public MasterRenderer(Loader loader, Camera cam){
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glShadeModel(GL11.GL_FLAT);
        createProjectionMatrix();

        shadowMapRenderer = new ShadowMapMasterRenderer(cam);
        skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);

        mainShader = new MainShader();
        mainShader.start();
        mainShader.loadProjectionMatrix(projectionMatrix);
        mainShader.loadShadowMapSize(shadowMapRenderer.getShadowMapSize());
        mainShader.loadShadowDistance(ShadowBox.SHADOW_DISTANCE);
        mainShader.stop();

        terrainShader = new TerrainShader();
        terrainShader.start();
        terrainShader.loadProjectionMatrix(projectionMatrix);
        terrainShader.loadShadowMapSize(shadowMapRenderer.getShadowMapSize());
        terrainShader.loadShadowDistance(ShadowBox.SHADOW_DISTANCE);
        terrainShader.stop();

        normalShader = new NormalShader();
        normalShader.start();
        normalShader.loadProjectionMatrix(projectionMatrix);
        normalShader.stop();

    }

    public void renderNormals(Map<RawModel, List<Entity>> entities, Terrain terrain, Camera camera){
        prepare();

        normalShader.start();
        normalShader.loadViewMatrix(camera);
        //renderEntities(entities, camera, normalShader);
        //renderTerrain(terrain, camera, normalShader);
        //renderTerrainEntities(terrain, camera, normalShader);
        normalShader.stop();
    }

    public void renderScene(Map<RawModel, List<Entity>> entities, Terrain terrain, Light light, Camera camera){
        prepare();

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, shadowMapRenderer.getShadowMap());

        mainShader.start();
        mainShader.connectTextureUnits();
        mainShader.loadLight(light);
        mainShader.loadViewMatrix(camera);
        mainShader.loadToShadowSpaceMatrix(shadowMapRenderer.getToShadowMapSpaceMatrix());
        renderEntities(entities, camera, mainShader);
        renderTerrainEntities(terrain, camera, mainShader);
        mainShader.stop();

        terrainShader.start();
        terrainShader.connectTextureUnits();
        terrainShader.loadLight(light);
        terrainShader.loadViewMatrix(camera);
        terrainShader.loadToShadowSpaceMatrix(shadowMapRenderer.getToShadowMapSpaceMatrix());
        renderTerrain(terrain, camera, terrainShader);
        terrainShader.stop();
    }

    public void renderShadowMap(Map<RawModel, List<Entity>> entities, Terrain terrain, Light light, Camera camera){
        shadowMapRenderer.start(light);

        // renderTerrain(terrain, camera, shadowMapRenderer.getShadowShader());
        renderEntities(entities, camera, shadowMapRenderer.getShadowShader());
        renderTerrainEntities(terrain, camera, shadowMapRenderer.getShadowShader());

        shadowMapRenderer.finish();
    }

    private void renderTerrain(Terrain terrain, Camera camera, ShaderProgram shader){
        Vector2f camChunkPos = camToChunkPos(camera.getPosition());
        for(ChunkEntity chunk : terrain.loadedChunks.values()) {
            prepareRawModel(chunk.getRawModel());
            prepareChunkEntity(chunk, shader);

            float dist = distance(chunk.getPosition(), camChunkPos);
            if (dist < 4) {
                GL11.glDrawElements(GL11.GL_TRIANGLES, ChunkEntity.numIndices1, GL11.GL_UNSIGNED_INT, 0);
            } else if (dist < 8) {
                GL11.glDrawElements(GL11.GL_TRIANGLES, ChunkEntity.numIndices2, GL11.GL_UNSIGNED_INT, ChunkEntity.numIndices1 * Float.BYTES);
            }
//            } else if (dist < 8){
//                GL11.glDrawElements(GL11.GL_TRIANGLES, ChunkEntity.numIndices3, GL11.GL_UNSIGNED_INT, (ChunkEntity.numIndices1 + ChunkEntity.numIndices2) * Float.BYTES);
//            }

            unbindRawModel();
        }
    }

    private void renderTerrainEntities(Terrain terrain, Camera camera, ShaderProgram shader){
        Vector2f camChunkPos = camToChunkPos(camera.getPosition());
        for(ChunkEntity chunk : terrain.loadedChunks.values()) {
            float dist = distance(chunk.getPosition(), camChunkPos);
            if(dist < 6) {
                renderEntities(chunk.entities, camera, shader);
            }
        }
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
        }else if(shader instanceof ShadowShader){
            Matrix4f mvpMatrix = Matrix4f.mul(shadowMapRenderer.getProjectionViewMatrix(), transformationMatrix, null);
            ((ShadowShader) shader).loadMvpMatrix(mvpMatrix);
        }
    }

    private void prepareChunkEntity(ChunkEntity chunkEntity, ShaderProgram shader) {
        Matrix4f transformationMatrix = MatrixHelper.createTransformationMatrix(new Vector3f(chunkEntity.getPosition().x* ChunkEntity.CHUNK_SIZE, 0, chunkEntity.getPosition().y* ChunkEntity.CHUNK_SIZE), 0, 0, 0, 1f);
        if(shader instanceof MainShader){
            ((MainShader) shader).loadTransformationMatrix(transformationMatrix);
        }else if(shader instanceof TerrainShader){
            ((TerrainShader) shader).loadTransformationMatrix(transformationMatrix);
        }else if(shader instanceof NormalShader){
            ((NormalShader) shader).loadTransformationMatrix(transformationMatrix);
        }else if(shader instanceof ShadowShader){
            Matrix4f mvpMatrix = Matrix4f.mul(shadowMapRenderer.getProjectionViewMatrix(), transformationMatrix, null);
            ((ShadowShader) shader).loadMvpMatrix(mvpMatrix);
        }
    }

    public void cleanUp(){
        mainShader.cleanUp();
        terrainShader.cleanUp();
        normalShader.cleanUp();
        shadowMapRenderer.cleanUp();
        skyboxRenderer.cleanUp();
    }

    public void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(209/255f, 237/255f, 240/255f, 1);
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

    private static float distance(Vector2f pos1, Vector2f pos2){
        return (float) Math.sqrt(Math.pow(pos2.x - pos1.x, 2) + Math.pow(pos2.y - pos1.y, 2));
    }

    private Vector2f camToChunkPos(Vector3f pos){
        return new Vector2f((float) Math.floor(pos.x/(float) ChunkEntity.CHUNK_SIZE), (float) Math.floor(pos.z/(float) ChunkEntity.CHUNK_SIZE));
    }
}
