package com.afs.integratedMachine.gui.screen;

import com.afs.integratedMachine.gui.menu.IMMenus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber()
public class IMScreens {
    @SubscribeEvent
    public static void registerMenuScreen(RegisterMenuScreensEvent e){
        e.register(IMMenus.REFINING_FURNACE.get(), RefiningFurnaceScreen::new);
    }
}
