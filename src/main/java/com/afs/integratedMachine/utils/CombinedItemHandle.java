package com.afs.integratedMachine.utils;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

public class CombinedItemHandle implements IItemHandler {
    private final List<SlotEntry> slots;

    public CombinedItemHandle(){
        slots = new ArrayList<>();
    }

    public CombinedItemHandle(List<SlotEntry> slots){
        this.slots = slots;
    }

    public static CombinedItemHandle of(IItemHandler handler, int... slot){
        List<SlotEntry> entries = new ArrayList<>();
        if(handler instanceof CombinedItemHandle combinedHandle)
            for(int x: slot) entries.add(combinedHandle.slots.get(x));
        else
            for(int x: slot) entries.add(new SlotEntry(handler, x));
        return new CombinedItemHandle(entries);
    }

    public static CombinedItemHandle ofRange(IItemHandler handler, int begin, int end){
        List<SlotEntry> entries = new ArrayList<>();
        if(handler instanceof CombinedItemHandle combinedItemHandle)
            for(int i = begin;i < end;i++) entries.add(combinedItemHandle.slots.get(i));
        else
            for(int i = begin;i < end;i++) entries.add(new SlotEntry(handler, i));
        return new CombinedItemHandle(entries);
    }

    public static CombinedItemHandle of(List<IItemHandler> handlers){
        List<SlotEntry> entries = new ArrayList<>();
        for(IItemHandler handler:handlers){
            if(handler instanceof CombinedItemHandle combinedItemHandle){
                entries.addAll(combinedItemHandle.slots);
            }
            else{
                for(int i = 0;i < handler.getSlots();i++){
                    entries.add(new SlotEntry(handler, i));
                }
            }
        }
        return new CombinedItemHandle(entries);
    }

    @Override
    public int getSlots() {
        return slots.size();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        SlotEntry entry = slots.get(slot);
        return entry.handler.getStackInSlot(entry.slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        SlotEntry entry = slots.get(slot);
        return entry.handler.insertItem(entry.slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        SlotEntry entry = slots.get(slot);
        return entry.handler.extractItem(entry.slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        SlotEntry entry = slots.get(slot);
        return entry.handler.getSlotLimit(entry.slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        SlotEntry entry = slots.get(slot);
        return entry.handler.isItemValid(entry.slot, stack);
    }

    public record SlotEntry(IItemHandler handler, int slot){}
}
