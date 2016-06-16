package org.ninthworld.lowpoly.guis;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.ninthworld.lowpoly.helper.MatrixHelper;
import org.ninthworld.lowpoly.models.RawModel;
import org.ninthworld.lowpoly.renderers.Loader;

import java.util.List;

/**
 * Created by NinthWorld on 6/7/2016.
 */
public class GuiRenderer {

    private final RawModel quad;

    private GuiShader shader;

    public GuiRenderer(Loader loader){
        quad = loader.loadToVao(new float[]{-1, 1, -1, -1, 1, 1, 1, -1});
        shader = new GuiShader();
    }

    public void render(List<GuiTexture> guis){
        shader.start();

        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);

        for(GuiTexture gui : guis){
            Matrix4f matrix = MatrixHelper.createTransformationMatrix(gui.getPosition(), gui.getScale());
            shader.loadTransformationMatrix(matrix);

            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTexture());

            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
        }

        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);

        shader.stop();
    }

    public void cleanUp(){
        shader.cleanUp();
    }
}
