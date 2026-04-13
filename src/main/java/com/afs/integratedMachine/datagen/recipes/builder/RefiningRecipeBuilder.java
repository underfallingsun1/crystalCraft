package com.afs.integratedMachine.datagen.recipes.builder;

import com.afs.integratedMachine.recipe.RefiningRecipe;
import com.afs.integratedMachine.recipe.utils.IngredientWithCount;
import com.afs.integratedMachine.utils.Meta;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;

public class RefiningRecipeBuilder extends BasicRecipeBuilder{
    private final ItemStack output;
    private final List<IngredientWithCount> ingredients = new ArrayList<>();
    private ItemStack subOutput = ItemStack.EMPTY;
    private int subOutputChance = 0;
    private int requiredTemperature = 600;
    private int fuelConsumedSpeed = 1;

    public RefiningRecipeBuilder(ItemStack result){
        output = result;
    }

    public RefiningRecipeBuilder(Item item){
        output = new ItemStack(item, 1);
    }

    public RefiningRecipeBuilder addItem(ItemLike item, int count){
        ingredients.add(new IngredientWithCount(Ingredient.of(item), count));
        return this;
    }

    public RefiningRecipeBuilder addItem(TagKey<Item> item, int count){
        ingredients.add(new IngredientWithCount(Ingredient.of(item), count));
        return this;
    }

    public RefiningRecipeBuilder setSubOutput(ItemStack stack, int chance){
        subOutput = stack;
        subOutputChance = chance;
        return this;
    }

    public RefiningRecipeBuilder setSubOutput(Item item, int chance){
        return setSubOutput(new ItemStack(item), chance);
    }

    public RefiningRecipeBuilder setSubOutput(ItemStack stack, double chance){
        return setSubOutput(stack, (int)(chance * 10000));
    }

    public RefiningRecipeBuilder setSubOutput(Item item, double chance){
        return setSubOutput(new ItemStack(item), (int)(chance * 10000));
    }

    public RefiningRecipeBuilder setTemperature(int temperature){
        requiredTemperature = temperature;
        return this;
    }

    public RefiningRecipeBuilder setFuelSpeed(int speed){
        fuelConsumedSpeed = speed;
        return this;
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        Advancement.Builder builder = output.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(builder::addCriterion);
        RefiningRecipe recipe = new RefiningRecipe(ingredients, this.output, subOutput, subOutputChance, requiredTemperature, fuelConsumedSpeed);
        output.accept(id, recipe, builder.build(id.withPrefix("recipes/")));
    }
}
