package com.afs.integratedMachine;

import com.afs.integratedMachine.utils.Utils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(Utils.MODID)
public class IntegratedMachine {
    public IntegratedMachine(IEventBus bus, ModContainer container){
        Utils.LOGGER.info("mod integrated machine is loaded!");
    }
}
