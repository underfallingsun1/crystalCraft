package com.afs.integratedMachine.compartment;

import com.afs.integratedMachine.common_registries.IMDataAttachments;
import com.afs.integratedMachine.utils.tags.IMBlockTags;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.*;

public class Compartment implements INBTSerializable<CompoundTag> {
    private BlockPos controller;
    private final Set<BlockPos> wall;
    private final Set<BlockPos> corner;
    private final Set<BlockPos> body;

    public Compartment(BlockPos controller, Set<BlockPos> wall, Set<BlockPos> corner, Set<BlockPos> body){
        this.controller = controller;
        this.wall = wall;
        this.corner = corner;
        this.body = body;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putLong("controller", controller.asLong());
        tag.putLongArray("wall", wall.stream().map(BlockPos::asLong).toList());
        tag.putLongArray("corner", corner.stream().map(BlockPos::asLong).toList());
        tag.putLongArray("body", body.stream().map(BlockPos::asLong).toList());
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        controller = BlockPos.of(nbt.getLong("controller"));
        wall.addAll(Arrays.stream(nbt.getLongArray("wall")).mapToObj(BlockPos::of).toList());
        corner.addAll(Arrays.stream(nbt.getLongArray("corner")).mapToObj(BlockPos::of).toList());
        body.addAll(Arrays.stream(nbt.getLongArray("body")).mapToObj(BlockPos::of).toList());
    }

    public static final Codec<Compartment> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    BlockPos.CODEC.fieldOf("controller").forGetter(h -> h.controller),
                    BlockPos.CODEC.listOf().fieldOf("wall").forGetter(h -> List.copyOf(h.wall)),
                    BlockPos.CODEC.listOf().fieldOf("corner").forGetter(h -> List.copyOf(h.corner)),
                    BlockPos.CODEC.listOf().fieldOf("body").forGetter(h -> List.copyOf(h.body))
            ).apply(inst, (c, wall, corner, body) -> new Compartment(c, Set.copyOf(wall), Set.copyOf(corner), Set.copyOf(body)))
    );

    public static final StreamCodec<ByteBuf, Compartment> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, h -> h.controller,
            ByteBufCodecs.collection(HashSet::new, BlockPos.STREAM_CODEC), h -> h.wall,
            ByteBufCodecs.collection(HashSet::new, BlockPos.STREAM_CODEC), h -> h.corner,
            ByteBufCodecs.collection(HashSet::new, BlockPos.STREAM_CODEC), h -> h.body,
            Compartment::new
    );

    public static Optional<Compartment> tryCreateCompartment(Level level, BlockPos controller, BlockPos start, int maxDistance, int maxCount){
        int minY = level.getMinBuildHeight();
        int maxY = minY + level.getHeight();
        if(isWall(level, start) || !isBlockInArea(controller, start, maxDistance, minY, maxY)){
            return Optional.empty();
        }
        Stack<BlockPos> dfsStack = new Stack<>();
        Set<BlockPos> body = new HashSet<>();
        body.add(start);
        Set<BlockPos> wall = new HashSet<>();
        Set<BlockPos> sideBody = new HashSet<>();
        Set<BlockPos> corner = new HashSet<>();
        dfsStack.push(start);
        int visited = 1;
        while(!dfsStack.isEmpty()){
            if(visited > maxCount){
                return Optional.empty();
            }
            BlockPos node = dfsStack.pop();
            int wallContact = 0;
            for(BlockPos pos: getNeighbors(node)){
                if(!isBlockInArea(controller, pos, maxDistance, minY, maxY)){
                    return Optional.empty();
                }
                if(isWall(level, pos)){
                    if(!isWallValid(level, pos, controller)){
                        return Optional.empty();
                    }
                    wall.add(pos);
                    wallContact += 1;
                    if(wallContact >= 2){
                        sideBody.add(node);
                    }
                }
                else if(body.add(pos)){
                    visited ++;
                    dfsStack.push(pos);
                }
            }
        }
        for(BlockPos pos: sideBody){
            for(BlockPos potentialCorner : getCornerPos(pos)){
                if(!wall.contains(potentialCorner) && !body.contains(potentialCorner)){
                    corner.add(potentialCorner);
                }
            }
        }
        return Optional.of(new Compartment(controller, wall, corner, body));
    }

    private static boolean isWallValid(Level level, BlockPos wallPos, BlockPos center){
        return !level.getBlockState(wallPos).is(IMBlockTags.COMPARTMENT_CONTROLLER) || wallPos.equals(center);
    }

    private static boolean isBlockInArea(BlockPos center, BlockPos pos, int distance, int minY, int maxY){
        if (pos.getY() < minY || pos.getY() >= maxY){
            return false;
        }
        return Math.abs(center.getX() - pos.getX()) <= distance &&
                Math.abs(center.getZ() - pos.getZ()) <= distance &&
                Math.abs(center.getY() - pos.getY()) <= distance;
    }

    private static boolean isWall(Level level, BlockPos pos){
        return level.getBlockState(pos).is(IMBlockTags.COMPARTMENT_WALL);
    }

    private static BlockPos[] getNeighbors(BlockPos pos){
        return new BlockPos[]{
                pos.east(), pos.west(), pos.north(), pos.south(), pos.above(), pos.below()
        };
    }

    private static List<BlockPos> getCornerPos(BlockPos pos) {
        List<BlockPos> result = new ArrayList<>(20);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (Math.abs(dx) + Math.abs(dy) + Math.abs(dz) >= 2) {
                        result.add(pos.offset(dx, dy, dz));
                    }
                }
            }
        }
        return result;
    }

    public ContainCondition checkContainCondition(BlockPos pos){
        if(controller.equals(pos)) return ContainCondition.CONTROLLER;
        else if (body.contains(pos)) return ContainCondition.BODY;
        else if (wall.contains(pos)) return ContainCondition.WALL;
        else if (corner.contains(pos)) return ContainCondition.CORNER;
        return ContainCondition.NONE;
    }

    public enum ContainCondition{
        BODY, WALL, CORNER, NONE, CONTROLLER
    }

    public CompartmentRef addToChunk(Level level){
        Set<ChunkAccess> chunks = new HashSet<>();
        ChunkAccess center = level.getChunk(controller);
        CompartmentList list = center.getData(IMDataAttachments.COMPARTMENTS);
        CompartmentEntry entry = list.add(this);
        CompartmentRef ref = new CompartmentRef(level.dimension(), controller, entry.getId());
        for(BlockPos pos: body){
            ChunkAccess access = level.getChunk(pos);
            if(access != center && !chunks.contains(access)){
                chunks.add(access);
                CompartmentList cList = access.getData(IMDataAttachments.COMPARTMENTS);
                cList.addRef(ref);
            }
        }
        for (BlockPos pos: wall){
            ChunkAccess access = level.getChunk(pos);
            if(access != center && !chunks.contains(access)){
                chunks.add(access);
                CompartmentList cList = access.getData(IMDataAttachments.COMPARTMENTS);
                cList.addRef(ref);
            }
        }
        for (BlockPos pos: corner){
            ChunkAccess access = level.getChunk(pos);
            if(access != center && !chunks.contains(access)){
                chunks.add(access);
                CompartmentList cList = access.getData(IMDataAttachments.COMPARTMENTS);
                cList.addRef(ref);
            }
        }
        return ref;
    }

    public void remove(Level level){
        ChunkAccess chunk = level.getChunk(controller);
        CompartmentList list = chunk.getData(IMDataAttachments.COMPARTMENTS);
        list.remove(this);
    }
}
