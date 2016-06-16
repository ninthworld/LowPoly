package org.ninthworld.lowpoly.shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.ninthworld.lowpoly.entities.Camera;
import org.ninthworld.lowpoly.helper.MatrixHelper;
import org.ninthworld.lowpoly.lights.Light;
import org.ninthworld.lowpoly.shaders.ShaderProgram;

/**
 * Created by NinthWorld on 6/3/2016.
 */
public class TerrainShader extends ShaderProgram {

    private static final String VERTEX_FILE = "shaders/terrain/terrain2.vs";
    private static final String FRAGMENT_FILE = "shaders/terrain/terrain2.fs";

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;

    private int location_lightPosition;
    private int location_lightColor;

    private int location_toShadowMapSpace;
    private int location_shadowMap;

    public TerrainShader(){
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes(){
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "color");
        super.bindAttribute(2, "normal");
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");

        location_lightPosition = super.getUniformLocation("lightPosition");
        location_lightColor = super.getUniformLocation("lightColor");

        location_toShadowMapSpace = super.getUniformLocation("toShadowMapSpace");
        location_shadowMap = super.getUniformLocation("shadowMap");
    }

    public void loadToShadowSpaceMatrix(Matrix4f matrix){
        super.loadMatrix(location_toShadowMapSpace, matrix);
    }

    public void connectTextureUnits(){
        super.loadInteger(location_shadowMap, 0);
    }

    public void loadLight(Light light){
        super.loadVector3f(location_lightPosition, light.getPosition());
        super.loadVector3f(location_lightColor, light.getColor());
    }

    public void loadTransformationMatrix(Matrix4f matrix){
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    public void loadViewMatrix(Camera camera){
        Matrix4f viewMatrix = MatrixHelper.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }

    public void loadProjectionMatrix(Matrix4f projection){
        super.loadMatrix(location_projectionMatrix, projection);
    }
}
