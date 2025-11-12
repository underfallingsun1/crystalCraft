package com.afs.crystal_craft.datagen.lang;

import com.afs.crystal_craft.utils.Utils;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class EnUs extends LanguageProvider {
    public static final String locale = "en_us";

    public EnUs(PackOutput output) {
        super(output, Utils.MODID, locale);
    }

    @Override
    protected void addTranslations() {

    }
}
