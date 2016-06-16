package org.ninthworld.lowpoly.terrain;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.lowpoly.entities.Entity;
import org.ninthworld.lowpoly.helper.MatrixHelper;
import org.ninthworld.lowpoly.helper.SimplexNoise;
import org.ninthworld.lowpoly.models.RawModel;
import org.ninthworld.lowpoly.renderers.Loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by NinthWorld on 6/7/2016.
 */
public class ChunkEntity {

    public static final int CHUNK_SIZE = 32;

    private RawModel model;
    private Vector2f position;
    private float[][] data;
    public Map<RawModel, List<Entity>> entities;

    public ChunkEntity(Loader loader, Vector2f position) {
        this.position = position;
        this.data = new float[CHUNK_SIZE+1][CHUNK_SIZE+1];
        this.model = null;
        this.entities = new HashMap<>();

        generateChunk();
        generateChunkModel(loader);
    }

    private void generateChunk(){
        long seed = 12345L;
        int octaves = 3;
        float scale = 20;
        float persistance = 4f;
        float lacunarity = 0.3f;

        SimplexNoise[] noise = new SimplexNoise[octaves];
        for(int i=0; i<noise.length; i++){
            noise[i] = new SimplexNoise(seed + i);
        }

        for(int i=0; i<data.length; i++){
            for(int j=0; j<data[i].length; j++){
                float amplitude = 1;
                float frequency = 1;
                float noiseHeight = 0;

                for(int k=0; k<noise.length; k++){
                    float x = ((j+position.x*CHUNK_SIZE)/scale)*frequency;
                    float y = ((i+position.y*CHUNK_SIZE)/scale)*frequency;
                    noiseHeight += noise[k].noise(x, y) * amplitude;

                    amplitude *= persistance;
                    frequency *= lacunarity;
                }

                data[i][j] = noiseHeight;
            }
        }

        SimplexNoise bumpNoise = new SimplexNoise(seed + octaves);
        float amplitude = 0.2f;
        float frequency = 1.8f;
        for(int i=0; i<data.length; i++){
            for(int j=0; j<data[i].length; j++){
                float x = (j+position.x*CHUNK_SIZE)/frequency;
                float y = (i+position.y*CHUNK_SIZE)/frequency;
                data[i][j] += bumpNoise.noise(x, y) * amplitude;
            }
        }
    }

    public void generateChunkModel(Loader loader){
//        Vector3f color1 = new Vector3f(120/255f, 160/255f, 50/255f);
//        Vector3f color2 = new Vector3f(120/255f, 80/255f, 50/255f);
        Vector3f[] colorArray = new Vector3f[]{
                //new Vector3f(77/255f, 162/255f, 199/255f), // Water
                //new Vector3f(242/255f, 219/255f, 117/255f), // Sand
                new Vector3f(132/255f, 161/255f, 69/255f) // Grass
                //new Vector3f(254/255f, 245/255f, 190/255f), // Mountain
        };

        float[] colorHeights = new float[]{
                //2f,
                //16f,
                //128f,
                //1024f
                //16f,
                1024f
        };

        float blendingRange = 0.01f;

        Vector3f[][] colorData = new Vector3f[data.length][data[0].length];
        for(int i=0; i<colorData.length; i++){
            for(int j=0; j<colorData[i].length; j++){
                float dataHeight = data[i][j];

                for(int k=0; k<colorHeights.length; k++){
                    if(dataHeight < colorHeights[k]){
                        if(k > 0 && dataHeight < colorHeights[k-1] + blendingRange){
                            float ratio = (dataHeight - colorHeights[k-1])/blendingRange;
                            colorData[i][j] = MatrixHelper.lerp(colorArray[k-1], colorArray[k], ratio);
                        }else{
                            colorData[i][j] = colorArray[k];
                        }
                        break;
                    }
                }
            }
        }

        float[] vertices = getVertices(data);
        float[] colors = getColors(data, colorData);
        float[] normals = getNormals(data);
        int[] indices = getIndices(data);

        model = loader.loadToVao(vertices, colors, normals, indices);
    }

    public void unloadChunkModel(Loader loader){
        loader.cleanRawModel(model);
    }

    private static Vector3f triangleNormal(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3){
        float ux = x2 - x1, uy = y2 - y1, uz = z2 - z1;
        float vx = x3 - x1, vy = y3 - y1, vz = z3 - z1;
        return new Vector3f(uy*vz-uz*vy, uz*vx-ux*vz, ux*vy-uy*vx);
    }

    private static float[] getVertices(float[][] data){
        List<Float> vertices = new ArrayList<>();

        for(int i=0; i<data.length-1; i++){
            for(int j=0; j<data[0].length; j++){
                float height1 = data[i][j];
                float height2 = data[i+1][j];

                vertices.add((float) j);
                vertices.add(height1);
                vertices.add((float) i);

                vertices.add((float) j);
                vertices.add(height2);
                vertices.add((float) i+1);
            }
        }

        float[] verticesArray = new float[vertices.size()];
        for(int i=0; i<vertices.size(); i++){
            verticesArray[i] = vertices.get(i);
        }

        return verticesArray;
    }

