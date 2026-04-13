package com.afs.integratedMachine.utils;

import net.minecraft.network.chat.Component;

public class LangComps {
    public static final LangComp TAB_TITLE = new LangComp("tab.integrated_machine.title");
    public static final LangComp REFINING_FURNACE_TITLE = new LangComp("gui.refining_furnace.title");
    public static final LangComp REFINING_FURNACE_FUEL_TIP = new LangComp("gui.refining_furnace.tip.fuel");
    public static final LangComp REFINING_FURNACE_TEMPERATURE_TIP = new LangComp("gui.refining_furnace.tip.temperature");

    public record LangComp(String key){
        public Component apply(){
            return Component.translatable(key);
        }

        public Component apply(Object... args){
            return Component.translatable(key, args);
        }
    }
}
