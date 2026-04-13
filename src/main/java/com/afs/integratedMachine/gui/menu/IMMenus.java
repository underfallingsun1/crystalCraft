package com.afs.integratedMachine.gui.menu;

import com.afs.integratedMachine.utils.Meta;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class IMMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(
            Registries.MENU, Meta.MODID
    );

    public static final Supplier<MenuType<RefiningFurnaceMenu>> REFINING_FURNACE = MENUS.register(
            "refining_furnace", () -> new MenuType<>(RefiningFurnaceMenu::new, FeatureFlags.DEFAULT_FLAGS)
    );
}
