package com.afs.crystal_craft;

import com.afs.crystal_craft.block.MBlocks;
import com.afs.crystal_craft.item.MItems;
import com.afs.crystal_craft.utils.Utils;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Utils.MODID)
public class CrystalCraft {
    public CrystalCraft(IEventBus bus, ModContainer container){
        Utils.LOGGER.info("{} init.", Utils.MODID);
        MBlocks.BLOCKS.register(bus);
        MBlocks.BLOCK_TYPES.register(bus);
        MItems.ITEMS.register(bus);
    }
}
