package org.ninthworld.lowpoly.engine;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.lowpoly.entities.Camera;
import org.ninthworld.lowpoly.entities.Entity;
import org.ninthworld.lowpoly.entities.TankEntity;
import org.ninthworld.lowpoly.guis.GuiRenderer;
import org.ninthworld.lowpoly.guis.GuiTexture;
import org.ninthworld.lowpoly.helper.MatrixHelper;
import org.ninthworld.lowpoly.helper.OBJLoader;
import org.ninthworld.lowpoly.lights.Light;
import org.ninthworld.lowpoly.models.RawModel;
import org.ninthworld.lowpoly.postProcessing.Fbo;
import org.ninthworld.lowpoly.postProcessing.PostProcessing;
import org.ninthworld.lowpoly.renderers.*;
import org.ninthworld.lowpoly.terrain.ChunkEntity;
import org.ninthworld.lowpoly.terrain.Terrain;

import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by NinthWorld on 6/2/2016.
 */
public class Main {

    public static void main(String[] args) throws IOException {

        DisplayManager.createDisplay();
        Loader loader = new Loader();

        Map<String, RawModel> rawModels = new HashMap<>();
        rawModels.put("pineTree", OBJLoader.loadOBJ(loader, new File("models/pinetree.obj"), new Vector3f[]{new Vector3f(61/255f, 40/255f, 9/255f), new Vector3f(76/255f, 110/255f, 33/255f)}));
        rawModels.put("grass", OBJLoader.loadOBJ(loader, new File("models/grass.obj"), new Vector3f[]{new Vector3f(112/255f, 161/255f, 49/255f)}));

        rawModels.put("tankHead", OBJLoader.loadOBJ(loader, new File("models/tankHead.obj"), new Vector3f[]{new Vector3f(140/255f, 115/255f, 52/255f), new Vector3f(120/255f, 95/255f, 32/255f)}));
        rawModels.put("tankBody", OBJLoader.loadOBJ(loader, new File("models/tankBody.obj"), new Vector3f[]{new Vector3f(140/255f, 115/255f, 52/255f), new Vector3f(92/255f, 85/255f, 59/255f), new Vector3f(92/255f, 85/255f, 59/255f)}));

        Vector3f tankPos = new Vector3f(0, 0, 0);
        Vector3f tankRot = new Vector3f(0, 0, 0);
        Entity tankHead = new Entity(rawModels.get("tankHead"), tankPos, tankRot.x, tankRot.y, tankRot.z, 2f);
        Entity tankBody = new Entity(rawModels.get("tankBody"), tankPos, tankRot.x, tankRot.y, tankRot.z, 2f);

        Terrain terrain = new Terrain(loader, rawModels);
        Map<RawModel, List<Entity>> entities = new HashMap<>();

        List<Entity> tankHeadEntities = new ArrayList<>();
        tankHeadEntities.add(tankHead);

        List<Entity> tankBodyEntities = new ArrayList<>();
        tankBodyEntities.add(tankBody);

        entities.put(rawModels.get("tankHead"), tankHeadEntities);
        entities.put(rawModels.get("tankBody"), tankBodyEntities);

        TankEntity tankEntity = new TankEntity(tankHead, tankBody, new Vector3f(0, 0, 0), 0, 0, 0, 1f);

        Light light = new Light(new Vector3f(10000, 7000, 14000), new Vector3f(1, 1, 1));
        Camera camera = new Camera(new Vector3f(0, 5, 10));

        Fbo normalFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
        Fbo skyboxFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
        Fbo multisampleFbo = new Fbo(Display.getWidth(), Display.getHeight());
        Fbo outputFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
        PostProcessing.init(loader);

        MasterRenderer masterRenderer = new MasterRenderer(loader, camera);

        while(!Display.isCloseRequested()){

            tankEntity.move(terrain, camera);
            // camera.move();

            normalFbo.bindFrameBuffer();
            masterRenderer.renderNormals(entities, terrain, camera);
            normalFbo.unbindFrameBuffer();

            masterRenderer.renderShadowMap(entities, terrain, light, camera);

            skyboxFbo.bindFrameBuffer();
            masterRenderer.prepare();
            masterRenderer.skyboxRenderer.renderSkybox(camera);
            skyboxFbo.unbindFrameBuffer();

            multisampleFbo.bindFrameBuffer();
            masterRenderer.renderScene(entities, terrain, light, camera);
            multisampleFbo.unbindFrameBuffer();

            multisampleFbo.resolveToFbo(outputFbo);
            PostProcessing.doPostProcessing(outputFbo.getColorTexture(), outputFbo.getDepthTexture(), normalFbo.getColorTexture(), skyboxFbo.getColorTexture());

            DisplayManager.updateDisplay();
        }

        skyboxFbo.cleanUp();
        normalFbo.cleanUp();
        multisampleFbo.cleanUp();
        outputFbo.cleanUp();
        PostProcessing.cleanUp();

        masterRenderer.cleanUp();

        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}