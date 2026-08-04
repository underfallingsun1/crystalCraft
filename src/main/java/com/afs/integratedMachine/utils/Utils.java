package com.afs.integratedMachine.utils;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {
    public static ResourceLocation modLoc(String path){
        return ResourceLocation.fromNamespaceAndPath(Meta.MODID, path);
    }

    public static void addStack(List<ItemStack> stacks, ItemStack newStack){
        for(ItemStack stack:stacks){
            if(ItemStack.isSameItemSameComponents(stack, newStack)){
                stack.grow(newStack.getCount());
                return;
            }
        }
        stacks.add(newStack);
    }

    public static void applyItem(List<ItemStack> stacks, IItemHandler handler){
        for(ItemStack stack: stacks){
            for(int i = 0; i < handler.getSlots(); i++){
                stack = handler.insertItem(i, stack, false);
                if(stack.isEmpty()){
                    break;
                }
            }
            if(!stack.isEmpty()){
                Meta.LOGGER.warn("Item can not inject!Item:{}, Remain:{}", stack.getItem(), stack.getCount());
            }
        }
    }


    public static final Map<ResourceKey<Level>, Level> levelCache = new HashMap<>();

    public static Level getLevelByDimension(ResourceKey<Level> dimension){
        return levelCache.computeIfAbsent(dimension, dim -> {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                return server.getLevel(dim);
            }
            else{
                throw new IllegalArgumentException("unknown level: " + dimension.location());
            }
        });
    }
}
