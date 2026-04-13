package com.afs.integratedMachine.datagen;

import com.afs.integratedMachine.dataMap.IMDataMaps;
import com.afs.integratedMachine.dataMap.RefiningFurnaceFuel;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.concurrent.CompletableFuture;

public class DataMaps extends DataMapProvider {
    protected DataMaps(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        builder(IMDataMaps.REFINING_FURNACE_FUEL)
                .add(BuiltInRegistries.ITEM.wrapAsHolder(Items.COAL), refiningFuel(800, -1), false)
                .add(BuiltInRegistries.ITEM.wrapAsHolder(Items.COAL_BLOCK), refiningFuel(8000, -1), false)
                .add(BuiltInRegistries.ITEM.wrapAsHolder(Items.CHARCOAL), refiningFuel(640, -1), false)
                .add(BuiltInRegistries.ITEM.wrapAsHolder(Items.BLAZE_ROD), refiningFuel(300, 600), false)
                .add(BuiltInRegistries.ITEM.wrapAsHolder(Items.BLAZE_POWDER), refiningFuel(150, 600), false)
                .add(BuiltInRegistries.ITEM.wrapAsHolder(Items.LAVA_BUCKET), refiningFuel(3000, 1200), false);
    }

    private RefiningFurnaceFuel refiningFuel(int fuelValue, int liftTempTo){
        return new RefiningFurnaceFuel(fuelValue, liftTempTo);
    }
}
