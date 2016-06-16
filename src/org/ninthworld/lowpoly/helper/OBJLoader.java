package org.ninthworld.lowpoly.helper;

import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.lowpoly.models.RawModel;
import org.ninthworld.lowpoly.renderers.Loader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NinthWorld on 6/15/2016.
 */
public class OBJLoader {

    public static RawModel loadOBJ(Loader loader, File file, Vector3f[] colorList) throws IOException {
        List<Float> verticesList = new ArrayList<>();
        List<Float> normalsList = new ArrayList<>();
        List<int[][]> indicesList = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        int fCount = -1;
        boolean onF = false;
        while((line = br.readLine()) != null){
            String[] split = line.split(" ");
            if(line.startsWith("v ")){
                onF = false;
                for(int i=1; i<split.length; i++) {
                    verticesList.add(Float.parseFloat(split[i]));
                }
            }else if(line.startsWith("vn ")){
                onF = false;
                for(int i=1; i<split.length; i++) {
                    normalsList.add(Float.parseFloat(split[i]));
                }
            }else if(line.startsWith("f ")){
                if(!onF){
                    fCount++;
                }
                onF = true;
                int[][] intArray = new int[4][3];
                for(int i=1; i<split.length; i++) {
                    String[] split2 = split[i].split("//");
                    intArray[i-1][0] = Integer.parseInt(split2[0]) - 1;
                    intArray[i-1][1] = fCount;
                    intArray[i-1][2] = Integer.parseInt(split2[1]) - 1;
                }
                indicesList.add(intArray);
            }
        }

        List<Integer> fixedIndicesList = new ArrayList<>();
        float[] normals = new float[verticesList.size()];
        float[] colors = new float[verticesList.size()];

        for(int[][] intArray : indicesList){
            for(int i=0; i<intArray.length; i++){
                int vert = intArray[i][0]*3;
                int normal = intArray[i][2]*3;
                normals[vert] = normalsList.get(normal);
                normals[vert+1] = normalsList.get(normal+1);
                normals[vert+2] = normalsList.get(normal+2);

                colors[vert] = colorList[intArray[i][1]%colorList.length].x;
                colors[vert+1] = colorList[intArray[i][1]%colorList.length].y;
                colors[vert+2] = colorList[intArray[i][1]%colorList.length].z;
            }
            fixedIndicesList.add(intArray[0][0]);
            fixedIndicesList.add(intArray[1][0]);
            fixedIndicesList.add(intArray[2][0]);
        }

        float[] vertices = new float[verticesList.size()];
        for(int i=0; i<vertices.length; i++){
            vertices[i] = verticesList.get(i);
        }
        int[] indices = new int[fixedIndicesList.size()];
        for(int i=0; i<indices.length; i++){
            indices[i] = fixedIndicesList.get(i);
        }

        return loader.loadToVao(vertices, colors, normals, indices);
    }
}
