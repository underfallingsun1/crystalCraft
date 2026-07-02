package com.afs.integratedMachine.utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.List;

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
}
