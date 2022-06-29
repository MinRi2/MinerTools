package MinerTools.ui;

import arc.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.world.*;

public class BlockFinder{
    public static final int chunkSize = 32;

    private static final Seq<BlockChunk> returnSeq = new Seq<>();

    public BlockChunk[][] chunks;

    public BlockFinder(){
        Log.info("" + (2 / 32));

        Events.on(WorldLoadEvent.class,  e -> {
            int cols = Vars.world.width() / chunkSize + 1;
            int rows = Vars.world.height() / chunkSize + 1;

            chunks = new BlockChunk[cols][rows];

            for(int cx = 0; cx < cols; cx++){
                for(int cy = 0; cy < rows; cy++){
                    chunks[cx][cy] = new BlockChunk(cx * chunkSize * Vars.tilesize, cy * chunkSize * Vars.tilesize);
                }
            }

            Groups.build.each(this::addBuild);
        });

        Events.on(TilePreChangeEvent.class, event -> {
            Tile tile = event.tile;

            if(tile.build != null && tile.isCenter()){
                removeBuild(tile.build);
            }
        });

        Events.on(TileChangeEvent.class, event -> {
            Tile tile = event.tile;

            if(tile.build != null && tile.isCenter()){
                addBuild(tile.build);
            }
        });
    }

    private void addBuild(Building build){
        getChunk(build).addBuild(build);
    }

    private void removeBuild(Building build){
        getChunk(build).removeBuild(build);
    }

    private BlockChunk getChunk(Position pos){
        int tileX = World.toTile(pos.getX());
        int tileY = World.toTile(pos.getY());

        int ix = tileX / chunkSize;
        int iy = tileY / chunkSize;

        return chunks[ix][iy];
    }

    public Seq<BlockChunk> findBlock(Block block){
        returnSeq.clear();

        for(BlockChunk[] colChunks : chunks){
            for(BlockChunk chunk : colChunks){
                if(chunk.hasBlock(block)){
                    returnSeq.add(chunk);
                }
            }
        }

        return returnSeq;
    }

    public static class BlockChunk{
        public Rect rect;

        public ObjectMap<Block, ObjectSet<Building>> buildings = new ObjectMap<>();

        public BlockChunk(float x, float y){
            rect = new Rect(x, y, chunkSize * Vars.tilesize, chunkSize * Vars.tilesize);
        }

        public void addBuild(Building build){
            buildings.get(build.block, ObjectSet::new).add(build);
        }

        public void removeBuild(Building build){
            ObjectSet<Building> set = buildings.get(build.block);

            if(set == null || set.isEmpty()){
                return;
            }

            set.remove(build);
        }

        public boolean hasBlock(Block block){
            ObjectSet<Building> set = getBlock(block);
            return set != null && !set.isEmpty();
        }

        public ObjectSet<Building> getBlock(Block block){
            return buildings.get(block);
        }

        public Vec2 centerPos(){
            return rect.getCenter(Tmp.v1);
        }

    }

}
