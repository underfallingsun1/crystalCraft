package com.afs.integratedMachine.datagen.recipes;

import com.afs.integratedMachine.datagen.advancement.AdvancementUtils;
import com.afs.integratedMachine.datagen.recipes.builder.RefiningRecipeBuilder;
import com.afs.integratedMachine.item.IMItems;
import com.afs.integratedMachine.utils.Utils;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

public class RefiningRecipeSubProvider {
    public static void run(RecipeOutput output){
        new RefiningRecipeBuilder(new ItemStack(IMItems.POWERED_INGOT.get(), 4))
                .addItem(Tags.Items.STORAGE_BLOCKS_REDSTONE, 1)
                .addItem(Tags.Items.INGOTS_GOLD, 4)
                .unlockedBy("has_the_item", AdvancementUtils.hasItem(Tags.Items.STORAGE_BLOCKS_REDSTONE))
                .save(output, Utils.modLoc("refining_powered_ingot"));
        new RefiningRecipeBuilder(new ItemStack(IMItems.STEEL_INGOT.get(), 15))
                .setSubOutput(new ItemStack(IMItems.STEEL_INGOT.get(), 2), 0.5)
                .addItem(Tags.Items.STORAGE_BLOCKS_IRON, 2)
                .setTemperature(1000)
                .setFuelSpeed(2)
                .unlockedBy("has_the_item", AdvancementUtils.hasItem(Tags.Items.STORAGE_BLOCKS_IRON))
                .save(output, Utils.modLoc("refining_steel"));
        new RefiningRecipeBuilder(new ItemStack(Items.SMOOTH_STONE, 4))
                .setSubOutput(new ItemStack(Items.FLINT, 2), 0.25)
                .addItem(Tags.Items.COBBLESTONES, 1)
                .addItem(Tags.Items.STONES, 1)
                .addItem(Tags.Items.GRAVELS, 1)
                .setTemperature(500)
                .unlockedBy("has_the_item", AdvancementUtils.hasItem(Items.SMOOTH_STONE))
                .save(output, Utils.modLoc("refining_smooth_stone"));
    }
}
