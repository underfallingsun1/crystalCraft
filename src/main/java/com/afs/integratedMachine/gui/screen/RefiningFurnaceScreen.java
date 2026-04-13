package com.afs.integratedMachine.gui.screen;

import com.afs.integratedMachine.block.entity.RefiningFurnaceBlockEntity;
import com.afs.integratedMachine.gui.menu.RefiningFurnaceMenu;
import com.afs.integratedMachine.utils.LangComps;
import com.afs.integratedMachine.utils.Utils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;
import java.util.Optional;

public class RefiningFurnaceScreen extends AbstractContainerScreen<RefiningFurnaceMenu> {
    private static final ResourceLocation BACKGROUND_LOC = Utils.modLoc("textures/gui/menu/refining_furnace.png");
    private static final ResourceLocation BACKGROUND_BAR_LOC = Utils.modLoc("textures/gui/menu/refining_furnace_bar.png");
    private static final int DOWN_COLOR = 0xffcc0000;
    private static final int UP_COLOR = 0xffcccc80;

    public RefiningFurnaceScreen(RefiningFurnaceMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        titleLabelX = 68;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int fuel = menu.getStoredFuel();
        int temperature = menu.getTemperature();
        int maxFuel = RefiningFurnaceBlockEntity.FUEL_CAPACITY;
        float fuelPer = ((float) fuel) / maxFuel;

        renderTooltip(guiGraphics, mouseX, mouseY);

        int xPos = mouseX - leftPos;
        int yPos = mouseY - topPos;
        if(this.menu.getCarried().isEmpty() && (xPos >= 8 && xPos <= 22 && yPos >= 8 && yPos <= 48)){
            String fuelPercentage = String.format("%.1f", fuelPer * 100);
            guiGraphics.renderTooltip(font,
                    List.of(
                            LangComps.REFINING_FURNACE_FUEL_TIP.apply(fuel, maxFuel, fuelPercentage),
                            LangComps.REFINING_FURNACE_TEMPERATURE_TIP.apply(temperature, RefiningFurnaceBlockEntity.getMaxTemperature(fuel))
                            ),
                    Optional.empty(),
                    mouseX, mouseY);
        }
        if(fuelPer > 0.0f) {
            int fuelBarUpY = topPos + 48 - (int) (40 * fuelPer);
            int upColor = lerpColor(DOWN_COLOR, UP_COLOR, fuelPer);
            guiGraphics.fillGradient(leftPos + 8, fuelBarUpY, leftPos + 23, topPos + 49, upColor, DOWN_COLOR);
        }
        float progress = menu.getProgress();
        if(progress > 0.01){
            int len = (int) (15 * progress);
            guiGraphics.blit(BACKGROUND_BAR_LOC, leftPos + 99, topPos + 36, 1, 0, len, 16, 16, 16);
        }
    }

    private int lerpColor(int colorA, int colorB, float t) {
        int r = (int) (((colorA >> 16) & 0xff) * (1 - t) + ((colorB >> 16) & 0xff) * t);
        int g = (int) (((colorA >> 8) & 0xff) * (1 - t) + ((colorB >> 8) & 0xff) * t);
        int b = (int) ((colorA & 0xff) * (1 - t) + (colorB & 0xff) * t);
        return 0xff000000 | (r << 16) | (g << 8) | b;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND_LOC, leftPos, topPos,0, 0, this.imageWidth, this.imageHeight);
    }
}
