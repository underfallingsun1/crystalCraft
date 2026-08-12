package com.afs.integratedMachine;

import com.afs.integratedMachine.block.IMBlocks;
import com.afs.integratedMachine.block.entity.IMBlockEntityTypes;
import com.afs.integratedMachine.common_registries.IMDataAttachments;
import com.afs.integratedMachine.compartment.property.CompartmentProperty;
import com.afs.integratedMachine.item.IMItems;
import com.afs.integratedMachine.client.gui.menu.IMMenus;
import com.afs.integratedMachine.recipe.IMRecipes;
import com.afs.integratedMachine.recipe.utils.inputEntry.AbstractInputEntry;
import com.afs.integratedMachine.recipe.utils.outputEntry.AbstractOutputEntry;
import com.afs.integratedMachine.utils.Meta;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@Mod(Meta.MODID)
@EventBusSubscriber(modid = Meta.MODID)
public class IntegratedMachine {
    public IntegratedMachine(IEventBus bus, ModContainer container){
        IMItems.ITEMS.register(bus);
        IMItems.TABS.register(bus);
        IMBlocks.BLOCKS.register(bus);
        IMBlocks.TYPES.register(bus);
        IMBlockEntityTypes.BE_TYPES.register(bus);
        IMMenus.MENUS.register(bus);
        IMRecipes.RECIPES.register(bus);
        IMRecipes.SERIALIZERS.register(bus);
        AbstractInputEntry.INPUT_TYPES.register(bus);
        AbstractOutputEntry.OUTPUT_TYPES.register(bus);
        IMDataAttachments.ATTACHMENT_TYPES.register(bus);

        Meta.LOGGER.info("mod integrated machine is loaded!");
    }

    @SubscribeEvent
    public static void AddNewRegistries(NewRegistryEvent e){
        e.register(AbstractInputEntry.INPUT_TYPE_REGISTRY);
        e.register(AbstractOutputEntry.OUTPUT_TYPE_REGISTRY);
        e.register(CompartmentProperty.COMPARTMENT_PROPERTY_REGISTRY);
    }
}
