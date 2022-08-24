package de.maxhenkel.storage.blocks.tileentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.maxhenkel.storage.blocks.AdvancedShulkerBoxBlock;
import de.maxhenkel.storage.blocks.tileentity.AdvancedShulkerBoxTileEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdvancedShulkerBoxRenderer extends BlockEntityRenderer<AdvancedShulkerBoxTileEntity> {
    private final ModelPart base;
    private final ModelPart lid;

    public AdvancedShulkerBoxRenderer(BlockEntityRenderDispatcher renderer) {
        super(renderer);
        base = new ModelPart(64, 64, 0, 28);
        lid = new ModelPart(64, 64, 0, 0);
        lid.addBox(-8F, -16F, -8F, 16F, 12F, 16F);
        lid.setPos(0F, 24F, 0F);
        base.addBox(-8F, -4F, -8F, 16F, 4F, 16F);
        base.setPos(0F, 24F, 0F);
    }

    @Override
    public void render(AdvancedShulkerBoxTileEntity box, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        Direction direction = Direction.UP;
        if (box.hasLevel()) {
            BlockState blockstate = box.getLevel().getBlockState(box.getBlockPos());
            if (blockstate.getBlock() instanceof AdvancedShulkerBoxBlock) {
                direction = blockstate.getValue(AdvancedShulkerBoxBlock.FACING);
            }
        }

        Material material = ModAtlases.getShulkerBoxMaterial(box.getColor());

        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5D, 0.5D, 0.5D);
        matrixStackIn.scale(0.9995F, 0.9995F, 0.9995F);
        matrixStackIn.mulPose(direction.getRotation());
        matrixStackIn.scale(1F, -1F, -1F);
        matrixStackIn.translate(0D, -1D, 0D);
        VertexConsumer ivertexbuilder = material.buffer(bufferIn, RenderType::entityCutoutNoCull);
        base.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);

        float progress = box.getProgress(partialTicks);
        matrixStackIn.translate(0D, -progress * 0.5F, 0D);
        lid.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);

        matrixStackIn.popPose();
    }
}