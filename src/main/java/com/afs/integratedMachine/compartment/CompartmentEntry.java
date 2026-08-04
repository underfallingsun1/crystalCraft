package com.afs.integratedMachine.compartment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashSet;

public class CompartmentEntry implements CompartmentGetter{
    private final Compartment compartment;
    private final int id;

    @Override
    public Compartment get() {
        return compartment;
    }

    public CompartmentEntry(Compartment compartment, int id){
        this.compartment = compartment;
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public static final Codec<CompartmentEntry> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                Compartment.CODEC.fieldOf("compartment").forGetter(e -> e.compartment),
                Codec.INT.fieldOf("id").forGetter(e -> e.id)
            ).apply(inst, CompartmentEntry::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, CompartmentEntry> STREAM_CODEC =
            StreamCodec.composite(
                    Compartment.STREAM_CODEC, e -> e.compartment,
                    ByteBufCodecs.INT, e -> e.id,
                    CompartmentEntry::new
            );

    public CompoundTag save(HolderLookup.Provider registry){
        CompoundTag tag = new CompoundTag();
        tag.put("compartment", compartment.serializeNBT(registry));
        tag.putInt("id", id);
        return tag;
    }

    public static CompartmentEntry load(CompoundTag tag, HolderLookup.Provider registry){
        int id = tag.getInt("id");
        CompoundTag compartmentTag = tag.getCompound("compartment");
        Compartment compartment = new Compartment(null, new HashSet<>(), new HashSet<>(), new HashSet<>());
        compartment.deserializeNBT(registry, compartmentTag);
        return new CompartmentEntry(compartment, id);
    }
}