    private static float[] getColors(float[][] data, Vector3f[][] colorData){
        List<Float> colors = new ArrayList<>();

        for(int i=0; i<data.length-1; i++){
            for(int j=0; j<data[0].length; j++){
                if(colorData[i][j] == null){
                    colorData[i][j] = new Vector3f(0, 0, 0);
                }
                colors.add(colorData[i][j].x);
                colors.add(colorData[i][j].y);
                colors.add(colorData[i][j].z);

                if(colorData[i+1][j] == null){
                    colorData[i+1][j] = new Vector3f(0, 0, 0);
                }
                colors.add(colorData[i+1][j].x);
                colors.add(colorData[i+1][j].y);
                colors.add(colorData[i+1][j].z);
            }
        }

        float[] colorsArray = new float[colors.size()];
        for(int i=0; i<colors.size(); i++){
            colorsArray[i] = colors.get(i);
        }

        return colorsArray;
    }

    private static float[] getNormals(float[][] data){
        List<Float> normals = new ArrayList<>();

        for(int i=0; i<data.length-1; i++){
            for(int j=0; j<data[0].length; j++){
                Vector3f normal;
                if(j > 0) {
                    normal = triangleNormal(i - 1, data[i][j - 1], j, i - 1, data[i + 1][j - 1], j + 1, i, data[i][j], j);
                    normals.add(normal.x);
                    normals.add(normal.y);
                    normals.add(normal.z);
                }else{
                    normals.add(0f);
                    normals.add(0f);
                    normals.add(0f);
                }

                if(j < data[0].length-1) {
                    normal = triangleNormal(i + 1, data[i + 1][j + 1], j + 1, i + 1, data[i][j + 1], j, i, data[i + 1][j], j + 1);
                    normals.add(normal.x);
                    normals.add(normal.y);
                    normals.add(normal.z);
                }else{
                    normals.add(0f);
                    normals.add(0f);
                    normals.add(0f);
                }
            }
        }

        float[] normalsArray = new float[normals.size()];
        for(int i=0; i<normals.size(); i++){
            normalsArray[i] = normals.get(i);
        }

        return normalsArray;
    }

