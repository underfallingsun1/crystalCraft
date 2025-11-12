package com.afs.crystal_craft.datagen.lang;

import com.afs.crystal_craft.utils.Utils;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ZhCn extends LanguageProvider {
    public static final String locale = "zh_cn";

    public ZhCn(PackOutput output) {
        super(output, Utils.MODID, locale);
    }

    @Override
    protected void addTranslations() {

    }
}
