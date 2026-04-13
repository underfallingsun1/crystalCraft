package com.afs.integratedMachine.datagen.lang;

import com.afs.integratedMachine.block.IMBlocks;
import com.afs.integratedMachine.item.IMItems;
import com.afs.integratedMachine.utils.Meta;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import static com.afs.integratedMachine.utils.LangComps.*;

public class EnUs extends LanguageProvider {
    public EnUs(PackOutput output) {
        super(output, Meta.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(TAB_TITLE.key(), "Integrated Machine");

        add(REFINING_FURNACE_TITLE.key(), "Refining Furnace");
        add(REFINING_FURNACE_FUEL_TIP.key(), "Fuel: %1$s / %2$s (%3$s %%)");
        add(REFINING_FURNACE_TEMPERATURE_TIP.key(), "Temperature: %1$sK / %2$sK");

        add(IMItems.POWERED_INGOT.get(), "Powered Ingot");
        add(IMItems.SHINING_INGOT.get(), "Shining Ingot");
        add(IMItems.END_INGOT.get(), "End Ingot");
        add(IMItems.CRYSTALLIZED_INGOT.get(), "Crystallized Ingot");
        add(IMItems.EXTREME_INGOT.get(), "Extreme Ingot");
        add(IMItems.STEEL_INGOT.get(), "Steel Ingot");

        add(IMBlocks.REFINING_FURNACE.get(), "Refining Furnace");
    }
}
