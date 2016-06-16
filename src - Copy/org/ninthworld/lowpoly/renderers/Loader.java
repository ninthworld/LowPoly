package org.ninthworld.lowpoly.renderers;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.ninthworld.lowpoly.models.RawModel;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NinthWorld on 6/2/2016.
 */
public class Loader {

    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> vbos = new ArrayList<>();

    public RawModel loadToVao(float[] positions, float[] colors, float[] normals, int[] indices){
        int vaoID = createVAO();
        int vboID = bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 3, colors);
        storeDataInAttributeList(2, 3, normals);
        unbindVAO();
        return new RawModel(vaoID, vboID, indices.length);
    }

    public RawModel loadToVao(float[] positions){
        int vaoID = createVAO();
        this.storeDataInAttributeList(0, 2, positions);
        unbindVAO();
        return new RawModel(vaoID, 0, positions.length/2);
    }

    public void cleanRawModel(RawModel model){
        int vao = model.getVaoID();
        int vbo = model.getVboID();

        if(vaos.contains(vao)){
            GL30.glDeleteVertexArrays(vao);
            vaos.remove((Integer) vao);
        }

        if(vbos.contains(vao)){
            GL15.glDeleteBuffers(vbo);
            vbos.remove((Integer) vbo);
        }
    }

    public void cleanUp(){
        for(int vao : vaos){
            GL30.glDeleteVertexArrays(vao);
        }

        for(int vbo : vbos){
            GL15.glDeleteBuffers(vbo);
        }
    }

    private int createVAO(){
        int vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);

        return vaoID;
    }

    private void storeDataInAttributeList(int attributeNumber, int dimesions, float[] data){
        int vboID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, dimesions, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
    }

    private void unbindVAO(){
        GL30.glBindVertexArray(0);
    }

    private int bindIndicesBuffer(int[] indices){
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        return vboID;
    }

    private IntBuffer storeDataInIntBuffer(int[] data){
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private FloatBuffer storeDataInFloatBuffer(float[] data){
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();

        return buffer;
    }
}
