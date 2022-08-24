package de.maxhenkel.storage.items.render;

import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.storage.blocks.tileentity.AdvancedShulkerBoxTileEntity;
import de.maxhenkel.storage.blocks.tileentity.render.AdvancedShulkerBoxRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public class AdvancedShulkerBoxItemRenderer extends BlockEntityWithoutLevelRenderer {

    private AdvancedShulkerBoxRenderer renderer;
    private AdvancedShulkerBoxTileEntity tileEntity;


    public AdvancedShulkerBoxItemRenderer(DyeColor color) {
        tileEntity = new AdvancedShulkerBoxTileEntity(color);
    }

    @Override
    public void renderByItem(ItemStack itemStackIn, ItemTransforms.TransformType transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if (renderer == null) {
            renderer = new AdvancedShulkerBoxRenderer(BlockEntityRenderDispatcher.instance);
        }
        renderer.render(tileEntity, 1F, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
    }
}
