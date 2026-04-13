package com.afs.integratedMachine.datagen.recipes.builder;

import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class BasicRecipeBuilder implements RecipeBuilder {
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    @Override
    public RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        criteria.put(name, criterion);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String groupName) {
        return this;
    }

    @Override
    public Item getResult() {
        return Items.STONE;
    }
}
