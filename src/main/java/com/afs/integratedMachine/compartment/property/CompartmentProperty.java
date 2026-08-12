package com.afs.integratedMachine.compartment.property;

import com.afs.integratedMachine.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class CompartmentProperty<T> {
    private final String id;
    private final T defaultValue;
    private final Supplier<ValueCalculator<T>> valueCalculatorGetter;

    public CompartmentProperty(String id, T defaultValue, Supplier<ValueCalculator<T>> valueCalculatorGetter){
        this.id = id;
        this.defaultValue = defaultValue;
        this.valueCalculatorGetter = valueCalculatorGetter;
    }

    public String getId() {
        return id;
    }

    public T getDefaultValue(){
        return defaultValue;
    }

    public ValueCalculator<T> getCalculator(){
        return valueCalculatorGetter.get();
    }

    public interface ValueCalculator<T>{
        void add(BlockPos pos, T value);
        T calculate();
    }

    public static final ResourceKey<Registry<CompartmentProperty<?>>> COMPARTMENT_PROPERTY_TYPE =
            ResourceKey.createRegistryKey(Utils.modLoc("compartment_property"));

    public static final Registry<CompartmentProperty<?>> COMPARTMENT_PROPERTY_REGISTRY =
            new RegistryBuilder<>(COMPARTMENT_PROPERTY_TYPE)
                    .sync(true)
                    .create();
}
