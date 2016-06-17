package org.ninthworld.lowpoly.shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.ninthworld.lowpoly.shaders.ShaderProgram;


public class ShadowShader extends ShaderProgram {

	private static final String VERTEX_FILE = "/shaders/shadow/shadow.vs";
	private static final String FRAGMENT_FILE = "/shaders/shadow/shadow.fs";
	
	private int location_mvpMatrix;

	public ShadowShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
    public void getAllUniformLocations() {
		location_mvpMatrix = super.getUniformLocation("mvpMatrix");
		
	}

    public void loadMvpMatrix(Matrix4f mvpMatrix){
		super.loadMatrix(location_mvpMatrix, mvpMatrix);
	}

	@Override
    public void bindAttributes() {
		super.bindAttribute(0, "in_position");
	}

}
