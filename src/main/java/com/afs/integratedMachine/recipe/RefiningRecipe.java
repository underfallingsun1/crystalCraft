package com.afs.integratedMachine.recipe;

import com.afs.integratedMachine.block.entity.RefiningFurnaceBlockEntity;
import com.afs.integratedMachine.recipe.input.SimpleItemInput;
import com.afs.integratedMachine.recipe.utils.IMachineRecipe;
import com.afs.integratedMachine.recipe.utils.IngredientWithCount;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

public class RefiningRecipe extends AbstractSimpleItemRecipe implements IMachineRecipe<RefiningFurnaceBlockEntity, SimpleItemInput> {
    private final List<IngredientWithCount> requirements;
    private final ItemStack mainOutput;
    private final ItemStack subOutput;
    private final int subOutputChance;//max chance is 10000
    private final int requiredTemperature;
    private final int fuelConsumingSpeed;

    public static final MapCodec<RefiningRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.list(IngredientWithCount.CODEC).fieldOf("requirements").forGetter(RefiningRecipe::getRequirements),
            ItemStack.OPTIONAL_CODEC.fieldOf("main_output").forGetter(RefiningRecipe::getMainOutput),
            ItemStack.OPTIONAL_CODEC.fieldOf("sub_output").forGetter(RefiningRecipe::getSubOutput),
            Codec.INT.fieldOf("sub_output_chance").forGetter(RefiningRecipe::getSubOutputChance),
            Codec.INT.fieldOf("required_temperature").forGetter(RefiningRecipe::getRequiredTemperature),
            Codec.INT.fieldOf("fuel_consuming_speed").forGetter(RefiningRecipe::getFuelConsumingSpeed)
    ).apply(inst, RefiningRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RefiningRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.collection(ArrayList::new, IngredientWithCount.STREAM_CODEC),
                    RefiningRecipe::getRequirements,
                    ItemStack.OPTIONAL_STREAM_CODEC,
                    RefiningRecipe::getMainOutput,
                    ItemStack.OPTIONAL_STREAM_CODEC,
                    RefiningRecipe::getSubOutput,
                    ByteBufCodecs.INT,
                    RefiningRecipe::getSubOutputChance,
                    ByteBufCodecs.INT,
                    RefiningRecipe::getRequiredTemperature,
                    ByteBufCodecs.INT,
                    RefiningRecipe::getFuelConsumingSpeed,
                    RefiningRecipe::new
            );

    public RefiningRecipe(List<IngredientWithCount> requirements, ItemStack mainOutput, ItemStack subOutput, int subOutputChance, int requiredTemperature, int fuelConsumingSpeed) {
        this.requirements = requirements;
        this.mainOutput = mainOutput;
        this.subOutput = subOutput;
        this.subOutputChance = subOutputChance;
        this.requiredTemperature = requiredTemperature;
        this.fuelConsumingSpeed = fuelConsumingSpeed;
    }

    @Override
    public boolean matches(SimpleItemInput input, Level level) {
        int m = input.size();
        int[] count = new int[m];
        for(int i = 0;i < m;i ++){
            count[i] = input.getItem(i).getCount();
        }
        for(IngredientWithCount requirement: requirements){
            int required = requirement.count();
            for(int i = 0;i < m;i ++){
                if(requirement.test(input.getItem(i))){
                    int consumed = Math.min(required, count[i]);
                    required -= consumed;
                    count[i] -= consumed;
                    if(required == 0){
                        break;
                    }
                }
            }
            if(required > 0){
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(SimpleItemInput input, HolderLookup.Provider registries) {
        return mainOutput.copy();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return mainOutput;
    }

    @Override
    public List<ItemStack> takeItem(IItemHandler handler, int repeat) {
        List<ItemStack> stacks = new ArrayList<>();
        for(int i = 0;i < requirements.size();i ++){
            int required = requirements.get(i).count() * repeat;
            for(int j = 0;j < handler.getSlots();j++){
                if(requirements.get(i).test(handler.getStackInSlot(j))){
                    ItemStack extractedStack = handler.extractItem(j, required, false);
                    stacks.add(extractedStack);
                    required -= extractedStack.getCount();
                    if(required == 0){
                        break;
                    }
                }
            }
            if(required > 0){
                throw new IllegalStateException("requirement " + requirements.get(i).ingredient() + " not " +
                        "reach the count " + requirements.get(i).count() + " * " + repeat + "(remain:" +
                        required + ")");
            }
        }
        return stacks;
    }

    @Override
    public List<ItemStack> getItemOutput(SimpleItemInput input, RefiningFurnaceBlockEntity be){
        List<ItemStack> result = new ArrayList<>();
        if(mainOutput != ItemStack.EMPTY){
            result.add(mainOutput.copy());
        }
        if(subOutput != ItemStack.EMPTY) {
            RandomSource random = be.getLevel().getRandom();
            if (random.nextInt(10000) < subOutputChance) {
                result.add(subOutput.copy());
            }
        }
        return result;
    }

    @Override
    public boolean checkItemOutput(IItemHandler handler, int repeat) {
        int n = handler.getSlots();
        List<ItemStack> stacks = new ArrayList<>(n);
        for(int i = 0;i < n;i ++){
            stacks.add(handler.getStackInSlot(i).copy());
        }
        if(mainOutput != ItemStack.EMPTY){
            int needToInput = mainOutput.getCount() * repeat;
            for(int i = 0;i < n;i ++){
                if(stacks.get(i) == ItemStack.EMPTY){
                    int added = Math.min(needToInput, mainOutput.getMaxStackSize());
                    stacks.set(i, mainOutput.copyWithCount(added));
                    needToInput -= added;
                }
                else if(ItemStack.isSameItemSameComponents(mainOutput, stacks.get(i))){
                    ItemStack stack = stacks.get(i);
                    int added = Math.min(needToInput, stack.getMaxStackSize() - stack.getCount());
                    stack.grow(added);
                    needToInput -= added;
                }
                if(needToInput == 0) break;
            }
            if(needToInput > 0) return false;
        }
        if(subOutput != ItemStack.EMPTY){
            int needToInput = subOutput.getCount() * repeat;
            for(int i = 0;i < n;i ++){
                if(stacks.get(i) == ItemStack.EMPTY){
                    int added = Math.min(needToInput, subOutput.getMaxStackSize());
                    stacks.set(i, subOutput.copyWithCount(added));
                    needToInput -= added;
                }
                else if(ItemStack.isSameItemSameComponents(subOutput, stacks.get(i))){
                    ItemStack stack = stacks.get(i);
                    int added = Math.min(needToInput, stack.getMaxStackSize() - stack.getCount());
                    stack.grow(added);
                    needToInput -= added;
                }
                if(needToInput == 0) break;
            }
            if(needToInput > 0) return false;
        }
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return IMRecipes.REFINING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return IMRecipes.REFINING_RECIPE.get();
    }

    public List<IngredientWithCount> getRequirements() {
        return requirements;
    }

    public ItemStack getMainOutput() {
        return mainOutput;
    }

    public int getSubOutputChance() {
        return subOutputChance;
    }

    public ItemStack getSubOutput() {
        return subOutput;
    }

    public int getRequiredTemperature() {
        return requiredTemperature;
    }

    public int getFuelConsumingSpeed() {
        return fuelConsumingSpeed;
    }
}
