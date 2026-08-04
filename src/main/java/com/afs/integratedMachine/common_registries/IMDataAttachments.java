package com.afs.integratedMachine.common_registries;

import com.afs.integratedMachine.compartment.Compartment;
import com.afs.integratedMachine.compartment.CompartmentList;
import com.afs.integratedMachine.utils.Meta;
import com.mojang.serialization.Codec;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.function.Supplier;

public class IMDataAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Meta.MODID);

    private static final Codec<List<Compartment>> HATCHES_CODEC = Codec.list(Compartment.CODEC);

    public static final Supplier<AttachmentType<CompartmentList>> COMPARTMENTS = ATTACHMENT_TYPES.register(
            "compartments", () -> AttachmentType.serializable(CompartmentList::new).build()
    );
}
