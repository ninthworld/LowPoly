package org.ninthworld.lowpoly.helper;

import java.nio.ByteBuffer;

/**
 * Created by NinthWorld on 6/15/2016.
 */
public class TextureData {

    private int width;
    private int height;
    private ByteBuffer buffer;

    public TextureData(ByteBuffer buffer, int width, int height){
        this.buffer = buffer;
        this.width = width;
        this.height = height;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public ByteBuffer getBuffer(){
        return buffer;
    }

}