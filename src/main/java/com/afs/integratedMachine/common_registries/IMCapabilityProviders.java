package com.afs.integratedMachine.common_registries;

import com.afs.integratedMachine.block.entity.IMBlockEntityTypes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber
public class IMCapabilityProviders {
    @SubscribeEvent
    public static void RegisterCapabilities(RegisterCapabilitiesEvent e){
        e.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                IMBlockEntityTypes.REFINING_FURNACE_BE.get(),
                (be, side) -> switch (side){
                    case UP -> be.getInputSlots();
                    case DOWN -> be.getOutputSlots();
                    case EAST, WEST, SOUTH, NORTH -> be.getFuelSlots();
                }
        );
    }
}
