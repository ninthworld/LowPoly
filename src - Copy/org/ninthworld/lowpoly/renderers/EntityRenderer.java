package org.ninthworld.lowpoly.renderers;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.ninthworld.lowpoly.entities.Entity;
import org.ninthworld.lowpoly.helper.MatrixHelper;
import org.ninthworld.lowpoly.models.RawModel;
import org.ninthworld.lowpoly.shaders.MainShader;

import java.util.List;
import java.util.Map;

/**
 * Created by NinthWorld on 6/6/2016.
 */
public class EntityRenderer {

    private MainShader shader;

    public EntityRenderer(MainShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void render(Map<Model, List<Entity>> entities, Matrix4f toShadowSpace) {
        shader.loadToShadowSpaceMatrix(toShadowSpace);

        for (Model model : entities.keySet()) {
            prepareModel(model);
            List<Entity> batch = entities.get(model);
            for (Entity entity : batch) {
                prepareInstance(entity);
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }
            unbindModel();
        }
    }

    private void prepareModel(Model model) {
        RawModel rawModel = model.getRawModel();
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
//        ModelTexture texture = model.getTexture();
//        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
//        GL13.glActiveTexture(GL13.GL_TEXTURE0);
//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
    }

    private void unbindModel() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    private void prepareInstance(Entity entity) {
        Matrix4f transformationMatrix = MatrixHelper.createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
    }
}
