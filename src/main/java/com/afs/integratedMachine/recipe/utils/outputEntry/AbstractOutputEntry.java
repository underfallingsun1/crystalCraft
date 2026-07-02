package com.afs.integratedMachine.recipe.utils.outputEntry;

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

public abstract class AbstractOutputEntry {
    public abstract OutputType getEntryType();

    public abstract SimpleSerializer<? extends AbstractOutputEntry> type();

    public static final ResourceKey<Registry<SimpleSerializer<? extends AbstractOutputEntry>>> OUTPUT_TYPE_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Utils.modLoc("output_type"));

    public static final Registry<SimpleSerializer<? extends AbstractOutputEntry>> OUTPUT_TYPE_REGISTRY =
            new RegistryBuilder<>(OUTPUT_TYPE_REGISTRY_KEY).sync(true).create();

    public static final Codec<AbstractOutputEntry> CODEC = OUTPUT_TYPE_REGISTRY.byNameCodec().dispatch(
            "type", AbstractOutputEntry::type, SimpleSerializer::codec
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, AbstractOutputEntry> STREAM_CODEC =
            ByteBufCodecs.registry(OUTPUT_TYPE_REGISTRY_KEY).dispatch(
                    AbstractOutputEntry::type, SimpleSerializer::streamCodec
            );

    public enum OutputType{
        ITEM, FLUID, EVENT
    }

    public static final DeferredRegister<SimpleSerializer<? extends AbstractOutputEntry>> OUTPUT_TYPES =
            DeferredRegister.create(OUTPUT_TYPE_REGISTRY, Meta.MODID);

    public static final Supplier<SimpleSerializer<AbstractItemOutputEntry.Simple>> ITEM_OUTPUT = OUTPUT_TYPES.register(
            "item_output", () -> AbstractItemOutputEntry.Simple.SERIALIZER
    );

    public static final Supplier<SimpleSerializer<AbstractItemOutputEntry.Chanced>> CHANCED_ITEM_OUTPUT = OUTPUT_TYPES.register(
            "chanced_item_output", () -> AbstractItemOutputEntry.Chanced.SERIALIZER
    );
}
