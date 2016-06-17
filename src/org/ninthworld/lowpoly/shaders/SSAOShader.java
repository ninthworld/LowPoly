package org.ninthworld.lowpoly.shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.ninthworld.lowpoly.shaders.ShaderProgram;

public class SSAOShader extends ShaderProgram {

	private static final String VERTEX_FILE = "/shaders/postprocessing/ssao.vs";
	private static final String FRAGMENT_FILE = "/shaders/postprocessing/ssao.fs";

	private int location_colorTexture;
	private int location_depthTexture;
    private int location_normalTexture;
	private int location_skyboxTexture;
	private int[] location_samples;
	private int location_invProjectionMatrix;

	public SSAOShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_colorTexture = super.getUniformLocation("colorTexture");
		location_depthTexture = super.getUniformLocation("depthTexture");
        location_normalTexture = super.getUniformLocation("normalTexture");
		location_skyboxTexture = super.getUniformLocation("skyboxTexture");
		location_invProjectionMatrix = super.getUniformLocation("invProjectionMatrix");
		location_samples = new int[16];
		for(int i=0; i<location_samples.length; i++) {
			location_samples[i] = super.getUniformLocation("samples[" + i + "]");
		}
	}

	public void connectTextureUnits(){
		super.loadInteger(location_colorTexture, 0);
		super.loadInteger(location_depthTexture, 1);
        super.loadInteger(location_normalTexture, 2);
		super.loadInteger(location_skyboxTexture, 3);
	}

	public void loadSamples(Vector2f[] samples){
		for(int i=0; i<samples.length; i++) {
            super.loadVector2f(location_samples[i], samples[i]);
		}
	}

	public void loadInvProjectionMatrix(Matrix4f projection){
		super.loadMatrix(location_invProjectionMatrix, projection);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

}
