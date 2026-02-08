package com.afs.integratedMachine.datagen.lang;

import com.afs.integratedMachine.utils.Utils;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import static com.afs.integratedMachine.utils.LangComps.*;

public class ZhCn extends LanguageProvider {
    public ZhCn(PackOutput output) {
        super(output, Utils.MODID, "zh_cn");
    }

    @Override
    protected void addTranslations() {
        add(TAB_TITLE.key(), "集成机械");
    }
}
