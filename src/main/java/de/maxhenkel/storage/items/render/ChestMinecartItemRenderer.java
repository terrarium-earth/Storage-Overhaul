package de.maxhenkel.storage.items.render;

import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.storage.entity.ModChestMinecartEntity;
import de.maxhenkel.storage.entity.ModEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class ChestMinecartItemRenderer extends BlockEntityWithoutLevelRenderer {

    private Minecraft minecraft;
    private MinecartRenderer renderer;
    private Supplier<Block> block;
    private ModChestMinecartEntity entity;

    public ChestMinecartItemRenderer(Supplier<Block> block) {
        super();
        this.block = block;
        minecraft = Minecraft.getInstance();
    }

    @Override
    public void renderByItem(ItemStack itemStackIn, ItemTransforms.TransformType transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if (renderer == null) {
            renderer = new MinecartRenderer(minecraft.getEntityRenderDispatcher());
        }
        if (entity == null) {
            entity = ModEntities.CHEST_MINECART.create(minecraft.level);
            entity.setBlock(block.get());
        }
        renderer.render(entity, 0F, 1F, matrixStackIn, bufferIn, combinedLightIn);
    }
}
