package org.ninthworld.lowpoly.models;

/**
 * Created by NinthWorld on 6/2/2016.
 */
public class RawModel {

    private int vaoID;
    private int vboID;
    private int vertexCount;

    public RawModel(int vaoID, int vboID, int vertexCount){
        this.vaoID = vaoID;
        this.vboID = vboID;
        this.vertexCount = vertexCount;
    }

    public int getVaoID() {
        return vaoID;
    }

    public int getVboID() {
        return vboID;
    }

    public int getVertexCount() {
        return vertexCount;
    }
}