    private static int[] getIndices(float[][] data){
        List<Integer> indices = new ArrayList<>();

        for(int i=0; i<data.length-1; i++){
            for(int j=0; j<data[0].length-1; j++){
                indices.add(j*2     + 2*i*data.length);
                indices.add(j*2 + 1 + 2*i*data.length);
                indices.add(j*2 + 2 + 2*i*data.length);

                indices.add(j*2 + 3 + 2*i*data.length);
                indices.add(j*2 + 2 + 2*i*data.length);
                indices.add(j*2 + 1 + 2*i*data.length);
            }
        }

        // ---------------------------
        int dataXLen = (data.length - 1)/2;
        int dataZLen = (data[0].length - 1)/2;
        for(int i=1; i < dataXLen-1; i++) {
            for (int j = 1; j < dataZLen-1; j++) {
                indices.add(j*4 + 0 + 4*i*data[0].length);
                indices.add(j*4 + 1 + 4*i*data[0].length + 2*data[0].length);
                indices.add(j*4 + 4 + 4*i*data[0].length);

                indices.add(j*4 + 5 + 4*i*data[0].length + 2*data[0].length);
                indices.add(j*4 + 4 + 4*i*data[0].length);
                indices.add(j*4 + 1 + 4*i*data[0].length + 2*data[0].length);
            }
        }

        for (int j = 1; j < dataZLen-1; j++) {
            indices.add(j*4 + 0);
            indices.add(j*4 + 1 + 2*data[0].length);
            indices.add(j*4 + 2);

            indices.add(j*4 + 5 + 2*data[0].length);
            indices.add(j*4 + 4);
            indices.add(j*4 + 2);

            indices.add(j*4 + 5 + 2*data[0].length);
            indices.add(j*4 + 2);
            indices.add(j*4 + 1 + 2*data[0].length);
        }

        int offset = 2*data[0].length*(data.length-3);
        for (int j = 1; j < dataZLen-1; j++) {
            indices.add(offset + j*4 + 0);
            indices.add(offset + j*4 + 1 + 2*data[0].length);
            indices.add(offset + j*4 + 3 + 2*data[0].length);

            indices.add(offset + j*4 + 5 + 2*data[0].length);
            indices.add(offset + j*4 + 4);
            indices.add(offset + j*4 + 3 + 2*data[0].length);

            indices.add(offset + j*4 + 0);
            indices.add(offset + j*4 + 3 + 2*data[0].length);
            indices.add(offset + j*4 + 4);
        }

        for (int i = 1; i < dataXLen-1; i++) {
            indices.add(i*data[0].length*4 + 0);
            indices.add(i*data[0].length*4 + 1);
            indices.add(i*data[0].length*4 + 4);

            indices.add(i*data[0].length*4 + 5 + 2*data[0].length);
            indices.add(i*data[0].length*4 + 0 + 2*data[0].length);
            indices.add(i*data[0].length*4 + 1 + 2*data[0].length);

            indices.add(i*data[0].length*4 + 4);
            indices.add(i*data[0].length*4 + 1);
            indices.add(i*data[0].length*4 + 5 + 2*data[0].length);
        }

        offset = data[0].length*2 - 6;
        for (int i = 1; i < dataXLen-1; i++) {
            indices.add(offset + i*data[0].length*4 + 5);
            indices.add(offset + i*data[0].length*4 + 4);
            indices.add(offset + i*data[0].length*4 + 0);

            indices.add(offset + i*data[0].length*4 + 1 + 2*data[0].length);
            indices.add(offset + i*data[0].length*4 + 5 + 2*data[0].length);
            indices.add(offset + i*data[0].length*4 + 4 + 2*data[0].length);

            indices.add(offset + i*data[0].length*4 + 0);
            indices.add(offset + i*data[0].length*4 + 1 + 2*data[0].length);
            indices.add(offset + i*data[0].length*4 + 4 + 2*data[0].length);
        }

        // Top-Left corner
        indices.add(0);
        indices.add(1);
        indices.add(2);

        indices.add(0 + 2*data[0].length);
        indices.add(1 + 2*data[0].length);
        indices.add(5 + 2*data[0].length);

        indices.add(2);
        indices.add(5 + 2*data[0].length);
        indices.add(4);

        indices.add(5 + 2*data[0].length);
        indices.add(2);
        indices.add(1);

        // Top-Right Corner
        offset = data[0].length*2 - 6;
        indices.add(offset + 5);
        indices.add(offset + 4);
        indices.add(offset + 2);

        indices.add(offset + 0);
        indices.add(offset + 1 + 2*data[0].length);
        indices.add(offset + 2);

        indices.add(offset + 5 + 2*data[0].length);
        indices.add(offset + 5);
        indices.add(offset + 1 + 2*data[0].length);

        indices.add(offset + 2);
        indices.add(offset + 1 + 2*data[0].length);
        indices.add(offset + 5);

        // Bottom-Left Corner
        offset = 2*data[0].length*(data.length-3);
        indices.add(offset + 0 + 2*data[0].length);
        indices.add(offset + 1 + 2*data[0].length);
        indices.add(offset + 3 + 2*data[0].length);

        indices.add(offset + 0);
        indices.add(offset + 1);
        indices.add(offset + 4);

        indices.add(offset + 3 + 2*data[0].length);
        indices.add(offset + 5 + 2*data[0].length);
        indices.add(offset + 4);

        indices.add(offset + 0 + 2*data[0].length);
        indices.add(offset + 3 + 2*data[0].length);
        indices.add(offset + 4);

        // Bottom-Right Corner
        offset = 2*data[0].length*(data.length-3) + data[0].length*2 - 6;
        indices.add(offset + 4 + 2*data[0].length);
        indices.add(offset + 3 + 2*data[0].length);
        indices.add(offset + 5 + 2*data[0].length);

        indices.add(offset + 0);
        indices.add(offset + 1 + 2*data[0].length);
        indices.add(offset + 3 + 2*data[0].length);

        indices.add(offset + 0);
        indices.add(offset + 5);
        indices.add(offset + 4);

        indices.add(offset + 3 + 2*data[0].length);
        indices.add(offset + 5);
        indices.add(offset + 0);

        // ---------------------------
        dataXLen = (data.length - 1)/4;
        dataZLen = (data[0].length - 1)/4;
        for(int i=0; i < dataXLen; i++) {
            for (int j = 0; j < dataZLen; j++) {
                indices.add(j*8 + 0 + 8*i*data[0].length);
                indices.add(j*8 + 1 + 8*i*data[0].length + 6*data[0].length);
                indices.add(j*8 + 8 + 8*i*data[0].length);

                indices.add(j*8 + 9 + 8*i*data[0].length + 6*data[0].length);
                indices.add(j*8 + 8 + 8*i*data[0].length);
                indices.add(j*8 + 1 + 8*i*data[0].length + 6*data[0].length);
            }
        }

        int[] indicesArray = new int[indices.size()];
        for(int i=0; i<indices.size(); i++){
            indicesArray[i] = indices.get(i);
        }

        return indicesArray;
    }

    public static final int numIndices1 = CHUNK_SIZE*CHUNK_SIZE*6;
    public static final int numIndices2 = (CHUNK_SIZE/2-2)*(CHUNK_SIZE/2-2)*6 + (CHUNK_SIZE/2 - 2)*9*4 + 3*4*4;
    public static final int numIndices3 = (CHUNK_SIZE/4)*(CHUNK_SIZE/4)*6;

    public float getData(int x, int y){
        return data[x][y];
    }

    public RawModel getRawModel() {
        return model;
    }

    public Vector2f getPosition() {
        return position;
    }

    public static String chunkVecString(float x, float y){
        return Integer.toString((int)x) + "," + Integer.toString((int)y);
    }

}