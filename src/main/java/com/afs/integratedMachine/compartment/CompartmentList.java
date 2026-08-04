package com.afs.integratedMachine.compartment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.List;

public class CompartmentList implements INBTSerializable<CompoundTag> {
    private int currentId;
    private final List<CompartmentEntry> compartments;
    private final List<CompartmentRef> ref;
    private final List<Compartment> compartmentsCache = new ArrayList<>();
    public boolean dirty = true;

    public CompartmentList(int beginId, List<CompartmentEntry> compartments, List<CompartmentRef> ref){
        this.currentId = beginId;
        this.compartments = compartments;
        this.ref = ref;
    }

    public CompartmentList(){
        this(0, new ArrayList<>(), new ArrayList<>());
    }

    public CompartmentEntry add(Compartment compartment){
        CompartmentEntry entry = new CompartmentEntry(compartment, currentId);
        currentId += 1;
        compartments.add(entry);
        dirty = true;
        return entry;
    }

    public Compartment get(int id){
        int index = find(id);
        if(index == -1) return null;
        else return compartments.get(index).get();
    }

    public Compartment remove(int id){
        int index = find(id);
        if(index == -1) return null;
        else {
            dirty = true;
            return compartments.remove(index).get();
        }
    }

    public void remove(Compartment compartment){
        for(CompartmentEntry entry: compartments){
            if(entry.get() == compartment){
                compartments.remove(entry);
                return;
            }
        }
    }

    public int find(int id){
        int min = 0, max = compartments.size(), mid;
        while(min <= max){
            mid = (max + min) / 2;
            int midId = compartments.get(mid).getId();
            if(midId < id) min = mid + 1;
            else if(midId > id) max = mid - 1;
            else return mid;
        }
        return -1;
    }

    public List<Compartment> getAllCompartments(){
        clearAllBrokenRefs();
        if(dirty){
            compartmentsCache.clear();
            for(CompartmentGetter getter: compartments){
                Compartment c = getter.get();
                if(c != null){
                    compartmentsCache.add(c);
                }
            }
            for(CompartmentGetter getter: ref){
                Compartment c = getter.get();
                if(c != null){
                    compartmentsCache.add(c);
                }
            }
        }
        return compartmentsCache;
    }

    public static final Codec<CompartmentList> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    Codec.INT.fieldOf("current_id").forGetter(l -> l.currentId),
                    CompartmentEntry.CODEC.listOf().fieldOf("compartments").forGetter(l -> l.compartments),
                    CompartmentRef.CODEC.listOf().fieldOf("refs").forGetter(l -> l.ref)
            ).apply(inst, CompartmentList::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, CompartmentList> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, l -> l.currentId,
            ByteBufCodecs.collection(ArrayList::new, CompartmentEntry.STREAM_CODEC), l -> l.compartments,
            ByteBufCodecs.collection(ArrayList::new, CompartmentRef.STREAM_CODEC), l -> l.ref,
            CompartmentList::new
    );

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("current_id", currentId);
        ListTag source = new ListTag();
        for(CompartmentEntry entry: compartments){
            source.add(entry.save(provider));
        }
        tag.put("compartments", source);
        ListTag refs = new ListTag();
        for(CompartmentRef cRef: ref){
            refs.add(cRef.save());
        }
        tag.put("refs", refs);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        currentId = nbt.getInt("current_id");
        ListTag source = nbt.getList("compartments", Tag.TAG_COMPOUND);
        for (Tag st: source){
            compartments.add(CompartmentEntry.load((CompoundTag) st, provider));
        }
        ListTag refs = nbt.getList("refs", Tag.TAG_COMPOUND);
        for (Tag rt: refs){
            ref.add(CompartmentRef.load((CompoundTag) rt, provider));
        }
        dirty = true;
    }

    public void addRef(CompartmentRef cRef){
        ref.add(cRef);
    }

    public CompartmentEntry entryOf(Compartment compartment){
        for(CompartmentEntry entry: compartments){
            if(entry.get() == compartment){
                return entry;
            }
        }
        return null;
    }

    public void clearAllBrokenRefs() {
        ref.removeIf(CompartmentRef::isBroken);
    }
}
