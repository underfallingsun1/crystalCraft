package com.afs.crystal_craft.datagen;

import com.afs.crystal_craft.utils.Utils;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Utils.MODID)
public class GatherData {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent e){
        DataGenerator generator = e.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper helper = e.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> registries = e.getLookupProvider();

    }
}
