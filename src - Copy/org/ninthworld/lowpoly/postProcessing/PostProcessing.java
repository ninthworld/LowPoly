package org.ninthworld.lowpoly.postProcessing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.ninthworld.lowpoly.models.RawModel;
import org.ninthworld.lowpoly.renderers.Loader;

public class PostProcessing {
	
	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };	
	private static RawModel quad;
	private static SSAOEffect ssaoEffect;

	public static void init(Loader loader){
		quad = loader.loadToVao(POSITIONS);
        ssaoEffect = new SSAOEffect();
	}
	
	public static void doPostProcessing(int colorTexture, int depthTexture, int normalTexture){
		start();
        ssaoEffect.render(colorTexture, depthTexture, normalTexture);
		end();
	}
	
	public static void cleanUp(){
        ssaoEffect.cleanUp();
	}
	
	private static void start(){
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	private static void end(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}



}
