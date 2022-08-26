package de.maxhenkel.storage.items.render;

import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.storage.ChestTier;
import de.maxhenkel.storage.blocks.tileentity.ModChestTileEntity;
import de.maxhenkel.storage.blocks.tileentity.render.ModChestRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChestItemRenderer extends BlockEntityWithoutLevelRenderer {

    private ModChestRenderer renderer;
    private ModChestTileEntity tileEntity;

    public ChestItemRenderer(WoodType woodType, ChestTier tier) {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        tileEntity = new ModChestTileEntity(woodType, tier);
    }

    @Override
    public void renderByItem(ItemStack itemStackIn, ItemTransforms.TransformType transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if (renderer == null) {
            renderer = new ModChestRenderer(BlockEntityRenderDispatcher.instance);
        }
        renderer.render(tileEntity, 1F, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
    }
}
