package com.afs.integratedMachine.recipe.utils.randomSet;

import com.afs.integratedMachine.utils.Meta;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;

import java.util.*;

public class RandomSetHandle {
    private final Map<String, RandomSet> randomSets;
    private final Map<String, Boolean> cache;
    private final Set<String> recursionStack;

    public static final Codec<RandomSetHandle> CODEC = Codec.unboundedMap(Codec.STRING, RandomSet.CODEC)
            .xmap(RandomSetHandle::new, RandomSetHandle::getRandomSets);

    public static final StreamCodec<ByteBuf, RandomSetHandle> STREAM_CODEC = ByteBufCodecs.map(
            n -> ((Map<String, RandomSet>)new HashMap<String, RandomSet>(n)), ByteBufCodecs.STRING_UTF8, RandomSet.STREAM_CODEC
    ).map(RandomSetHandle::new, RandomSetHandle::getRandomSets);

    public RandomSetHandle(Map<String, RandomSet> randomSets){
        this.randomSets = randomSets;
        cache = new HashMap<>();
        recursionStack = new HashSet<>();
    }

    public Map<String, RandomSet> getRandomSets(){
        return randomSets;
    }

    public boolean solve(String id, RandomSource random, Map<String, Integer> config){
        if(cache.containsKey(id)) return cache.get(id);
        else{
            if(recursionStack.contains(id)){
                Meta.LOGGER.error("infinity recursive occurred!");
                return false;
            }
            recursionStack.add(id);
            boolean res = randomSets.get(id).get(random, this, config);
            cache.put(id, res);
            return res;
        }
    }

    public boolean get(String id){
        return Boolean.TRUE.equals(cache.get(id));
    }

    public void build(RandomSource random, Map<String, Integer> config){
        cache.clear();
        for(String k: randomSets.keySet()){
            solve(k, random, config);
            recursionStack.clear();
        }
    }
}
