package com.afs.integratedMachine.block.entity;

import com.afs.integratedMachine.block.RefiningFurnaceBlock;
import com.afs.integratedMachine.dataMap.IMDataMaps;
import com.afs.integratedMachine.dataMap.RefiningFurnaceFuel;
import com.afs.integratedMachine.gui.menu.RefiningFurnaceMenu;
import com.afs.integratedMachine.recipe.IMRecipes;
import com.afs.integratedMachine.recipe.RefiningRecipe;
import com.afs.integratedMachine.recipe.input.SimpleItemInput;
import com.afs.integratedMachine.utils.CombinedItemHandle;
import com.afs.integratedMachine.utils.LangComps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RefiningFurnaceBlockEntity extends BlockEntity {
    private final ItemStackHandler items = new ItemStackHandler(6){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            isDirty = true;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if(slot == 0) return isFuel(stack);
            else return slot <= 3;
        }
    };
    private final CombinedItemHandle fuelSlots = CombinedItemHandle.of(items, 0);
    private final CombinedItemHandle inputSlots = CombinedItemHandle.of(items ,1, 2, 3);
    private final CombinedItemHandle outputSlots = CombinedItemHandle.of(items, 4, 5);
    public static final int FUEL_CAPACITY = 36000;
    public static final int MAX_PROGRESS = 600;
    public static final int BASE_TEMPERATURE = 300;
    public static final int BASE_MAX_TEMPERATURE = 600;
    public static final int MAX_MAX_TEMPERATURE = 1200;

    public static int getMaxTemperature(int storedFuel){
        int bonus = Math.max(0, (storedFuel - (FUEL_CAPACITY / 5)) / (FUEL_CAPACITY / 1000));
        return Math.min(BASE_MAX_TEMPERATURE + bonus, MAX_MAX_TEMPERATURE);
    }

    public int getMaxTemperature(){
        return getMaxTemperature(storedFuel);
    }

    private int storedFuel = 0;
    private int temperature = BASE_TEMPERATURE;
    private int progress = 0;
    private boolean onProgress = false;
    private int recipeFuelSpeed = 0;
    private int recipeRequiredTemperature = 0;
    private boolean isDirty = false;
    private int temperatureBound = 0;
    private final List<ItemStack> recipeResultList = new ArrayList<>();
    private RefiningRecipe recipeCache = null;


    public RefiningFurnaceBlockEntity(BlockPos pos, BlockState blockState) {
        super(IMBlockEntityTypes.REFINING_FURNACE_BE.get(), pos, blockState);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        items.deserializeNBT(registries, tag.getCompound("items"));
        storedFuel = tag.getInt("stored_fuel");
        temperature = tag.getInt("temperature");
        onProgress = tag.getBoolean("on_progress");
        progress = tag.getInt("progress");
        recipeFuelSpeed = tag.getInt("recipe_fuel_speed");
        recipeRequiredTemperature = tag.getInt("recipe_required_temperature");
        isDirty = tag.getBoolean("is_dirty");
        temperatureBound = tag.getInt("temperature_bound");
        tag.getList("recipe_result", CompoundTag.TAG_COMPOUND).forEach(
                compound -> recipeResultList.add(ItemStack.parseOptional(registries, (CompoundTag) compound))
        );
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("items", items.serializeNBT(registries));
        tag.putInt("stored_fuel", storedFuel);
        tag.putInt("temperature", temperature);
        tag.putInt("progress", progress);
        tag.putBoolean("on_progress", onProgress);
        tag.putInt("recipe_fuel_speed", recipeFuelSpeed);
        tag.putInt("recipe_required_temperature", recipeRequiredTemperature);
        tag.putBoolean("is_dirty", isDirty);
        tag.putInt("temperature_bound", temperatureBound);
        ListTag resultItems = new ListTag();
        for(ItemStack stack:recipeResultList){
            resultItems.add(stack.save(registries));
        }
        tag.put("recipe_result", resultItems);
    }

    private boolean isFuel(ItemStack stack){
        return (!stack.isEmpty()) && stack.getItemHolder().getData(IMDataMaps.REFINING_FURNACE_FUEL) != null;
    }

    public class RunningData implements ContainerData{
        @Override
        public int get(int index) {
            return switch (index){
                case 0 -> storedFuel;
                case 1 -> temperature;
                case 2 -> progress;
                default -> throw new IllegalArgumentException("index " + index + " must be in 0-3!");
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index){
                case 0:
                    storedFuel = value;
                    break;
                case 1:
                    temperature = value;
                    break;
                case 2:
                    progress = value;
                    break;
                default:
                    throw new IllegalArgumentException("index " + index + "must in 0-3!");
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    public void serverTick(Level level, BlockPos pos, BlockState state){
        if(storedFuel > 0 || temperature > 300){
            if(!state.getValue(RefiningFurnaceBlock.LIT)){
                level.setBlock(pos, state.setValue(RefiningFurnaceBlock.LIT, true), Block.UPDATE_CLIENTS);
            }
        }
        else if(state.getValue(RefiningFurnaceBlock.LIT)){
            level.setBlock(pos, state.setValue(RefiningFurnaceBlock.LIT, false), Block.UPDATE_CLIENTS);
        }

        useFuel();

        if(storedFuel > 0 || temperature > 300){
            if(!onProgress){
                if(level.getGameTime() % 20 == 0){
                    updateTemperature();
                }
                matchRecipeAndApply(level);
            }
            else{
                burn(level);
            }
        }
    }

    private void useFuel(){
        ItemStack fuelItem = items.getStackInSlot(0);
        if(isFuel(fuelItem)){
            RefiningFurnaceFuel fuelValue = fuelItem.getItemHolder().getData(IMDataMaps.REFINING_FURNACE_FUEL);
            int consumed = Math.min(fuelItem.getCount(), (FUEL_CAPACITY - storedFuel) / fuelValue.fuelValue());
            if(consumed > 0){
                storedFuel += consumed * fuelValue.fuelValue();
                if(fuelValue.liftTemperatureTo() > temperature){
                    temperature = fuelValue.liftTemperatureTo();
                }
                ItemStack remained = fuelItem.getCraftingRemainingItem();
                fuelItem.shrink(consumed);
                if(fuelItem.isEmpty() && !remained.isEmpty()){
                    items.setStackInSlot(0, remained);
                }
            }
        }
    }

    private void updateTemperature(){
        int maxTemperature = getMaxTemperature();
        if(storedFuel > 0) {
            if (temperature < maxTemperature) {
                storedFuel -= 1 + (temperature - 300) / 300;
                temperature++;
            } else {
                storedFuel -= 1 + (temperature - 300) / 450;
                if (temperature > maxTemperature) {
                    temperature -= 1 + (temperature - maxTemperature) / 100;
                }
            }
            if(storedFuel < 0) storedFuel = 0;
        }
        else{
            temperature -= (temperature - 300) / 300 + 1;
            if(temperature < 300) temperature = 300;
        }
    }

    private void matchRecipeAndApply(Level level){
        if(!isDirty && (temperatureBound == 0 || temperature < temperatureBound)) return;
        SimpleItemInput input = new SimpleItemInput(inputSlots);
        if(recipeCache != null && matchRecipeAndApply(input, recipeCache, level)){
            return;
        }
        var recipes = level.getRecipeManager().getAllRecipesFor(IMRecipes.REFINING_RECIPE.get());
        for(var opRecipe:recipes){
            RefiningRecipe recipe = opRecipe.value();
            if(matchRecipeAndApply(input, recipe, level)){
                return;
            }
        }
        isDirty = false;
    }

    private boolean matchRecipeAndApply(SimpleItemInput input, RefiningRecipe recipe, Level level){
        if(recipe.matches(input, level)){
            int rqTemperature = recipe.getRequiredTemperature();
            if(temperature < rqTemperature){
                if(temperatureBound == 0 || rqTemperature < temperatureBound) {
                    temperatureBound = rqTemperature;
                    recipeCache = recipe;
                    isDirty = false;
                    return false;
                }
            }
            else if(!recipe.checkItemOutput(outputSlots, 1)){
                temperatureBound = 0;
                recipeCache = recipe;
                isDirty = false;
                return false;
            }
            else{
                onProgress = true;
                progress = 0;
                recipeResultList.addAll(recipe.getItemOutput(input, this));
                recipeFuelSpeed = recipe.getFuelConsumingSpeed();
                recipeRequiredTemperature = recipe.getRequiredTemperature();
                recipeCache = recipe;
                temperatureBound = 0;
                recipe.takeItem(inputSlots, 1);
                return true;
            }
        }
        return false;
    }

    private void burn(Level level){
        if(temperature < recipeRequiredTemperature){
            if(progress > 0) progress -= (recipeRequiredTemperature - temperature) / 300 + 1;
            if(progress < 0) progress = 0;
        }
        if(storedFuel > 0){
            if(temperature < recipeRequiredTemperature){
                storedFuel -= 1;
                if(level.getGameTime() % 20 == 10 && temperature < getMaxTemperature()){
                    temperature ++;
                }
            }
            else{
                int bonus = (temperature - recipeRequiredTemperature) / 100;
                int overTemperaturePunishment = temperature > getMaxTemperature()? 4 + 2 * (temperature - getMaxTemperature()) / 50: 1;
                storedFuel -= recipeFuelSpeed * (1 + bonus / 2) * overTemperaturePunishment;
                progress += bonus + 1;
                if(progress >= MAX_PROGRESS){
                    addResultToOutput();
                    onProgress = false;
                    progress = 0;
                    matchRecipeAndApply(level);
                }
            }
            if(storedFuel < 0) storedFuel = 0;
        }
        else {
            if(level.getGameTime() % 5 == 0){
                temperature -= (temperature - BASE_TEMPERATURE) / 50 + 1;
                if(temperature < BASE_TEMPERATURE) temperature = BASE_TEMPERATURE;
            }
        }
    }

    private void addResultToOutput(){
        for(ItemStack stack: recipeResultList){
            for(int i = 4;i < 6;i ++){
                if(items.getStackInSlot(i).isEmpty()){
                    items.setStackInSlot(i, stack);
                    break;
                }
                else if(ItemStack.isSameItemSameComponents(items.getStackInSlot(i), stack)){
                    int canInput = Math.min(stack.getCount(), stack.getMaxStackSize() - items.getStackInSlot(i).getCount());
                    items.getStackInSlot(i).grow(canInput);
                    stack.shrink(canInput);
                    if(stack.isEmpty()) break;
                }
            }
        }
        recipeResultList.clear();
    }

    public ItemStackHandler getItems() {
        return items;
    }

    public CombinedItemHandle getInputSlots() {
        return inputSlots;
    }

    public CombinedItemHandle getOutputSlots() {
        return outputSlots;
    }

    public CombinedItemHandle getFuelSlots() {
        return fuelSlots;
    }

    public class MenuGetter implements MenuProvider{
        @Override
        public Component getDisplayName() {
            return LangComps.REFINING_FURNACE_TITLE.apply();
        }

        @Override
        public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
            return new RefiningFurnaceMenu(containerId, playerInventory, items,
                    ContainerLevelAccess.create(getLevel(), getBlockPos()), new RunningData());
        }
    }
}
