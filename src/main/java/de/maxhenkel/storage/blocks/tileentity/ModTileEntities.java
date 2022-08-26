package de.maxhenkel.storage.blocks.tileentity;

import de.maxhenkel.storage.Main;
import de.maxhenkel.storage.blocks.ModBlocks;
import de.maxhenkel.storage.blocks.tileentity.render.AdvancedShulkerBoxRenderer;
import de.maxhenkel.storage.blocks.tileentity.render.ModChestRenderer;
import de.maxhenkel.storage.blocks.tileentity.render.StorageBarrelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.event.RegistryEvent;

public class ModTileEntities {

    public static BlockEntityType<ModChestTileEntity> CHEST;

    public static BlockEntityType<ModBarrelTileEntity> BARREL;

    public static BlockEntityType<StorageBarrelTileEntity> STORAGE_BARREL;

    public static BlockEntityType<AdvancedShulkerBoxTileEntity> SHULKER_BOX;

    public static void registerTileEntities(RegistryEvent.Register<BlockEntityType<?>> event) {
        CHEST = BlockEntityType.Builder.of((pos, state) -> new ModChestTileEntity(null, null, pos, state),
                ModBlocks.OAK_CHEST,
                ModBlocks.SPRUCE_CHEST,
                ModBlocks.BIRCH_CHEST,
                ModBlocks.ACACIA_CHEST,
                ModBlocks.JUNGLE_CHEST,
                ModBlocks.DARK_OAK_CHEST,
                ModBlocks.CRIMSON_CHEST,
                ModBlocks.WARPED_CHEST,

                ModBlocks.OAK_CHEST_TIER_1,
                ModBlocks.SPRUCE_CHEST_TIER_1,
                ModBlocks.BIRCH_CHEST_TIER_1,
                ModBlocks.ACACIA_CHEST_TIER_1,
                ModBlocks.JUNGLE_CHEST_TIER_1,
                ModBlocks.DARK_OAK_CHEST_TIER_1,
                ModBlocks.CRIMSON_CHEST_TIER_1,
                ModBlocks.WARPED_CHEST_TIER_1,

                ModBlocks.OAK_CHEST_TIER_2,
                ModBlocks.SPRUCE_CHEST_TIER_2,
                ModBlocks.BIRCH_CHEST_TIER_2,
                ModBlocks.ACACIA_CHEST_TIER_2,
                ModBlocks.JUNGLE_CHEST_TIER_2,
                ModBlocks.DARK_OAK_CHEST_TIER_2,
                ModBlocks.CRIMSON_CHEST_TIER_2,
                ModBlocks.WARPED_CHEST_TIER_2,

                ModBlocks.OAK_CHEST_TIER_3,
                ModBlocks.SPRUCE_CHEST_TIER_3,
                ModBlocks.BIRCH_CHEST_TIER_3,
                ModBlocks.ACACIA_CHEST_TIER_3,
                ModBlocks.JUNGLE_CHEST_TIER_3,
                ModBlocks.DARK_OAK_CHEST_TIER_3,
                ModBlocks.CRIMSON_CHEST_TIER_3,
                ModBlocks.WARPED_CHEST_TIER_3
        ).build(null);
        CHEST.setRegistryName(new ResourceLocation(Main.MODID, "chest"));
        event.getRegistry().register(CHEST);

        BARREL = BlockEntityType.Builder.of((pos, state) -> new ModBarrelTileEntity(null, pos, state),
                ModBlocks.OAK_BARREL,
                ModBlocks.SPRUCE_BARREL,
                ModBlocks.BIRCH_BARREL,
                ModBlocks.ACACIA_BARREL,
                ModBlocks.JUNGLE_BARREL,
                ModBlocks.DARK_OAK_BARREL,
                ModBlocks.CRIMSON_BARREL,
                ModBlocks.WARPED_BARREL,

                ModBlocks.OAK_BARREL_TIER_1,
                ModBlocks.SPRUCE_BARREL_TIER_1,
                ModBlocks.BIRCH_BARREL_TIER_1,
                ModBlocks.ACACIA_BARREL_TIER_1,
                ModBlocks.JUNGLE_BARREL_TIER_1,
                ModBlocks.DARK_OAK_BARREL_TIER_1,
                ModBlocks.CRIMSON_BARREL_TIER_1,
                ModBlocks.WARPED_BARREL_TIER_1,

                ModBlocks.OAK_BARREL_TIER_2,
                ModBlocks.SPRUCE_BARREL_TIER_2,
                ModBlocks.BIRCH_BARREL_TIER_2,
                ModBlocks.ACACIA_BARREL_TIER_2,
                ModBlocks.JUNGLE_BARREL_TIER_2,
                ModBlocks.DARK_OAK_BARREL_TIER_2,
                ModBlocks.CRIMSON_BARREL_TIER_2,
                ModBlocks.WARPED_BARREL_TIER_2,

                ModBlocks.OAK_BARREL_TIER_3,
                ModBlocks.SPRUCE_BARREL_TIER_3,
                ModBlocks.BIRCH_BARREL_TIER_3,
                ModBlocks.ACACIA_BARREL_TIER_3,
                ModBlocks.JUNGLE_BARREL_TIER_3,
                ModBlocks.DARK_OAK_BARREL_TIER_3,
                ModBlocks.CRIMSON_BARREL_TIER_3,
                ModBlocks.WARPED_BARREL_TIER_3
        ).build(null);
        BARREL.setRegistryName(new ResourceLocation(Main.MODID, "barrel"));
        event.getRegistry().register(BARREL);

        STORAGE_BARREL = BlockEntityType.Builder.of(StorageBarrelTileEntity::new,
                ModBlocks.OAK_STORAGE_BARREL,
                ModBlocks.SPRUCE_STORAGE_BARREL,
                ModBlocks.BIRCH_STORAGE_BARREL,
                ModBlocks.ACACIA_STORAGE_BARREL,
                ModBlocks.JUNGLE_STORAGE_BARREL,
                ModBlocks.DARK_OAK_STORAGE_BARREL,
                ModBlocks.CRIMSON_STORAGE_BARREL,
                ModBlocks.WARPED_STORAGE_BARREL
        ).build(null);
        STORAGE_BARREL.setRegistryName(new ResourceLocation(Main.MODID, "storage_barrel"));
        event.getRegistry().register(STORAGE_BARREL);

        SHULKER_BOX = BlockEntityType.Builder.of(() -> new AdvancedShulkerBoxTileEntity(null),
                ModBlocks.WHITE_SHULKER_BOX,
                ModBlocks.ORANGE_SHULKER_BOX,
                ModBlocks.MAGENTA_SHULKER_BOX,
                ModBlocks.LIGHT_BLUE_SHULKER_BOX,
                ModBlocks.YELLOW_SHULKER_BOX,
                ModBlocks.LIME_SHULKER_BOX,
                ModBlocks.PINK_SHULKER_BOX,
                ModBlocks.GRAY_SHULKER_BOX,
                ModBlocks.LIGHT_GRAY_SHULKER_BOX,
                ModBlocks.CYAN_SHULKER_BOX,
                ModBlocks.PURPLE_SHULKER_BOX,
                ModBlocks.BLUE_SHULKER_BOX,
                ModBlocks.BROWN_SHULKER_BOX,
                ModBlocks.GREEN_SHULKER_BOX,
                ModBlocks.RED_SHULKER_BOX,
                ModBlocks.BLACK_SHULKER_BOX
        ).build(null);
        SHULKER_BOX.setRegistryName(new ResourceLocation(Main.MODID, "shulker_box"));
        event.getRegistry().register(SHULKER_BOX);
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientSetup() {
        BlockEntityRenderers.register(ModTileEntities.CHEST, ModChestRenderer::new);

        BlockEntityRenderers.register(ModTileEntities.SHULKER_BOX, AdvancedShulkerBoxRenderer::new);

        BlockEntityRenderers.register(ModTileEntities.STORAGE_BARREL, StorageBarrelRenderer::new);
    }
}
