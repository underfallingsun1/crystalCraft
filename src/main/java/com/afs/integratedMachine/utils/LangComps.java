package com.afs.integratedMachine.utils;

import net.minecraft.network.chat.Component;

public class LangComps {
    public static final LangComp TAB_TITLE = new LangComp("tab.integrated_machine.title");

    public record LangComp(String key){
        public Component apply(){
            return Component.translatable(key);
        }

        public Component apply(Object... args){
            return Component.translatable(key, args);
        }
    }
}
