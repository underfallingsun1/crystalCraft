package com.afs.integratedMachine.datagen.lang;

import com.afs.integratedMachine.utils.LangComps;
import com.afs.integratedMachine.utils.Utils;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import static com.afs.integratedMachine.utils.LangComps.*;

public class EnUs extends LanguageProvider {
    public EnUs(PackOutput output) {
        super(output, Utils.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(TAB_TITLE.key(), "Integrated Machine");
    }
}
