package org.ninthworld.lowpoly.engine;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.lowpoly.entities.Camera;
import org.ninthworld.lowpoly.entities.Entity;
import org.ninthworld.lowpoly.guis.GuiRenderer;
import org.ninthworld.lowpoly.guis.GuiTexture;
import org.ninthworld.lowpoly.helper.OBJLoader;
import org.ninthworld.lowpoly.lights.Light;
import org.ninthworld.lowpoly.models.RawModel;
import org.ninthworld.lowpoly.postProcessing.Fbo;
import org.ninthworld.lowpoly.postProcessing.PostProcessing;
import org.ninthworld.lowpoly.renderers.*;
import org.ninthworld.lowpoly.terrain.Terrain;

import java.io.File;
import java.io.IOException;
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
        rawModels.put("pineTree", OBJLoader.loadOBJ(loader, new File("models/pinetree.obj")));

        Terrain terrain = new Terrain(loader, rawModels);
        Map<RawModel, List<Entity>> entities = new HashMap<>();

        Light light = new Light(new Vector3f(10000, 8000, 12000), new Vector3f(1, 1, 1));
        Camera camera = new Camera(new Vector3f(0, 5, 10));

        Fbo normalFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
        Fbo multisampleFbo = new Fbo(Display.getWidth(), Display.getHeight());
        Fbo outputFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
        PostProcessing.init(loader);

        MasterRenderer masterRenderer = new MasterRenderer(loader, camera);

        while(!Display.isCloseRequested()){
            camera.move();

            normalFbo.bindFrameBuffer();
            masterRenderer.renderNormals(entities, terrain, camera);
            normalFbo.unbindFrameBuffer();

            masterRenderer.renderShadowMap(entities, terrain, light);

            multisampleFbo.bindFrameBuffer();
            masterRenderer.renderScene(entities, terrain, light, camera);
            multisampleFbo.unbindFrameBuffer();
            multisampleFbo.resolveToFbo(outputFbo);

            PostProcessing.doPostProcessing(outputFbo.getColorTexture(), outputFbo.getDepthTexture(), normalFbo.getColorTexture());


            DisplayManager.updateDisplay();
        }

        normalFbo.cleanUp();
        multisampleFbo.cleanUp();
        outputFbo.cleanUp();
        PostProcessing.cleanUp();

        masterRenderer.cleanUp();

        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}