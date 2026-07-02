package com.afs.integratedMachine.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record SimpleSerializer<T>(MapCodec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, ? extends T> streamCodec) {
}
