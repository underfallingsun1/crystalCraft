package com.afs.integratedMachine.recipe.utils.outputEntry;

import com.afs.integratedMachine.recipe.utils.randomSet.RandomSetHandle;
import com.afs.integratedMachine.utils.SimpleSerializer;
import com.afs.integratedMachine.utils.Utils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.checkerframework.checker.units.qual.C;

import java.util.List;
import java.util.Map;

public abstract class AbstractItemOutputEntry extends AbstractOutputEntry implements OutputEntryEffect.Item{
    protected final ItemStack stack;

    public AbstractItemOutputEntry(ItemStack stack){
        this.stack = stack;
    }

    @Override
    public OutputType getEntryType() {
        return OutputType.ITEM;
    }

    @Override
    public void addPossibleItem(List<ItemStack> result) {
        Utils.addStack(result, stack);
    }

    public ItemStack getStack() {
        return stack;
    }

    public static class Simple extends AbstractItemOutputEntry{
        public Simple(ItemStack stack) {
            super(stack);
        }

        @Override
        public SimpleSerializer<Simple> type() {
            return AbstractOutputEntry.ITEM_OUTPUT.get();
        }

        public static final MapCodec<Simple> CODEC = ItemStack.CODEC.xmap(Simple::new, Simple::getStack).fieldOf("stack");

        public static final StreamCodec<RegistryFriendlyByteBuf, Simple> STREAM_CODEC =
                ItemStack.STREAM_CODEC.map(Simple::new, Simple::getStack);

        public static final SimpleSerializer<Simple> SERIALIZER = new SimpleSerializer<>(CODEC, STREAM_CODEC);

        @Override
        public void addItem(List<ItemStack> result, RandomSetHandle random, Map<String, Integer> config) {
            Utils.addStack(result, stack);
        }
    }

    public static class Chanced extends AbstractItemOutputEntry{
        private final String randomId;

        public Chanced(ItemStack stack, String randomId) {
            super(stack);
            this.randomId = randomId;
        }

        public String getRandomId() {
            return randomId;
        }

        @Override
        public SimpleSerializer<? extends AbstractOutputEntry> type() {
            return AbstractOutputEntry.CHANCED_ITEM_OUTPUT.get();
        }

        @Override
        public void addItem(List<ItemStack> result, RandomSetHandle random, Map<String, Integer> config) {
            if(random.get(randomId)){
                Utils.addStack(result, stack);
            }
        }

        public static final MapCodec<Chanced> CODEC = RecordCodecBuilder.mapCodec(
                inst -> inst.group(
                    ItemStack.CODEC.fieldOf("stack").forGetter(Chanced::getStack),
                    Codec.STRING.fieldOf("random_set_id").forGetter(Chanced::getRandomId)
                ).apply(inst, Chanced::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, Chanced> STREAM_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC, Chanced::getStack,
                ByteBufCodecs.STRING_UTF8, Chanced::getRandomId,
                Chanced::new
        );

        public static final SimpleSerializer<Chanced> SERIALIZER = new SimpleSerializer<>(CODEC, STREAM_CODEC);
    }
}
