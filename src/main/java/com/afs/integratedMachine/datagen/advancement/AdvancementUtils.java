package com.afs.integratedMachine.datagen.advancement;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public class AdvancementUtils {
    public static Criterion<?> hasItem(ItemLike item){
        return InventoryChangeTrigger.TriggerInstance.hasItems(item);
    }

    public static Criterion<?> hasItem(TagKey<Item> item){
        return InventoryChangeTrigger.TriggerInstance.hasItems(
                ItemPredicate.Builder.item().of(item)
        );
    }
}
