package com.afs.integratedMachine.recipe;

import com.afs.integratedMachine.utils.Meta;
import com.afs.integratedMachine.utils.Utils;
import com.afs.integratedMachine.recipe.utils.SimpleRecipeSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class IMRecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPES = DeferredRegister.create(
            Registries.RECIPE_TYPE, Meta.MODID
    );

    public static final DeferredHolder<RecipeType<?>, RecipeType<RefiningRecipe>> REFINING_RECIPE =
            RECIPES.register("refining", () -> RecipeType.simple(Utils.modLoc("refining")));

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(
            Registries.RECIPE_SERIALIZER, Meta.MODID
    );

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<RefiningRecipe>> REFINING_SERIALIZER =
            SERIALIZERS.register("refining", () -> new SimpleRecipeSerializer<>(RefiningRecipe.CODEC, RefiningRecipe.STREAM_CODEC));
}
