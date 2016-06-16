package org.ninthworld.lowpoly.terrain;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.lowpoly.chunk.ChunkEntity;
import org.ninthworld.lowpoly.entities.Entity;
import org.ninthworld.lowpoly.models.RawModel;
import org.ninthworld.lowpoly.renderers.Loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

                RawModel pineTree = rawModels.get("pineTree");
                List<Entity> pineTreeEntities = new ArrayList<>();
                pineTreeEntities.add(new Entity(pineTree, new Vector3f(chunkPos.x*ChunkEntity.CHUNK_SIZE + ChunkEntity.CHUNK_SIZE/2f, 10f, chunkPos.x*ChunkEntity.CHUNK_SIZE + ChunkEntity.CHUNK_SIZE/2f), 0, 0, 0, 1));
                chunkEntity.entities.put(pineTree, pineTreeEntities);

                loadedChunks.put(strChunkPos, chunkEntity);
            }
        }
    }
}
