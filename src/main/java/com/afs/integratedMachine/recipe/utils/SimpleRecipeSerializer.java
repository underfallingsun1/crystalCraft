package com.afs.integratedMachine.recipe.utils;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class SimpleRecipeSerializer<T extends Recipe<?>> implements RecipeSerializer<T> {
    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public SimpleRecipeSerializer(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        this.codec = codec;
        this.streamCodec = streamCodec;
    }

    @Override
    public MapCodec<T> codec() {
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
        return streamCodec;
    }
}
