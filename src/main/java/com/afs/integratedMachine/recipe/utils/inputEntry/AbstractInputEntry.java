package com.afs.integratedMachine.recipe.utils.inputEntry;

import com.afs.integratedMachine.utils.Meta;
import com.afs.integratedMachine.utils.SimpleSerializer;
import com.afs.integratedMachine.utils.Utils;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public abstract class AbstractInputEntry {
    public abstract InputType getEntryType();

    public abstract SimpleSerializer<? extends AbstractInputEntry> type();

    public enum InputType{
        ITEM, FLUID, ENVIRONMENT
    }

    public static final ResourceKey<Registry<SimpleSerializer<? extends AbstractInputEntry>>> INPUT_TYPE_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Utils.modLoc("input_type"));

    public static final Registry<SimpleSerializer<? extends AbstractInputEntry>> INPUT_TYPE_REGISTRY =
            new RegistryBuilder<>(INPUT_TYPE_REGISTRY_KEY).sync(true).create();

    public static final Codec<AbstractInputEntry> CODEC = INPUT_TYPE_REGISTRY.byNameCodec().dispatch(
            "type", AbstractInputEntry::type, SimpleSerializer::codec
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, AbstractInputEntry> STREAM_CODEC =
            ByteBufCodecs.registry(INPUT_TYPE_REGISTRY_KEY).dispatch(
                    AbstractInputEntry::type, SimpleSerializer::streamCodec
            );

    public static final DeferredRegister<SimpleSerializer<? extends AbstractInputEntry>> INPUT_TYPES =
            DeferredRegister.create(INPUT_TYPE_REGISTRY_KEY, Meta.MODID);

    public static final Supplier<SimpleSerializer<AbstractItemInputEntry.Simple>> ITEM_INPUT = INPUT_TYPES.register(
            "item_input", () -> AbstractItemInputEntry.Simple.SERIALIZER
    );

    public static final Supplier<SimpleSerializer<AbstractItemInputEntry.Unconsumed>> UNCONSUMED_ITEM_INPUT = INPUT_TYPES.register(
            "unconsumed_item_input", () -> AbstractItemInputEntry.Unconsumed.SERIALIZER
    );

    public static final Supplier<SimpleSerializer<AbstractItemInputEntry.Chanced>> CHANCED_ITEM_INPUT = INPUT_TYPES.register(
            "chanced_item_input", () -> AbstractItemInputEntry.Chanced.SERIALIZER
    );

    public static final Supplier<SimpleSerializer<EnvironmentPredicateInput>> ENVIRONMENT_PREDICATE = INPUT_TYPES.register(
            "environment_predicate", () -> EnvironmentPredicateInput.SERIALIZER
    );
}
