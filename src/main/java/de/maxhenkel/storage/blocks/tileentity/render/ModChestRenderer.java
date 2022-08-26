package de.maxhenkel.storage.blocks.tileentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.maxhenkel.storage.ChestTier;
import de.maxhenkel.storage.blocks.ModBlocks;
import de.maxhenkel.storage.blocks.ModChestBlock;
import de.maxhenkel.storage.blocks.tileentity.ModChestTileEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.core.Direction;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModChestRenderer extends ChestRenderer<ModChestTileEntity> {

    private final ModelPart singleLid;
    private final ModelPart singleBottom;
    private final ModelPart singleLatchBase;
    private final ModelPart singleLatchTier1;
    private final ModelPart singleLatchTier2;
    private final ModelPart singleLatchTier3;
    private final ModelPart rightLid;
    private final ModelPart rightBottom;
    private final ModelPart rightLatchBase;
    private final ModelPart rightLatchTier1;
    private final ModelPart rightLatchTier2;
    private final ModelPart rightLatchTier3;
    private final ModelPart leftLid;
    private final ModelPart leftBottom;
    private final ModelPart leftLatchBase;
    private final ModelPart leftLatchTier1;
    private final ModelPart leftLatchTier2;
    private final ModelPart leftLatchTier3;

    public ModChestRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn);
        singleBottom = new ModelPart(64, 64, 0, 19);
        singleBottom.addBox(1F, 0F, 1F, 14F, 10F, 14F, 0F);
        singleLid = new ModelPart(64, 64, 0, 0);
        singleLid.addBox(1F, 0F, 0F, 14F, 5F, 14F, 0F);
        singleLid.y = 9F;
        singleLid.z = 1F;
        singleLatchBase = new ModelPart(64, 64, 0, 0);
        singleLatchBase.addBox(7F, -1F, 15F, 2F, 4F, 1F, 0F);
        singleLatchBase.y = 8F;
        singleLatchTier1 = new ModelPart(64, 64, 8, 0);
        singleLatchTier1.addBox(7F, -1F, 15F, 2F, 4F, 1F, 0F);
        singleLatchTier1.y = 8F;
        singleLatchTier2 = new ModelPart(64, 64, 0, 8);
        singleLatchTier2.addBox(7F, -1F, 15F, 2F, 4F, 1F, 0F);
        singleLatchTier2.y = 8F;
        singleLatchTier3 = new ModelPart(64, 64, 8, 8);
        singleLatchTier3.addBox(7F, -1F, 15F, 2F, 4F, 1F, 0F);
        singleLatchTier3.y = 8F;
        rightBottom = new ModelPart(64, 64, 0, 19);
        rightBottom.addBox(1F, 0F, 1F, 15F, 10F, 14F, 0F);
        rightLid = new ModelPart(64, 64, 0, 0);
        rightLid.addBox(1F, 0F, 0F, 15F, 5F, 14F, 0F);
        rightLid.y = 9F;
        rightLid.z = 1F;
        rightLatchBase = new ModelPart(64, 64, 0, 0);
        rightLatchBase.addBox(15F, -1F, 15F, 1F, 4F, 1F, 0F);
        rightLatchBase.y = 8F;
        rightLatchTier1 = new ModelPart(64, 64, 8, 0);
        rightLatchTier1.addBox(15F, -1F, 15F, 1F, 4F, 1F, 0F);
        rightLatchTier1.y = 8F;
        rightLatchTier2 = new ModelPart(64, 64, 0, 8);
        rightLatchTier2.addBox(15F, -1F, 15F, 1F, 4F, 1F, 0F);
        rightLatchTier2.y = 8F;
        rightLatchTier3 = new ModelPart(64, 64, 8, 8);
        rightLatchTier3.addBox(15F, -1F, 15F, 1F, 4F, 1F, 0F);
        rightLatchTier3.y = 8F;
        leftBottom = new ModelPart(64, 64, 0, 19);
        leftBottom.addBox(0F, 0F, 1F, 15F, 10F, 14F, 0F);
        leftLid = new ModelPart(64, 64, 0, 0);
        leftLid.addBox(0F, 0F, 0F, 15F, 5F, 14F, 0F);
        leftLid.y = 9F;
        leftLid.z = 1F;
        leftLatchBase = new ModelPart(64, 64, 0, 0);
        leftLatchBase.addBox(0F, -1F, 15F, 1F, 4F, 1F, 0F);
        leftLatchBase.y = 8F;
        leftLatchTier1 = new ModelPart(64, 64, 8, 0);
        leftLatchTier1.addBox(0F, -1F, 15F, 1F, 4F, 1F, 0F);
        leftLatchTier1.y = 8F;
        leftLatchTier2 = new ModelPart(64, 64, 0, 8);
        leftLatchTier2.addBox(0F, -1F, 15F, 1F, 4F, 1F, 0F);
        leftLatchTier2.y = 8F;
        leftLatchTier3 = new ModelPart(64, 64, 8, 8);
        leftLatchTier3.addBox(0F, -1F, 15F, 1F, 4F, 1F, 0F);
        leftLatchTier3.y = 8F;
    }

    @Override
    public void render(ModChestTileEntity chest, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        BlockState blockstate = chest.hasLevel() ? chest.getBlockState() : ModBlocks.OAK_CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH);
        ChestType chesttype = blockstate.hasProperty(ChestBlock.TYPE) ? blockstate.getValue(ChestBlock.TYPE) : ChestType.SINGLE;
        Block block = blockstate.getBlock();
        if (!(block instanceof ModChestBlock)) {
            return;
        }
        ModChestBlock chestBlock = (ModChestBlock) block;
        matrixStackIn.pushPose();
        float rotation = blockstate.getValue(ChestBlock.FACING).toYRot();
        matrixStackIn.translate(0.5D, 0.5D, 0.5D);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-rotation));
        matrixStackIn.translate(-0.5D, -0.5D, -0.5D);
        DoubleBlockCombiner.NeighborCombineResult<? extends ModChestTileEntity> callback;
        if (chest.hasLevel()) {
            callback = chestBlock.getMergerCallback(blockstate, chest.getLevel(), chest.getBlockPos(), true);
        } else {
            callback = DoubleBlockCombiner.Combiner::acceptNone;
        }

        float lidAngle = callback.apply(ModChestBlock.lidAngleCallback(chest)).get(partialTicks);
        lidAngle = 1F - lidAngle;
        lidAngle = 1F - lidAngle * lidAngle * lidAngle;
        int i = callback.apply(new BrightnessCombiner<>()).applyAsInt(combinedLightIn);
        Material material = getMaterial(chest, chesttype);
        VertexConsumer ivertexbuilder = material.buffer(bufferIn, RenderType::entityCutout);
        if (chesttype == ChestType.LEFT) {
            renderModels(matrixStackIn, ivertexbuilder, leftLid, getDoubleLatchLeft(chest.getTier()), leftBottom, lidAngle, i, combinedOverlayIn);
        } else if (chesttype == ChestType.RIGHT) {
            renderModels(matrixStackIn, ivertexbuilder, rightLid, getDoubleLatchRight(chest.getTier()), rightBottom, lidAngle, i, combinedOverlayIn);
        } else {
            renderModels(matrixStackIn, ivertexbuilder, singleLid, getSingleLatch(chest.getTier()), singleBottom, lidAngle, i, combinedOverlayIn);
        }

        matrixStackIn.popPose();
    }

    private ModelPart getSingleLatch(ChestTier tier) {
        switch (tier) {
            case BASE_TIER:
                return singleLatchBase;
            case TIER_1:
                return singleLatchTier1;
            case TIER_2:
                return singleLatchTier2;
            case TIER_3:
                return singleLatchTier3;
        }
        return singleLatchBase;
    }

    private ModelPart getDoubleLatchRight(ChestTier tier) {
        switch (tier) {
            case BASE_TIER:
                return rightLatchBase;
            case TIER_1:
                return rightLatchTier1;
            case TIER_2:
                return rightLatchTier2;
            case TIER_3:
                return rightLatchTier3;
        }
        return rightLatchBase;
    }

    private ModelPart getDoubleLatchLeft(ChestTier tier) {
        switch (tier) {
            case BASE_TIER:
                return leftLatchBase;
            case TIER_1:
                return leftLatchTier1;
            case TIER_2:
                return leftLatchTier2;
            case TIER_3:
                return leftLatchTier3;
        }
        return leftLatchBase;
    }

    private void renderModels(PoseStack matrixStackIn, VertexConsumer bufferIn, ModelPart chestLid, ModelPart chestLatch, ModelPart chestBottom, float lidAngle, int combinedLightIn, int combinedOverlayIn) {
        chestLid.xRot = -(lidAngle * ((float) Math.PI / 2F));
        chestLatch.xRot = chestLid.xRot;
        chestLid.render(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        chestLatch.render(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        chestBottom.render(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
    }

    @Override
    protected Material getMaterial(ModChestTileEntity tileEntity, ChestType chestType) {
        return ModAtlases.getChestMaterial(tileEntity.getWoodType(), chestType);
    }
}
