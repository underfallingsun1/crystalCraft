package com.afs.integratedMachine.block.entity.render;

import com.afs.integratedMachine.block.entity.CompartmentControllerBlockEntity;
import com.afs.integratedMachine.utils.Utils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Matrix4f;

public class CompartmentControllerRender implements BlockEntityRenderer<CompartmentControllerBlockEntity> {

    private static final ResourceLocation FACE_TEXTURE = Utils.modLoc("textures/block/compartment_controller_face.png");

    @Override
    public void render(CompartmentControllerBlockEntity blockEntity, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        if (blockEntity.getLevel() == null) {
            return;
        }
        BlockState state = blockEntity.getBlockState();
        Direction facing = state.getValue(BlockStateProperties.FACING);

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(facing.getRotation());
        poseStack.translate(-0.5, 0.5, -0.5);

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(FACE_TEXTURE));
        Matrix4f pose = poseStack.last().pose();
        float z = 0.001F;

        int faceLight = packedLight | (15 << 4);

        consumer.addVertex(pose, 0, z, 0).setColor(-1).setUv(0, 0).setOverlay(packedOverlay)
                .setLight(faceLight).setNormal(poseStack.last(), 0, 0, 1);
        consumer.addVertex(pose, 1, z, 0).setColor(-1).setUv(1, 0).setOverlay(packedOverlay)
                .setLight(faceLight).setNormal(poseStack.last(), 0, 0, 1);
        consumer.addVertex(pose, 1, z, 1).setColor(-1).setUv(1, 1).setOverlay(packedOverlay)
                .setLight(faceLight).setNormal(poseStack.last(), 0, 0, 1);
        consumer.addVertex(pose, 0, z, 1).setColor(-1).setUv(0, 1).setOverlay(packedOverlay)
                .setLight(faceLight).setNormal(poseStack.last(), 0, 0, 1);

        poseStack.popPose();
    }
}
