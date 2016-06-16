package org.ninthworld.lowpoly.terrain;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.lowpoly.chunk.ChunkEntity;
import org.ninthworld.lowpoly.entities.Camera;
import org.ninthworld.lowpoly.helper.MatrixHelper;
import org.ninthworld.lowpoly.models.RawModel;
import org.ninthworld.lowpoly.renderers.Loader;
import org.ninthworld.lowpoly.shaders.TerrainShader;

/**
 * Created by NinthWorld on 6/6/2016.
 */
public class TerrainRenderer {

    private TerrainShader shader;

    public TerrainRenderer(Loader loader, TerrainShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;

        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void render(Camera camera, Matrix4f toShadowSpace) {
        if(toShadowSpace != null) {
            shader.loadToShadowSpaceMatrix(toShadowSpace);
        }

        Vector2f camChunkPos = camToChunkPos(camera.getPosition());

        for(ChunkEntity chunk : loadedChunks.values()){
            prepareModel(chunk.getRawModel());
            prepareInstance(chunk);
            float dist = distance(chunk.getPosition(), camChunkPos);
            if(dist < 4){
                GL11.glDrawElements(GL11.GL_TRIANGLES, ChunkEntity.numIndices1, GL11.GL_UNSIGNED_INT, 0);
            }else if(dist < 6){
                GL11.glDrawElements(GL11.GL_TRIANGLES, ChunkEntity.numIndices2, GL11.GL_UNSIGNED_INT, ChunkEntity.numIndices1*Float.BYTES);
            }else{
                GL11.glDrawElements(GL11.GL_TRIANGLES, ChunkEntity.numIndices3, GL11.GL_UNSIGNED_INT, (ChunkEntity.numIndices1+ChunkEntity.numIndices2)*Float.BYTES);
            }
        }

        unbindModel();
    }

    private static float distance(Vector2f pos1, Vector2f pos2){
        return (float) Math.sqrt(Math.pow(pos2.x - pos1.x, 2) + Math.pow(pos2.y - pos1.y, 2));
    }

    private Vector2f camToChunkPos(Vector3f pos){
        return new Vector2f((float) Math.floor(pos.x/(float) ChunkEntity.CHUNK_SIZE), (float) Math.floor(pos.z/(float) ChunkEntity.CHUNK_SIZE));
    }

    private void prepareModel(RawModel rawModel) {
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

    private void prepareInstance(ChunkEntity entity) {
        Matrix4f transformationMatrix = MatrixHelper.createTransformationMatrix(new Vector3f(entity.getPosition().x* ChunkEntity.CHUNK_SIZE, 0, entity.getPosition().y* ChunkEntity.CHUNK_SIZE), 0, 0, 0, 1f);
        shader.loadTransformationMatrix(transformationMatrix);
    }
}
