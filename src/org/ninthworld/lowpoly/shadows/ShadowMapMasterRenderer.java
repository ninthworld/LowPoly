package org.ninthworld.lowpoly.shadows;

import java.util.List;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.lowpoly.entities.Camera;
import org.ninthworld.lowpoly.entities.Entity;
import org.ninthworld.lowpoly.lights.Light;
import org.ninthworld.lowpoly.shaders.ShadowShader;

/**
 * This class is in charge of using all of the classes in the shadows package to
 * carry out the shadow render pass, i.e. rendering the scene to the shadow map
 * texture. This is the only class in the shadows package which needs to be
 * referenced from outside the shadows package.
 * 
 * @author Karl
 *
 */
public class ShadowMapMasterRenderer {

	private static final int SHADOW_MAP_SIZE = 4096; // 2048;

	private ShadowFrameBuffer shadowFbo;
	private ShadowShader shadowShader;
	private ShadowBox shadowBox;
	private Matrix4f projectionMatrix;
	private Matrix4f lightViewMatrix;
	private Matrix4f projectionViewMatrix;
	private Matrix4f offset = createOffset();

	public ShadowMapMasterRenderer(Camera camera) {
        projectionMatrix = new Matrix4f();
        lightViewMatrix = new Matrix4f();
        projectionViewMatrix = new Matrix4f();

        shadowShader = new ShadowShader();
		shadowBox = new ShadowBox(lightViewMatrix, camera);
		shadowFbo = new ShadowFrameBuffer(SHADOW_MAP_SIZE, SHADOW_MAP_SIZE);
	}

//	public void render(Map<Model, List<Entity>> entities, Light light) {
//		shadowBox.update();
//
//		Vector3f lightDirection = new Vector3f(-light.getPosition().x, -light.getPosition().y, -light.getPosition().z);
//		prepare(lightDirection, shadowBox);
//
//		entityRenderer.render(entities);
//        // terrainSMRenderer.render();
//		finish();
//	}

    public ShadowShader getShadowShader(){
        return shadowShader;
    }

    public Matrix4f getProjectionViewMatrix(){
        return projectionViewMatrix;
    }

	public Matrix4f getToShadowMapSpaceMatrix() {
		return Matrix4f.mul(offset, projectionViewMatrix, null);
	}

	public void cleanUp() {
        shadowShader.cleanUp();
		shadowFbo.cleanUp();
	}

	public int getShadowMap() {
		return shadowFbo.getShadowMap();
	}

    public int getShadowMapSize(){
        return SHADOW_MAP_SIZE;
    }

	protected Matrix4f getLightSpaceTransform() {
		return lightViewMatrix;
	}

	private void prepare(Vector3f lightDirection, ShadowBox box) {
		updateOrthoProjectionMatrix(box.getWidth(), box.getHeight(), box.getLength());
		updateLightViewMatrix(lightDirection, box.getCenter());
		Matrix4f.mul(projectionMatrix, lightViewMatrix, projectionViewMatrix);
		shadowFbo.bindFrameBuffer();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        shadowShader.start();
	}

    public void start(Light light){
        shadowBox.update();
        Vector3f lightDirection = new Vector3f(-light.getPosition().x, -light.getPosition().y, -light.getPosition().z);
		prepare(lightDirection, shadowBox);
    }

	public void finish() {
        shadowShader.stop();
		shadowFbo.unbindFrameBuffer();
	}

	private void updateLightViewMatrix(Vector3f direction, Vector3f center) {
		direction.normalise();
		center.negate();
		lightViewMatrix.setIdentity();
		float pitch = (float) Math.acos(new Vector2f(direction.x, direction.z).length());
		Matrix4f.rotate(pitch, new Vector3f(1, 0, 0), lightViewMatrix, lightViewMatrix);
		float yaw = (float) Math.toDegrees(((float) Math.atan(direction.x / direction.z)));
		yaw = direction.z > 0 ? yaw - 180 : yaw;
		Matrix4f.rotate((float) -Math.toRadians(yaw), new Vector3f(0, 1, 0), lightViewMatrix,
				lightViewMatrix);
		Matrix4f.translate(center, lightViewMatrix, lightViewMatrix);
	}

	private void updateOrthoProjectionMatrix(float width, float height, float length) {
		projectionMatrix.setIdentity();
		projectionMatrix.m00 = 2f / width;
		projectionMatrix.m11 = 2f / height;
		projectionMatrix.m22 = -2f / length;
		projectionMatrix.m33 = 1;
	}

	private static Matrix4f createOffset() {
		Matrix4f offset = new Matrix4f();
		offset.translate(new Vector3f(0.5f, 0.5f, 0.5f));
		offset.scale(new Vector3f(0.5f, 0.5f, 0.5f));
		return offset;
	}
}
