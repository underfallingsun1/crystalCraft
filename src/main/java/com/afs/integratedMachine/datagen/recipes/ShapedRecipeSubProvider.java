package com.afs.integratedMachine.datagen.recipes;

import com.afs.integratedMachine.datagen.advancement.AdvancementUtils;
import com.afs.integratedMachine.item.IMItems;
import com.afs.integratedMachine.utils.Utils;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

public class ShapedRecipeSubProvider {
    public static void run(RecipeOutput output){
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, IMItems.REFINING_FURNACE)
                .pattern("AAA")
                .pattern("CBC")
                .pattern("DCD")
                .define('A', Tags.Items.OBSIDIANS)
                .define('C', Items.SMOOTH_STONE)
                .define('D', Tags.Items.STORAGE_BLOCKS_IRON)
                .define('B', Items.BLAST_FURNACE)
                .unlockedBy("has_the_item", AdvancementUtils.hasItem(Items.BLAST_FURNACE))
                .save(output, Utils.modLoc("refining_furnace"));
    }
}
