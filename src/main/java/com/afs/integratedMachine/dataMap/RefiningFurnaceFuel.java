package com.afs.integratedMachine.dataMap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record RefiningFurnaceFuel(int fuelValue, int liftTemperatureTo) {
    public RefiningFurnaceFuel(int fuelValue){
        this(fuelValue, -1);
    }

    public static final Codec<RefiningFurnaceFuel> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    Codec.INT.fieldOf("fuel").forGetter(RefiningFurnaceFuel::fuelValue),
                    Codec.INT.optionalFieldOf("lift_temperature_to", -1).forGetter(RefiningFurnaceFuel::liftTemperatureTo)
            ).apply(inst, RefiningFurnaceFuel::new)
    );
}
