package com.afs.integratedMachine.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Arrays;

public class MultiFluidTankHandle implements IFluidHandler, INBTSerializable<CompoundTag> {
    private final NonNullList<FluidStack> tanks;
    private final int[] capacity;
    private boolean allowMultiIO;

    public MultiFluidTankHandle(int size, boolean allowMultiIO, int[] capacity) {
        this.tanks = NonNullList.withSize(size, FluidStack.EMPTY);
        this.allowMultiIO = allowMultiIO;
        if (capacity.length != size) {
            throw new IllegalArgumentException("capacity array length must equals to tank size!");
        }
        this.capacity = capacity;
    }

    public MultiFluidTankHandle(int size, boolean allowMultiIO, int generalCapacity) {
        this(size, allowMultiIO, createUniformCapacityArray(size, generalCapacity));
    }

    private static int[] createUniformCapacityArray(int size, int capacity) {
        int[] capacities = new int[size];
        Arrays.fill(capacities, capacity);
        return capacities;
    }

    public void setAllowMultiIO(boolean allowMultiIO) {
        this.allowMultiIO = allowMultiIO;
    }

    public boolean isAllowMultiIO() {
        return allowMultiIO;
    }

    public int[] getCapacity() {
        return capacity;
    }

    public void setCapacityInSlot(int slot, int newCapacity) {
        validateSlot(slot);
        capacity[slot] = newCapacity;
    }

    @Override
    public int getTanks() {
        return tanks.size();
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        validateSlot(tank);
        return tanks.get(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        validateSlot(tank);
        return capacity[tank];
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        int remaining = resource.getAmount();
        for (int i = 0; i < getTanks(); i++) {
            FluidStack tank = tanks.get(i);
            if (tank.isEmpty()) {
                int inserted = Math.min(remaining, capacity[i]);
                if (action.execute()) {
                    tanks.set(i, resource.copyWithAmount(inserted));
                }
                if (!allowMultiIO) return inserted;
                remaining -= inserted;
            } else if (FluidStack.isSameFluidSameComponents(resource, tank)) {
                int space = capacity[i] - tank.getAmount();
                int inserted = Math.min(remaining, space);
                if (action.execute()) {
                    tank.grow(inserted);
                }
                if (!allowMultiIO && inserted != 0) return inserted;
                remaining -= inserted;
            }
            if(remaining == 0){
                return resource.getAmount();
            }
        }
        return resource.getAmount() - remaining;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        int remaining = resource.getAmount();
        for (int i = 0; i < getTanks(); i++) {
            FluidStack tank = tanks.get(i);
            if (!FluidStack.isSameFluidSameComponents(tank, resource)) continue;

            int drained = Math.min(tank.getAmount(), remaining);
            if (action.execute()) {
                if (drained == tank.getAmount()) {
                    tanks.set(i, FluidStack.EMPTY);
                } else {
                    tank.shrink(drained);
                    break;
                }
            }
            if (!allowMultiIO) {
                return resource.copyWithAmount(drained);
            }
            remaining -= drained;
        }
        return resource.copyWithAmount(resource.getAmount() - remaining);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        FluidStack result = FluidStack.EMPTY;
        int remaining = maxDrain;

        for (int i = 0; i < getTanks(); i++) {
            FluidStack tank = tanks.get(i);
            if (tank.isEmpty()) continue;

            if (result.isEmpty()) {
                // First non-empty tank — start draining here
                result = drainFromTank(tank, remaining, i, action);
                if (result.isEmpty()) return FluidStack.EMPTY;
                if (!allowMultiIO || result.getAmount() == maxDrain) return result;
                remaining = maxDrain - result.getAmount();
            } else if (FluidStack.isSameFluidSameComponents(tank, result)) {
                // Same fluid in subsequent tank — drain more
                int drained = Math.min(tank.getAmount(), remaining);
                result.grow(drained);
                if (action.execute()) {
                    if (drained == tank.getAmount()) {
                        tanks.set(i, FluidStack.EMPTY);
                    } else {
                        tank.shrink(drained);
                    }
                }
                remaining -= drained;
                if (remaining == 0) break;
            }
        }

        return result;
    }

    private FluidStack drainFromTank(FluidStack tank, int amount, int slot, FluidAction action) {
        int drained = Math.min(tank.getAmount(), amount);
        if (drained == 0) return FluidStack.EMPTY;

        FluidStack result = tank.copyWithAmount(drained);
        if (action.execute()) {
            if (drained == tank.getAmount()) {
                tanks.set(slot, FluidStack.EMPTY);
            } else {
                tank.shrink(drained);
            }
        }
        return result;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        ListTag tanksTag = new ListTag();
        for (int i = 0; i < tanks.size(); i++) {
            FluidStack tank = tanks.get(i);
            if (!tank.isEmpty()) {
                CompoundTag entry = new CompoundTag();
                entry.putInt("tank", i);
                entry.put("fluid", tank.save(provider));
                tanksTag.add(entry);
            }
        }
        tag.put("tank", tanksTag);
        tag.put("capacity", new IntArrayTag(capacity));
        tag.putBoolean("allow_multi_io", allowMultiIO);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        ListTag tanksTag = nbt.getList("tank", Tag.TAG_COMPOUND);
        for (Tag element : tanksTag) {
            if (element instanceof CompoundTag entry) {
                int tank = entry.getInt("tank");
                tanks.set(tank, FluidStack.parseOptional(provider, entry.getCompound("fluid")));
            }
        }
        int[] storedCap = nbt.getIntArray("capacity");
        System.arraycopy(storedCap, 0, capacity, 0, tanks.size());
        allowMultiIO = nbt.getBoolean("allow_multi_io");
    }

    private void validateSlot(int slot) {
        if (slot >= tanks.size() || slot < 0) {
            throw new IndexOutOfBoundsException("Slot " + slot + " is out of range [0, " + tanks.size() + ")");
        }
    }

    private record TankEntry(int tank, FluidStack fluid) {
        public static final Codec<TankEntry> CODEC = RecordCodecBuilder.create(
                inst -> inst.group(
                        Codec.INT.fieldOf("tank").forGetter(TankEntry::tank),
                        FluidStack.CODEC.fieldOf("fluid").forGetter(TankEntry::fluid)
                ).apply(inst, TankEntry::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, TankEntry> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.INT, TankEntry::tank,
                        FluidStack.STREAM_CODEC, TankEntry::fluid,
                        TankEntry::new
                );
    }
}
