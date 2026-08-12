package com.afs.integratedMachine.client.model.connectModel;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.*;
import java.util.function.Predicate;

public record ConnectPredicate (
        Block block,
        TagKey<Block> tag,
        Map<String, Boolean> requiredProperty,
        Set<BlockState> acceptCache,
        Set<BlockState> denyCache
) implements Predicate<BlockState>{
    public static final Codec<ConnectPredicate> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("block").forGetter(p -> Optional.ofNullable(p.block)),
                    TagKey.codec(Registries.BLOCK).optionalFieldOf("tag").forGetter(p -> Optional.ofNullable(p.tag)),
                    Codec.unboundedMap(Codec.STRING, Codec.BOOL).optionalFieldOf("property", Map.of()).forGetter(ConnectPredicate::requiredProperty)
                    ).apply(inst, (b, t, rp) -> {
                        if(rp.isEmpty()) return new ConnectPredicate(b.orElse(null), t.orElse(null), rp, Set.of(), Set.of());
                        else return new ConnectPredicate(b.orElse(null), t.orElse(null), rp, new HashSet<>(), new HashSet<>());
                    })
            );

    public static ConnectPredicate ofBlock(Block block, Map<BooleanProperty, Boolean> map){
        if(map.isEmpty()) return new ConnectPredicate(block, null, Map.of(), Set.of(), Set.of());
        else{
            Map<String, Boolean> rq = new HashMap<>();
            for(BooleanProperty bp: map.keySet()){
                rq.put(bp.getName(), map.get(bp));
            }
            return new ConnectPredicate(block, null, rq, new HashSet<>(), new HashSet<>());
        }
    }

    public static ConnectPredicate ofTag(TagKey<Block> tag, Map<BooleanProperty, Boolean> map){
        if(map.isEmpty()) return new ConnectPredicate(null, tag, Map.of(), Set.of(), Set.of());
        else{
            Map<String, Boolean> rq = new HashMap<>();
            for(BooleanProperty bp: map.keySet()){
                rq.put(bp.getName(), map.get(bp));
            }
            return new ConnectPredicate(null, tag, rq, new HashSet<>(), new HashSet<>());
        }
    }

    @Override
    public boolean test(BlockState state) {
        if(block != null && !state.is(block)){
            return false;
        }
        if(tag != null && !state.is(tag)){
            return false;
        }
        if(requiredProperty.isEmpty()){
            return true;
        }

        if(acceptCache.contains(state)){
            return true;
        }
        if(state.isAir() || denyCache.contains(state)){
            return false;
        }
        for(String s: requiredProperty.keySet()){
            Property<?> property = state.getBlock().getStateDefinition().getProperty(s);
            if(!(property instanceof BooleanProperty bp) || !state.getValue(bp).equals(requiredProperty.get(s))){
                denyCache.add(state);
                return false;
            }
        }
        acceptCache.add(state);
        return true;
    }
}
