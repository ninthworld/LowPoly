package org.ninthworld.lowpoly.terrain;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.lowpoly.entities.Entity;
import org.ninthworld.lowpoly.helper.SimplexNoise;
import org.ninthworld.lowpoly.models.RawModel;
import org.ninthworld.lowpoly.renderers.Loader;

import java.util.*;

/**
 * Created by NinthWorld on 6/13/2016.
 */
public class Terrain {

    public Map<String, ChunkEntity> loadedChunks;

    public Terrain(Loader loader, Map<String, RawModel> rawModels){
        this.loadedChunks = new HashMap<>();

        int radius = 12;
        for(int i=0; i<=radius*2; i++){
            for(int j=0; j<=radius*2; j++){
                int x = i - radius, y = j - radius;
                Vector2f chunkPos = new Vector2f(x, y);
                String strChunkPos = ChunkEntity.chunkVecString(x, y);
                ChunkEntity chunkEntity = new ChunkEntity(loader, chunkPos);

                generateTerrainEntities(chunkEntity, rawModels);

                loadedChunks.put(strChunkPos, chunkEntity);
            }
        }
    }

    private void generateTerrainEntities(ChunkEntity chunk, Map<String, RawModel> rawModels){
        long seed = 5555L;

        Random random = new Random(seed++);
        SimplexNoise noise = new SimplexNoise(seed++);
        float frequency = 32f;

        RawModel pineTree = rawModels.get("pineTree");
        List<Entity> pineTreeEntities = new ArrayList<>();
        for(int i=0; i<ChunkEntity.CHUNK_SIZE; i+=4){
            for(int j=0; j<ChunkEntity.CHUNK_SIZE; j+=4){
                float x = (j+chunk.getPosition().x*ChunkEntity.CHUNK_SIZE);
                float y = (i+chunk.getPosition().y*ChunkEntity.CHUNK_SIZE);
                float noiseValue = noise.noise(x/frequency, y/frequency);

                if(noiseValue > 0f && random.nextFloat() + noiseValue > 1.2f){
                    Vector3f entityPos = new Vector3f(x, chunk.getData(i, j) + 2f, y);
                    pineTreeEntities.add(new Entity(pineTree, entityPos, (random.nextFloat()-0.5f)*2f * 10, random.nextFloat()*180, 0, (random.nextFloat()/4f) + 0.75f));
                }
            }
        }


        random = new Random(seed++);
        noise = new SimplexNoise(seed++);
        frequency = 16f;

        RawModel grass = rawModels.get("grass");
        List<Entity> grassEntities = new ArrayList<>();
        for(int i=0; i<ChunkEntity.CHUNK_SIZE; i++){
            for(int j=0; j<ChunkEntity.CHUNK_SIZE; j++){
                float x = (j+chunk.getPosition().x*ChunkEntity.CHUNK_SIZE);
                float y = (i+chunk.getPosition().y*ChunkEntity.CHUNK_SIZE);
                float noiseValue = noise.noise(x/frequency, y/frequency);

                if(noiseValue > 0f && random.nextFloat() + noiseValue > 1.2f){
                    Vector3f entityPos = new Vector3f(x, chunk.getData(i, j) + 0.25f, y);
                    grassEntities.add(new Entity(grass, entityPos, (random.nextFloat()-0.5f)*2f * 10, random.nextFloat()*180, 0, (random.nextFloat()/4f) + 0.75f));
                }
            }
        }

        chunk.entities.put(pineTree, pineTreeEntities);
        chunk.entities.put(grass, grassEntities);
    }

    public float getHeight(float x, float z){
        int chunkX = (int)Math.floor(x/ChunkEntity.CHUNK_SIZE);
        int chunkZ = (int)Math.floor(z/ChunkEntity.CHUNK_SIZE);
        ChunkEntity chunk = loadedChunks.get(ChunkEntity.chunkVecString(chunkX, chunkZ));

        if(chunk != null){
            int localX = (int)((x%ChunkEntity.CHUNK_SIZE)+ChunkEntity.CHUNK_SIZE)%ChunkEntity.CHUNK_SIZE;
            int localZ = (int)((z%ChunkEntity.CHUNK_SIZE)+ChunkEntity.CHUNK_SIZE)%ChunkEntity.CHUNK_SIZE;

            return chunk.getData(localZ, localX);
        }else{
            return 0.0f;
        }
    }
}
