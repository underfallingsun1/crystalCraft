package com.afs.integratedMachine;

import com.afs.integratedMachine.block.IMBlocks;
import com.afs.integratedMachine.block.entity.IMBlockEntityTypes;
import com.afs.integratedMachine.item.IMItems;
import com.afs.integratedMachine.gui.menu.IMMenus;
import com.afs.integratedMachine.recipe.IMRecipes;
import com.afs.integratedMachine.utils.Meta;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(Meta.MODID)
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
        Meta.LOGGER.info("mod integrated machine is loaded!");
    }
}
