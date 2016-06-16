package org.ninthworld.lowpoly.shadows;

import org.lwjgl.util.vector.Matrix4f;
import org.ninthworld.lowpoly.shaders.ShaderProgram;


public class ShadowShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "shaders/shadow/shadow.vs";
	private static final String FRAGMENT_FILE = "shaders/shadow/shadow.fs";
	
	private int location_mvpMatrix;

	protected ShadowShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_mvpMatrix = super.getUniformLocation("mvpMatrix");
		
	}
	
	protected void loadMvpMatrix(Matrix4f mvpMatrix){
		super.loadMatrix(location_mvpMatrix, mvpMatrix);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "in_position");
	}

}
