package com.afs.integratedMachine.recipe.utils.inputEntry;

import com.afs.integratedMachine.recipe.utils.randomSet.RandomSetHandle;
import com.afs.integratedMachine.utils.SimpleSerializer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Map;

public abstract class AbstractItemInputEntry extends AbstractInputEntry implements InputEntryEffect.Item{
    protected final Ingredient ingredient;
    protected final int count;

    public AbstractItemInputEntry(Ingredient ingredient, int count) {
        this.ingredient = ingredient;
        this.count = count;
    }

    @Override
    public InputType getEntryType() {
        return InputType.ITEM;
    }

    @Override
    public Ingredient getIngredient() {
        return ingredient;
    }

    @Override
    public int getCount() {
        return count;
    }

    public static class Simple extends AbstractItemInputEntry{
        public Simple(Ingredient ingredient, int count) {
            super(ingredient, count);
        }

        @Override
        public SimpleSerializer<Simple> type() {
            return AbstractInputEntry.ITEM_INPUT.get();
        }

        public static final MapCodec<Simple> CODEC = RecordCodecBuilder.mapCodec(
                inst -> inst.group(
                        Ingredient.CODEC.fieldOf("ingredient").forGetter(Simple::getIngredient),
                        Codec.INT.fieldOf("count").forGetter(Simple::getCount)
                ).apply(inst, Simple::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, Simple> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, Simple::getIngredient,
                        ByteBufCodecs.INT, Simple::getCount,
                        Simple::new
                );

        public static final SimpleSerializer<Simple> SERIALIZER = new SimpleSerializer<>(CODEC, STREAM_CODEC);

        @Override
        public int getConsumedAmount(Map<String, Integer> config, RandomSetHandle randomSets) {
            return count;
        }
    }

    public static class Unconsumed extends AbstractItemInputEntry {
        public Unconsumed(Ingredient ingredient, int count) {
            super(ingredient, count);
        }

        public static final MapCodec<Unconsumed> CODEC = RecordCodecBuilder.mapCodec(
                inst -> inst.group(
                        Ingredient.CODEC.fieldOf("ingredient").forGetter(Unconsumed::getIngredient),
                        Codec.INT.fieldOf("count").forGetter(Unconsumed::getCount)
                ).apply(inst, Unconsumed::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, Unconsumed> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, Unconsumed::getIngredient,
                        ByteBufCodecs.INT, Unconsumed::getCount,
                        Unconsumed::new
                );

        public static final SimpleSerializer<Unconsumed> SERIALIZER = new SimpleSerializer<>(CODEC, STREAM_CODEC);

        @Override
        public int getConsumedAmount(Map<String, Integer> config, RandomSetHandle randomSets) {
            return 0;
        }

        @Override
        public SimpleSerializer<Unconsumed> type() {
            return AbstractInputEntry.UNCONSUMED_ITEM_INPUT.get();
        }
    }

    public static class Chanced extends AbstractItemInputEntry{
        private final String randomSetId;

        public Chanced(Ingredient ingredient, int count, String randomSetId) {
            super(ingredient, count);
            this.randomSetId = randomSetId;
        }

        @Override
        public SimpleSerializer<? extends AbstractInputEntry> type() {
            return AbstractInputEntry.CHANCED_ITEM_INPUT.get();
        }

        @Override
        public int getConsumedAmount(Map<String, Integer> config, RandomSetHandle randomSets) {
            return randomSets.get(randomSetId)?count:0;
        }

        public String getRandomSetId() {
            return randomSetId;
        }

        public static final MapCodec<Chanced> CODEC = RecordCodecBuilder.mapCodec(
                inst -> inst.group(
                        Ingredient.CODEC.fieldOf("ingredient").forGetter(AbstractItemInputEntry::getIngredient),
                        Codec.INT.fieldOf("count").forGetter(AbstractItemInputEntry::getCount),
                        Codec.STRING.fieldOf("random").forGetter(Chanced::getRandomSetId)
                ).apply(inst, Chanced::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, Chanced> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, Chanced::getIngredient,
                        ByteBufCodecs.INT, Chanced::getCount,
                        ByteBufCodecs.STRING_UTF8, Chanced::getRandomSetId,
                        Chanced::new
                );

        public static final SimpleSerializer<Chanced> SERIALIZER = new SimpleSerializer<>(CODEC, STREAM_CODEC);
    }
}
