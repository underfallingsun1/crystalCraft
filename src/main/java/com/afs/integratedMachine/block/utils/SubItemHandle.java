package com.afs.integratedMachine.block.utils;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.apache.commons.io.IOCase;

public class SubItemHandle implements IItemHandler {
    private final IItemHandler handler;
    private final int offset;
    private final int size;
    private final IOType ioConfig;

    public static final SubItemHandle EMPTY = new SubItemHandle(null, 0, 0, false, false){
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 0;
        }

        @Override
        public int getSlots() {
            return 0;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            throw new IllegalStateException("empty handle should not be visited");
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return stack;
        }
    };

    public SubItemHandle(IItemHandler base, int offset, int size, boolean canInsert, boolean canExtract){
        this.handler = base;
        this.offset = offset;
        this.size = size;
        this.ioConfig = IOType.of(canInsert, canExtract);
    }

    @Override
    public int getSlots() {
        return size;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return handler.getStackInSlot(slot + offset);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if(!ioConfig.canInsert) return stack;
        return handler.insertItem(slot + offset, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if(!ioConfig.canExtract) return ItemStack.EMPTY;
        return handler.extractItem(slot + offset, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return handler.getSlotLimit(slot + offset);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if(!ioConfig.canInsert) return false;
        return handler.isItemValid(slot + offset, stack);
    }

    public IOType getIoConfig() {
        return ioConfig;
    }

    public int getSize() {
        return size;
    }

    public int getOffset() {
        return offset;
    }
}
