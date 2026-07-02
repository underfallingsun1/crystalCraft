package com.afs.integratedMachine.recipe.utils.randomSet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RandomSet {
    public abstract boolean get(RandomSource source, RandomSetHandle handle, Map<String, Integer> config);
    public abstract String getId();

    public static MapCodec<? extends RandomSet> dispatchCodec(String id){
        return switch (id){
            case "impossible" -> MapCodec.unit(IMPOSSIBLE);
            case "always" -> MapCodec.unit(ALWAYS);
            case "random" -> Random.CODEC;
            case "configured" -> ConfiguredRandom.CODEC;
            case "reverse" -> Reverse.CODEC;
            case "choose" -> Choose.CODEC;
            case "and" -> And.CODEC;
            case "or" -> Or.CODEC;
            default -> throw new IllegalArgumentException(id + " is not a existing random set id!");
        };
    }

    public static StreamCodec<ByteBuf, ? extends RandomSet> dispatchStreamCodec(String id){
        return switch (id){
            case "impossible" -> StreamCodec.unit(IMPOSSIBLE);
            case "always" -> StreamCodec.unit(ALWAYS);
            case "random" -> Random.STREAM_CODEC;
            case "configured" -> ConfiguredRandom.STREAM_CODEC;
            case "reverse" -> Reverse.STREAM_CODEC;
            case "choose" -> Choose.STREAM_CODEC;
            case "and" -> And.STREAM_CODEC;
            case "or" -> Or.STREAM_CODEC;
            default -> throw new IllegalArgumentException(id + "is not a existing random set id!");
        };
    }

    public static final Codec<RandomSet> CODEC = Codec.STRING.dispatch(
            "key",
            RandomSet::getId,
            RandomSet::dispatchCodec
    );

    public static StreamCodec<ByteBuf, RandomSet> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.dispatch(
            RandomSet::getId,
            RandomSet::dispatchStreamCodec
    );

    public static final RandomSet IMPOSSIBLE = new RandomSet() {
        @Override
        public boolean get(RandomSource source, RandomSetHandle handle, Map<String, Integer> config) {
            return false;
        }

        @Override
        public String getId() {
            return "impossible";
        }
    };

    public static final RandomSet ALWAYS = new RandomSet() {
        @Override
        public boolean get(RandomSource source, RandomSetHandle handle, Map<String, Integer> config) {
            return true;
        }

        @Override
        public String getId() {
            return "always";
        }
    };

    public static class Random extends RandomSet{
        public static final int TOTAL = 10000;
        private final int chance;

        public Random(int chance){
            this.chance = chance;
        }

        public int getChance() {
            return chance;
        }

        @Override
        public boolean get(RandomSource source, RandomSetHandle handle, Map<String, Integer> config) {
            return source.nextInt(TOTAL) < chance;
        }

        @Override
        public String getId() {
            return "random";
        }

        public static final MapCodec<Random> CODEC = Codec.INT.xmap(Random::new, Random::getChance).fieldOf("chance");
        public static final StreamCodec<ByteBuf, Random> STREAM_CODEC = ByteBufCodecs.INT.map(Random::new, Random::getChance);
    }

    public static class ConfiguredRandom extends RandomSet{
        public static final int TOTAL = 10000;
        private final int baseChance;
        private final Map<String, ConfigValueData> configs;

        public ConfiguredRandom(int baseChance, Map<String, ConfigValueData> configs){
            this.baseChance = baseChance;
            this.configs = configs;
        }

        public int getBaseChance() {
            return baseChance;
        }

        public Map<String, ConfigValueData> getConfigs() {
            return configs;
        }

        @Override
        public boolean get(RandomSource source, RandomSetHandle handle, Map<String, Integer> config) {
            int finalChance = baseChance;
            for(String id: config.keySet()){
                if(configs.containsKey(id)){
                    finalChance += configs.get(id).modify(config.get(id));
                }
            }
            return source.nextInt(TOTAL) < finalChance;
        }

        @Override
        public String getId() {
            return "configured";
        }

        public static final MapCodec<ConfiguredRandom> CODEC = RecordCodecBuilder.mapCodec(
                inst -> inst.group(
                        Codec.INT.fieldOf("base_chance").forGetter(ConfiguredRandom::getBaseChance),
                        Codec.unboundedMap(Codec.STRING, ConfigValueData.CODEC).fieldOf("configs").forGetter(ConfiguredRandom::getConfigs)
                ).apply(inst, ConfiguredRandom::new)
        );

        public static final StreamCodec<ByteBuf, ConfiguredRandom> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, ConfiguredRandom::getBaseChance,
                ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ConfigValueData.STREAM_CODEC), ConfiguredRandom::getConfigs,
                ConfiguredRandom::new
        );
    }

    public static class Reverse extends RandomSet{
        public final String reversed;

        public Reverse(String reversed){
            this.reversed = reversed;
        }

        public String getReversed() {
            return reversed;
        }

        @Override
        public boolean get(RandomSource source, RandomSetHandle handle, Map<String, Integer> config) {
            return !handle.solve(reversed, source, config);
        }

        @Override
        public String getId() {
            return "reverse";
        }

        public static final MapCodec<Reverse> CODEC = Codec.STRING.xmap(Reverse::new, Reverse::getReversed).fieldOf("reversed");
        public static final StreamCodec<ByteBuf, Reverse> STREAM_CODEC =
                ByteBufCodecs.STRING_UTF8.map(Reverse::new, Reverse::getReversed);
    }

    public static class Choose extends RandomSet{
        private final String condition;
        private final String ifTrue;
        private final String ifFalse;

        public Choose(String condition, String ifTrue, String ifFalse) {
            this.condition = condition;
            this.ifTrue = ifTrue;
            this.ifFalse = ifFalse;
        }

        public String getCondition() {
            return condition;
        }

        public String getIfTrue() {
            return ifTrue;
        }

        public String getIfFalse() {
            return ifFalse;
        }

        @Override
        public boolean get(RandomSource source, RandomSetHandle handle, Map<String, Integer> config) {
            return handle.solve(condition, source, config)?
                    handle.solve(ifTrue, source, config) :
                    handle.solve(ifFalse, source, config);
        }

        @Override
        public String getId() {
            return "choose";
        }

        public static final MapCodec<Choose> CODEC = RecordCodecBuilder.mapCodec(
                inst -> inst.group(
                        Codec.STRING.fieldOf("condition").forGetter(Choose::getCondition),
                        Codec.STRING.fieldOf("if_true").forGetter(Choose::getIfTrue),
                        Codec.STRING.fieldOf("if_false").forGetter(Choose::getIfFalse)
                ).apply(inst, Choose::new)
        );

        public static final StreamCodec<ByteBuf, Choose> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, Choose::getCondition,
                ByteBufCodecs.STRING_UTF8, Choose::getIfTrue,
                ByteBufCodecs.STRING_UTF8, Choose::getIfFalse,
                Choose::new
        );
    }

    public static class And extends RandomSet{
        private final List<String> targetIds;

        public And(List<String> targetIds){
            this.targetIds = targetIds;
        }

        public List<String> getTargetIds() {
            return targetIds;
        }

        @Override
        public boolean get(RandomSource source, RandomSetHandle handle, Map<String, Integer> config) {
            for(String id: targetIds){
                if(!handle.solve(id, source, config)) return false;
            }
            return true;
        }

        public static final MapCodec<And> CODEC = Codec.STRING.listOf().xmap(
                And::new, And::getTargetIds
        ).fieldOf("targets");

        public static final StreamCodec<ByteBuf, And> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.apply(
                ByteBufCodecs.list()
        ).map(And::new, And::getTargetIds);

        @Override
        public String getId() {
            return "and";
        }
    }

    public static class Or extends RandomSet{
        private final List<String> targetIds;

        public Or(List<String> targetIds){
            this.targetIds = targetIds;
        }

        public List<String> getTargetIds() {
            return targetIds;
        }

        @Override
        public boolean get(RandomSource source, RandomSetHandle handle, Map<String, Integer> config) {
            for(String id: targetIds){
                if(handle.solve(id, source, config)) return true;
            }
            return false;
        }

        public static final MapCodec<Or> CODEC = Codec.STRING.listOf().xmap(
                Or::new, Or::getTargetIds
        ).fieldOf("targets");

        public static final StreamCodec<ByteBuf, Or> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.apply(
                ByteBufCodecs.list()
        ).map(Or::new, Or::getTargetIds);

        @Override
        public String getId() {
            return "or";
        }
    }
}
