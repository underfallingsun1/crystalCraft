package com.afs.integratedMachine.block.entity.render;

import com.afs.integratedMachine.block.entity.IMBlockEntityTypes;
import com.afs.integratedMachine.utils.Meta;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = Meta.MODID)
public class BERRegistry {
    @SubscribeEvent
    public static void AddBER(EntityRenderersEvent.RegisterRenderers e){
        e.registerBlockEntityRenderer(IMBlockEntityTypes.COMPARTMENT_CONTROLLER.get(), c -> new CompartmentControllerRender());
    }
}
