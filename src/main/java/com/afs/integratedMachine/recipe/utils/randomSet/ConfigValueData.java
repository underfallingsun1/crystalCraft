package com.afs.integratedMachine.recipe.utils.randomSet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ConfigValueData(double factor, int base) {
    public int modify(int value){
        return base + (int)(factor * value);
    }

    public static final Codec<ConfigValueData> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    Codec.DOUBLE.fieldOf("factor").forGetter(ConfigValueData::factor),
                    Codec.INT.fieldOf("base").forGetter(ConfigValueData::base)
            ).apply(inst, ConfigValueData::new)
    );

    public static final StreamCodec<ByteBuf, ConfigValueData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, ConfigValueData::factor,
            ByteBufCodecs.INT, ConfigValueData::base,
            ConfigValueData::new
    );
}
