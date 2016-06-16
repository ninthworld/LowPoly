package org.ninthworld.lowpoly.renderers;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;

import static org.lwjgl.opengl.GL11.glViewport;

/**
 * Created by NinthWorld on 6/2/2016.
 */
public class DisplayManager {

    private static final int        WIDTH   = 1280;
    private static final int        HEIGHT  = 720;
    private static final int        FPS_CAP = 60;
    private static final boolean    VSYNC   = true;
    private static final String     TITLE   = "Simple Geometry";

    public static void createDisplay(){
        try {
            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.setVSyncEnabled(VSYNC);
            // Display.create(new PixelFormat(4, 24, 0, 4));
            //Display.create(new PixelFormat().withSamples(8), new ContextAttribs(3, 3).withForwardCompatible(true).withProfileCore(true));
            Display.create(new PixelFormat(), new ContextAttribs(3, 3).withForwardCompatible(true).withProfileCore(true));
            GL11.glEnable(GL13.GL_MULTISAMPLE);
            Display.setTitle(TITLE);
        } catch(LWJGLException e){
            e.printStackTrace();
            System.exit(0);
        }

        glViewport(0, 0, WIDTH, HEIGHT);
    }

    public static void updateDisplay(){
        Display.sync(FPS_CAP);
        Display.update();
    }

    public static void closeDisplay(){
        Display.destroy();
    }
}
