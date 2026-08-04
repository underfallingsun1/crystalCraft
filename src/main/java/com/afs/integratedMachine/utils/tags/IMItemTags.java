package com.afs.integratedMachine.utils.tags;

import com.afs.integratedMachine.utils.Utils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class IMItemTags {
    public static TagKey<Item> create(String id) {
        return TagKey.create(Registries.ITEM, Utils.modLoc(id));
    }

    public static TagKey<Item> create(String id, String namespace){
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(namespace, id));
    }

    public static TagKey<Item> create(TagKey<Block> blockTag){
        return TagKey.create(Registries.ITEM, blockTag.location());
    }
}
