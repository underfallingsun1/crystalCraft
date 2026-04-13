package com.afs.integratedMachine.gui.menu;

import com.afs.integratedMachine.block.IMBlocks;
import com.afs.integratedMachine.block.entity.RefiningFurnaceBlockEntity;
import com.afs.integratedMachine.dataMap.IMDataMaps;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class RefiningFurnaceMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final ContainerData data;

    public RefiningFurnaceMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new ItemStackHandler(6), ContainerLevelAccess.NULL, new SimpleContainerData(4));
    }

    public RefiningFurnaceMenu(int containerId, Inventory inventory, IItemHandler handler,
                                  ContainerLevelAccess access, ContainerData data){
        super(IMMenus.REFINING_FURNACE.get(), containerId);
        addMachine(handler);
        addInventory(inventory);
        addDataSlots(data);
        this.access = access;
        this.data = data;
    }

    public float getProgress(){
        return ((float) this.data.get(2)) / RefiningFurnaceBlockEntity.MAX_PROGRESS;
    }

    public int getStoredFuel(){
        return this.data.get(0);
    }

    public int getTemperature(){
        return this.data.get(1);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();
            if (index < 6) {
                if (!this.moveItemStackTo(slotStack, 6, 42, true)) {
                    return ItemStack.EMPTY;
                }
                slot.setChanged();
            } else if (index < 42) {
                if (slotStack.getItemHolder().getData(IMDataMaps.REFINING_FURNACE_FUEL) != null) {
                    if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                    slot.setChanged();
                } else if (!this.moveItemStackTo(slotStack, 1, 4, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotStack, 1, 4, false) &&
                       !this.moveItemStackTo(slotStack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, slotStack);
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, IMBlocks.REFINING_FURNACE.get());
    }

    private void addInventory(Inventory inventory){
        for(int j = 0;j < 3;j ++){
            for(int i = 0;i < 9;i ++){
                this.addSlot(new Slot(inventory, i + j * 9 + 9, 8 + i * 18, 84 + 18 * j));
            }
        }
        for(int i = 0;i < 9;i ++){
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 142));
        }
    }

    private void addMachine(IItemHandler handler){
        addSlot(new SlotItemHandler(handler, 0, 8, 54));
        addSlot(new SlotItemHandler(handler, 1, 44, 36));
        addSlot(new SlotItemHandler(handler, 2, 62, 36));
        addSlot(new SlotItemHandler(handler, 3, 80, 36));
        addSlot(new SlotItemHandler(handler, 4, 116, 36));
        addSlot(new SlotItemHandler(handler, 5, 134, 36));
    }
}
