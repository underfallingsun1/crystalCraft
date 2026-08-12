package com.afs.integratedMachine.datagen.model.builder;

import com.afs.integratedMachine.client.model.connectModel.ConnectModelLoader;
import com.afs.integratedMachine.client.model.connectModel.ConnectPredicate;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ibm.icu.impl.duration.impl.DataRecord;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.ArrayList;
import java.util.List;

public class ConnectedModelBuilder extends CustomLoaderBuilder<BlockModelBuilder> {
    public ConnectedModelBuilder(BlockModelBuilder parent, ExistingFileHelper existingFileHelper) {
        super(ConnectModelLoader.ID, parent, existingFileHelper, false);
    }

    private String textureSet;
    private List<ConnectPredicate> predicates = new ArrayList<>();

    public ConnectedModelBuilder setTextureSet(String textureSet){
        this.textureSet = textureSet;
        return this;
    }

    public ConnectedModelBuilder addPredicates(ConnectPredicate predicate){
        this.predicates.add(predicate);
        return this;
    }

    @Override
    public JsonObject toJson(JsonObject json) {
        json.addProperty("texture_set", textureSet);
        if(predicates.size() == 1){
            DataResult<JsonElement> res = ConnectPredicate.CODEC.encode(predicates.getFirst(), JsonOps.INSTANCE, new JsonObject());
            json.add("requirements", res.getOrThrow());
        }
        else if (predicates.size() >= 2){
            JsonArray jArray = new JsonArray();
            for(ConnectPredicate cp: predicates){
                DataResult<JsonElement> res = ConnectPredicate.CODEC.encode(cp, JsonOps.INSTANCE, new JsonObject());
                jArray.add(res.getOrThrow());
            }
            json.add("requirements", jArray);
        }
        return super.toJson(json);
    }
}
