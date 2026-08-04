package com.afs.integratedMachine.compartment;

import com.afs.integratedMachine.common_registries.IMDataAttachments;
import com.afs.integratedMachine.utils.Utils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;

public class CompartmentRef implements CompartmentGetter{
    private final ResourceKey<Level> dimension;
    private final ChunkPos pos;
    private final int id;
    private boolean broken = false;
    private Level level = null;
    private Compartment cache = null;

    public CompartmentRef(ResourceKey<Level> dimension, BlockPos pos, int id){
        this(dimension, new ChunkPos(pos), id);
    }

    public CompartmentRef(ResourceKey<Level> dimension, ChunkPos pos, int id){
        this.dimension = dimension;
        this.pos = pos;
        this.id = id;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("dimension", dimension.location().toString());
        tag.putInt("id", id);
        tag.putInt("x", pos.x);
        tag.putInt("z", pos.z);
        return tag;
    }

    public static CompartmentRef load(CompoundTag nbt, HolderLookup.Provider registry) {
        int id = nbt.getInt("id");
        ChunkPos pos = new ChunkPos(nbt.getInt("x"), nbt.getInt("z"));
        ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(nbt.getString("dimension")));
        return new CompartmentRef(dimension, pos, id);
    }

    public static final Codec<CompartmentRef> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(e -> e.dimension),
                    Codec.INT.fieldOf("id").forGetter(e -> e.id),
                    Codec.INT.fieldOf("x").forGetter(e -> e.pos.x),
                    Codec.INT.fieldOf("z").forGetter(e -> e.pos.z)
            ).apply(inst, (dim, id, x, z)
                    -> new CompartmentRef(dim, new ChunkPos(x, z), id))
    );

    public static final StreamCodec<ByteBuf, CompartmentRef> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION), e -> e.dimension,
            ByteBufCodecs.INT, e -> e.id,
            ByteBufCodecs.INT, e -> e.pos.x,
            ByteBufCodecs.INT, e -> e.pos.z,
            (dim, id, x, z) -> new CompartmentRef(dim, new ChunkPos(x, z), id)
    );

    public boolean isBroken() {
        return broken;
    }

    @Override
    public Compartment get() {
        if(broken){
            return null;
        }
        if(level == null){
            level = Utils.getLevelByDimension(dimension);
        }
        ChunkAccess chunk = level.getChunk(pos.x, pos.z);
        if(!chunk.hasData(IMDataAttachments.COMPARTMENTS.get())){
            broken = true;
            return null;
        }
        CompartmentList list = chunk.getData(IMDataAttachments.COMPARTMENTS.get());
        if(cache != null && !list.dirty){
            return cache;
        }
        Compartment compartment = list.get(id);
        if(compartment == null){
            broken = true;
            return null;
        }
        cache = compartment;
        return cache;
    }
}
