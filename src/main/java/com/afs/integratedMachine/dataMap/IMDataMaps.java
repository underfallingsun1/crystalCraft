package com.afs.integratedMachine.dataMap;

import com.afs.integratedMachine.utils.Meta;
import com.afs.integratedMachine.utils.Utils;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

@EventBusSubscriber(modid = Meta.MODID)
public class IMDataMaps {
    public static final DataMapType<Item, RefiningFurnaceFuel> REFINING_FURNACE_FUEL = DataMapType.builder(
            Utils.modLoc("refining_furnace_fuel"),
            Registries.ITEM,
            RefiningFurnaceFuel.CODEC
    ).synced(RefiningFurnaceFuel.CODEC, true).build();

    @SubscribeEvent
    public static void register(RegisterDataMapTypesEvent e){
        e.register(REFINING_FURNACE_FUEL);
    }
}
