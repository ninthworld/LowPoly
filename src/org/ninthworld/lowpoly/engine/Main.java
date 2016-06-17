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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by NinthWorld on 6/2/2016.
 */
public class Main {

    private Loader loader;

    private MasterRenderer masterRenderer;
    private Light light;
    private Camera camera;

    private Fbo normalFbo;
    private Fbo skyboxFbo;
    private Fbo multisampleFbo;
    private Fbo outputFbo;

    private Map<String, RawModel> rawModels;
    private Map<RawModel, List<Entity>> entities;

    private Terrain terrain;

    private TankEntity tankEntity;

    public Main(){
        DisplayManager.createDisplay();
        loader = new Loader();

        light = new Light(new Vector3f(10000, 7000, 14000), new Vector3f(1, 1, 1));
        camera = new Camera(new Vector3f(0, 5, 10));
        masterRenderer = new MasterRenderer(loader, camera);

        rawModels = new HashMap<>();
        try {
            loadModels();
        } catch (IOException e) {
            e.printStackTrace();
        }

        terrain = new Terrain(loader, rawModels);
        entities = new HashMap<>();

        normalFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
        skyboxFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
        multisampleFbo = new Fbo(Display.getWidth(), Display.getHeight());
        outputFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
        PostProcessing.init(loader);

        setup();
    }

    private void loadModels() throws IOException {
        InputStream pinetreeModel = getClass().getResourceAsStream("/models/pinetree.obj");
        InputStream grassModel = getClass().getResourceAsStream("/models/grass.obj");
        InputStream tankHeadModel = getClass().getResourceAsStream("/models/tankHead.obj");
        InputStream tankBodyModel = getClass().getResourceAsStream("/models/tankBody.obj");

        rawModels.put("pineTree", OBJLoader.loadOBJ(loader, pinetreeModel, new Vector3f[]{new Vector3f(61/255f, 40/255f, 9/255f), new Vector3f(76/255f, 110/255f, 33/255f)}));
        rawModels.put("grass", OBJLoader.loadOBJ(loader, grassModel, new Vector3f[]{new Vector3f(112/255f, 161/255f, 49/255f)}));
        rawModels.put("tankHead", OBJLoader.loadOBJ(loader, tankHeadModel, new Vector3f[]{new Vector3f(140/255f, 115/255f, 52/255f), new Vector3f(120/255f, 95/255f, 32/255f)}));
        rawModels.put("tankBody", OBJLoader.loadOBJ(loader, tankBodyModel, new Vector3f[]{new Vector3f(140/255f, 115/255f, 52/255f), new Vector3f(92/255f, 85/255f, 59/255f), new Vector3f(92/255f, 85/255f, 59/255f)}));

    }

    private void setup(){
        Entity tankHead = new Entity(rawModels.get("tankHead"), new Vector3f(0, 0, 0), 0, 0, 0, 2f);
        Entity tankBody = new Entity(rawModels.get("tankBody"), new Vector3f(0, 0, 0), 0, 0, 0, 2f);

        List<Entity> tankHeadEntities = new ArrayList<>();
        tankHeadEntities.add(tankHead);

        List<Entity> tankBodyEntities = new ArrayList<>();
        tankBodyEntities.add(tankBody);

        entities.put(rawModels.get("tankHead"), tankHeadEntities);
        entities.put(rawModels.get("tankBody"), tankBodyEntities);

        tankEntity = new TankEntity(tankHead, tankBody, new Vector3f(0, 0, 0), 0, 0, 0, 1f);

        loop();
    }

    private void loop(){
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

        cleanUp();
    }

    private void cleanUp(){
        skyboxFbo.cleanUp();
        normalFbo.cleanUp();
        multisampleFbo.cleanUp();
        outputFbo.cleanUp();
        PostProcessing.cleanUp();
        masterRenderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }

    public static void main(String[] args){
        System.setProperty("org.lwjgl.librarypath", new File("lib/native/windows").getAbsolutePath());
        new Main();
    }
}