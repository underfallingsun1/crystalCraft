package com.afs.integratedMachine.utils.tags;

import com.afs.integratedMachine.utils.Utils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class IMBlockTags {
    public static TagKey<Block> create(String id){
        return TagKey.create(Registries.BLOCK, Utils.modLoc(id));
    }

    public static TagKey<Block> create(String id, String namespace){
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(namespace, id));
    }

    public static final TagKey<Block> COMPARTMENT_WALL = create("compartment_wall");
    public static final TagKey<Block> COMPARTMENT_CONTROLLER = create("compartment_controller");
    public static final TagKey<Block> COMPARTMENT_INTERFACE = create("compartment_interface");
}
