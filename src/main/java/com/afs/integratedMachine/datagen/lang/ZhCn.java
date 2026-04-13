package com.afs.integratedMachine.datagen.lang;

import com.afs.integratedMachine.block.IMBlocks;
import com.afs.integratedMachine.item.IMItems;
import com.afs.integratedMachine.utils.Meta;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import static com.afs.integratedMachine.utils.LangComps.*;

public class ZhCn extends LanguageProvider {
    public ZhCn(PackOutput output) {
        super(output, Meta.MODID, "zh_cn");
    }

    @Override
    protected void addTranslations() {
        add(TAB_TITLE.key(), "集成机械");

        add(REFINING_FURNACE_TITLE.key(), "精炼炉");
        add(REFINING_FURNACE_FUEL_TIP.key(), "燃值: %1$s / %2$s (%3$s %%)");
        add(REFINING_FURNACE_TEMPERATURE_TIP.key(), "温度: %1$sK / %2$sK");

        add(IMItems.POWERED_INGOT.get(), "充能金属锭");
        add(IMItems.SHINING_INGOT.get(), "发光金属锭");
        add(IMItems.END_INGOT.get(), "末影金属锭");
        add(IMItems.CRYSTALLIZED_INGOT.get(), "晶化金属锭");
        add(IMItems.EXTREME_INGOT.get(), "终极金属锭");
        add(IMItems.STEEL_INGOT.get(), "钢锭");

        add(IMBlocks.REFINING_FURNACE.get(), "精炼炉");
    }
}
