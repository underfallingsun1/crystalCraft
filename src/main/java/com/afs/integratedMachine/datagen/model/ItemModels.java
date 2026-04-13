package com.afs.integratedMachine.datagen.model;

import com.afs.integratedMachine.item.IMItems;
import com.afs.integratedMachine.utils.Meta;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ItemModels extends ItemModelProvider {
    public ItemModels(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Meta.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(IMItems.POWERED_INGOT.get());
        basicItem(IMItems.SHINING_INGOT.get());
        basicItem(IMItems.END_INGOT.get());
        basicItem(IMItems.CRYSTALLIZED_INGOT.get());
        basicItem(IMItems.EXTREME_INGOT.get());
        basicItem(IMItems.STEEL_INGOT.get());
    }
}
