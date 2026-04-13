package com.afs.integratedMachine.utils.tags;

import com.afs.integratedMachine.utils.Utils;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class IMItemTags {
    public static TagKey<? extends Item> create(String id) {
        return TagKey.create(Registries.ITEM, Utils.modLoc(id));
    }
}
