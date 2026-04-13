package com.afs.integratedMachine.recipe.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public record IngredientWithCount(Ingredient ingredient, int count) {
    public boolean test(ItemStack stack){
        return ingredient.test(stack);
    }

    public static final Codec<IngredientWithCount> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    Ingredient.CODEC.fieldOf("ingredient").forGetter(IngredientWithCount::ingredient),
                    Codec.INT.optionalFieldOf("count", 1).forGetter(IngredientWithCount::count)
            ).apply(inst, IngredientWithCount::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, IngredientWithCount> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, IngredientWithCount::ingredient,
            ByteBufCodecs.INT, IngredientWithCount::count,
            IngredientWithCount::new
    );
}
