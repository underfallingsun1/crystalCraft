package com.afs.integratedMachine.datagen;

import com.afs.integratedMachine.datagen.lang.EnUs;
import com.afs.integratedMachine.datagen.lang.ZhCn;
import com.afs.integratedMachine.utils.Utils;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = Utils.MODID)
public class DataGeneration {
    @SubscribeEvent
    public static void generateData(GatherDataEvent e){
        e.createProvider(EnUs::new);
        e.createProvider(ZhCn::new);
    }
}
