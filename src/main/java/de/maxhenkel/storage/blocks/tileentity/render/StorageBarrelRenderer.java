package de.maxhenkel.storage.blocks.tileentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.storage.blocks.StorageBarrelBlock;
import de.maxhenkel.storage.blocks.tileentity.StorageBarrelTileEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StorageBarrelRenderer extends BlockEntityRenderer<StorageBarrelTileEntity> {

    private final ItemRenderer itemRenderer;
    private Minecraft minecraft;

    public StorageBarrelRenderer(BlockEntityRenderDispatcher renderer) {
        super(renderer);
        minecraft = Minecraft.getInstance();
        itemRenderer = minecraft.getItemRenderer();
    }

    @Override
    public void render(StorageBarrelTileEntity barrel, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        Direction direction = Direction.UP;
        if (barrel.hasLevel()) {
            BlockState blockstate = barrel.getLevel().getBlockState(barrel.getBlockPos());
            if (blockstate.getBlock() instanceof StorageBarrelBlock) {
                direction = blockstate.getValue(StorageBarrelBlock.PROPERTY_FACING);
                combinedLightIn = minecraft.levelRenderer.getLightColor(barrel.getLevel(), barrel.getBlockPos().relative(direction));
            }
        }


        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5D, 0.5D, 0.5D);
        matrixStackIn.mulPose(direction.getRotation());
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90.0F));
        matrixStackIn.translate(0D, 0D, -0.5D + 1D / 16D);

        ItemStack itemstack = barrel.getBarrelContent();
        if (!itemstack.isEmpty()) {
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            itemRenderer.renderStatic(itemstack, ItemTransforms.TransformType.FIXED, combinedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);
        }

        matrixStackIn.popPose();
    }
}