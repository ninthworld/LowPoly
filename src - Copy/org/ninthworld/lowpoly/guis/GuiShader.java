package org.ninthworld.lowpoly.guis;

import org.lwjgl.util.vector.Matrix4f;
import org.ninthworld.lowpoly.shaders.ShaderProgram;

/**
 * Created by NinthWorld on 6/7/2016.
 */
public class GuiShader extends ShaderProgram {

    private static final String VERTEX_FILE = "shaders/gui/gui.vs";
    private static final String FRAGMENT_FILE = "shaders/gui/gui.fs";

    private int location_transformationMatrix;

    public GuiShader(){
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes(){
        super.bindAttribute(0, "position");
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
    }

    public void loadTransformationMatrix(Matrix4f matrix){
        super.loadMatrix(location_transformationMatrix, matrix);
    }
}
