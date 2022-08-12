package de.maxhenkel.storage.blocks.tileentity.render;

import de.maxhenkel.storage.Main;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Main.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModAtlases {
    private static final ResourceLocation CHEST_OAK_LOCATION = new ResourceLocation(Main.MODID, "entity/chest/oak");
    private static final ResourceLocation CHEST_OAK_LEFT_LOCATION = new ResourceLocation(Main.MODID, "entity/chest/oak_left");
    private static final ResourceLocation CHEST_OAK_RIGHT_LOCATION = new ResourceLocation(Main.MODID, "entity/chest/oak_right");

    private static final ResourceLocation CHEST_SPRUCE_LOCATION = new ResourceLocation(Main.MODID, "entity/chest/spruce");
    private static final ResourceLocation CHEST_SPRUCE_LEFT_LOCATION = new ResourceLocation(Main.MODID, "entity/chest/spruce_left");
    private static final ResourceLocation CHEST_SPRUCE_RIGHT_LOCATION = new ResourceLocation(Main.MODID, "entity/chest/spruce_right");

    private static final ResourceLocation CHEST_BIRCH_LOCATION = new ResourceLocation(Main.MODID, "entity/chest/birch");
    private static final ResourceLocation CHEST_BIRCH_LEFT_LOCATION = new ResourceLocation(Main.MODID, "entity/chest/birch_left");
    private static final ResourceLocation CHEST_BIRCH_RIGHT_LOCATION = new ResourceLocation(Main.MODID, "entity/chest/birch_right");

    private static final ResourceLocation CHEST_ACACIA_LOCATION = new ResourceLocation(Main.MODID, "entity/chest/acacia");
    private static final ResourceLocation CHEST_ACACIA_LEFT_LOCATION = new ResourceLocation(Main.MODID, "entity/chest/acacia_left");
    private static final ResourceLocation CHEST_ACACIA_RIGHT_LOCATION = new ResourceLocation(Main.MODID, "entity/chest/acacia_right");

    private static final ResourceLocation CHEST_JUNGLE_LOCATION = new ResourceLocation(Main.MODID, "entity/chest/jungle");
    private static final ResourceLocation CHEST_JUNGLE_LEFT_LOCATION = new ResourceLocation(Main.MODID, "entity/chest/jungle_left");
    private static final ResourceLocation CHEST_JUNGLE_RIGHT_LOCATION = new ResourceLocation(Main.MODID, "entity/chest/jungle_right");

    private static final ResourceLocation CHEST_DARK_OAK_LOCATION = new ResourceLocation(Main.MODID, "entity/chest/dark_oak");
    private static final ResourceLocation CHEST_DARK_OAK_LEFT_LOCATION = new ResourceLocation(Main.MODID, "entity/chest/dark_oak_left");
    private static final ResourceLocation CHEST_DARK_OAK_RIGHT_LOCATION = new ResourceLocation(Main.MODID, "entity/chest/dark_oak_right");

    private static final ResourceLocation CHEST_CRIMSON_LOCATION = new ResourceLocation(Main.MODID, "entity/chest/crimson");
    private static final ResourceLocation CHEST_CRIMSON_LEFT_LOCATION = new ResourceLocation(Main.MODID, "entity/chest/crimson_left");
    private static final ResourceLocation CHEST_CRIMSON_RIGHT_LOCATION = new ResourceLocation(Main.MODID, "entity/chest/crimson_right");

    private static final ResourceLocation CHEST_WARPED_LOCATION = new ResourceLocation(Main.MODID, "entity/chest/warped");
    private static final ResourceLocation CHEST_WARPED_LEFT_LOCATION = new ResourceLocation(Main.MODID, "entity/chest/warped_left");
    private static final ResourceLocation CHEST_WARPED_RIGHT_LOCATION = new ResourceLocation(Main.MODID, "entity/chest/warped_right");

    private static final ResourceLocation WHITE_SHULKER_BOX_LOCATION = new ResourceLocation(Main.MODID, "entity/shulkerbox/shulker_white");
    private static final ResourceLocation ORANGE_SHULKER_BOX_LOCATION = new ResourceLocation(Main.MODID, "entity/shulkerbox/shulker_orange");
    private static final ResourceLocation MAGENTA_SHULKER_BOX_LOCATION = new ResourceLocation(Main.MODID, "entity/shulkerbox/shulker_magenta");
    private static final ResourceLocation LIGHT_BLUE_SHULKER_BOX_LOCATION = new ResourceLocation(Main.MODID, "entity/shulkerbox/shulker_light_blue");
    private static final ResourceLocation YELLOW_SHULKER_BOX_LOCATION = new ResourceLocation(Main.MODID, "entity/shulkerbox/shulker_yellow");
    private static final ResourceLocation LIME_SHULKER_BOX_LOCATION = new ResourceLocation(Main.MODID, "entity/shulkerbox/shulker_lime");
    private static final ResourceLocation PINK_SHULKER_BOX_LOCATION = new ResourceLocation(Main.MODID, "entity/shulkerbox/shulker_pink");
    private static final ResourceLocation GRAY_SHULKER_BOX_LOCATION = new ResourceLocation(Main.MODID, "entity/shulkerbox/shulker_gray");
    private static final ResourceLocation LIGHT_GRAY_SHULKER_BOX_LOCATION = new ResourceLocation(Main.MODID, "entity/shulkerbox/shulker_light_gray");
    private static final ResourceLocation CYAN_SHULKER_BOX_LOCATION = new ResourceLocation(Main.MODID, "entity/shulkerbox/shulker_cyan");
    private static final ResourceLocation PURPLE_SHULKER_BOX_LOCATION = new ResourceLocation(Main.MODID, "entity/shulkerbox/shulker_purple");
    private static final ResourceLocation BLUE_SHULKER_BOX_LOCATION = new ResourceLocation(Main.MODID, "entity/shulkerbox/shulker_blue");
    private static final ResourceLocation BROWN_SHULKER_BOX_LOCATION = new ResourceLocation(Main.MODID, "entity/shulkerbox/shulker_brown");
    private static final ResourceLocation GREEN_SHULKER_BOX_LOCATION = new ResourceLocation(Main.MODID, "entity/shulkerbox/shulker_green");
    private static final ResourceLocation RED_SHULKER_BOX_LOCATION = new ResourceLocation(Main.MODID, "entity/shulkerbox/shulker_red");
    private static final ResourceLocation BLACK_SHULKER_BOX_LOCATION = new ResourceLocation(Main.MODID, "entity/shulkerbox/shulker_black");

    private static final Material CHEST_OAK_MATERIAL = getChestMaterial(CHEST_OAK_LOCATION);
    private static final Material CHEST_OAK_LEFT_MATERIAL = getChestMaterial(CHEST_OAK_LEFT_LOCATION);
    private static final Material CHEST_OAK_RIGHT_MATERIAL = getChestMaterial(CHEST_OAK_RIGHT_LOCATION);

    private static final Material CHEST_SPRUCE_MATERIAL = getChestMaterial(CHEST_SPRUCE_LOCATION);
    private static final Material CHEST_SPRUCE_LEFT_MATERIAL = getChestMaterial(CHEST_SPRUCE_LEFT_LOCATION);
    private static final Material CHEST_SPRUCE_RIGHT_MATERIAL = getChestMaterial(CHEST_SPRUCE_RIGHT_LOCATION);

    private static final Material CHEST_BIRCH_MATERIAL = getChestMaterial(CHEST_BIRCH_LOCATION);
    private static final Material CHEST_BIRCH_LEFT_MATERIAL = getChestMaterial(CHEST_BIRCH_LEFT_LOCATION);
    private static final Material CHEST_BIRCH_RIGHT_MATERIAL = getChestMaterial(CHEST_BIRCH_RIGHT_LOCATION);

    private static final Material CHEST_ACACIA_MATERIAL = getChestMaterial(CHEST_ACACIA_LOCATION);
    private static final Material CHEST_ACACIA_LEFT_MATERIAL = getChestMaterial(CHEST_ACACIA_LEFT_LOCATION);
    private static final Material CHEST_ACACIA_RIGHT_MATERIAL = getChestMaterial(CHEST_ACACIA_RIGHT_LOCATION);

    private static final Material CHEST_JUNGLE_MATERIAL = getChestMaterial(CHEST_JUNGLE_LOCATION);
    private static final Material CHEST_JUNGLE_LEFT_MATERIAL = getChestMaterial(CHEST_JUNGLE_LEFT_LOCATION);
    private static final Material CHEST_JUNGLE_RIGHT_MATERIAL = getChestMaterial(CHEST_JUNGLE_RIGHT_LOCATION);

    private static final Material CHEST_DARK_OAK_MATERIAL = getChestMaterial(CHEST_DARK_OAK_LOCATION);
    private static final Material CHEST_DARK_OAK_LEFT_MATERIAL = getChestMaterial(CHEST_DARK_OAK_LEFT_LOCATION);
    private static final Material CHEST_DARK_OAK_RIGHT_MATERIAL = getChestMaterial(CHEST_DARK_OAK_RIGHT_LOCATION);

    private static final Material CHEST_CRIMSON_MATERIAL = getChestMaterial(CHEST_CRIMSON_LOCATION);
    private static final Material CHEST_CRIMSON_LEFT_MATERIAL = getChestMaterial(CHEST_CRIMSON_LEFT_LOCATION);
    private static final Material CHEST_CRIMSON_RIGHT_MATERIAL = getChestMaterial(CHEST_CRIMSON_RIGHT_LOCATION);

    private static final Material CHEST_WARPED_MATERIAL = getChestMaterial(CHEST_WARPED_LOCATION);
    private static final Material CHEST_WARPED_LEFT_MATERIAL = getChestMaterial(CHEST_WARPED_LEFT_LOCATION);
    private static final Material CHEST_WARPED_RIGHT_MATERIAL = getChestMaterial(CHEST_WARPED_RIGHT_LOCATION);

    public static final Material WHITE_SHULKER_BOX_MATERIAL = new Material(Sheets.SHULKER_SHEET, WHITE_SHULKER_BOX_LOCATION);
    public static final Material ORANGE_SHULKER_BOX_MATERIAL = new Material(Sheets.SHULKER_SHEET, ORANGE_SHULKER_BOX_LOCATION);
    public static final Material MAGENTA_SHULKER_BOX_MATERIAL = new Material(Sheets.SHULKER_SHEET, MAGENTA_SHULKER_BOX_LOCATION);
    public static final Material LIGHT_BLUE_SHULKER_BOX_MATERIAL = new Material(Sheets.SHULKER_SHEET, LIGHT_BLUE_SHULKER_BOX_LOCATION);
    public static final Material YELLOW_SHULKER_BOX_MATERIAL = new Material(Sheets.SHULKER_SHEET, YELLOW_SHULKER_BOX_LOCATION);
    public static final Material LIME_SHULKER_BOX_MATERIAL = new Material(Sheets.SHULKER_SHEET, LIME_SHULKER_BOX_LOCATION);
    public static final Material PINK_SHULKER_BOX_MATERIAL = new Material(Sheets.SHULKER_SHEET, PINK_SHULKER_BOX_LOCATION);
    public static final Material GRAY_SHULKER_BOX_MATERIAL = new Material(Sheets.SHULKER_SHEET, GRAY_SHULKER_BOX_LOCATION);
    public static final Material LIGHT_GRAY_SHULKER_BOX_MATERIAL = new Material(Sheets.SHULKER_SHEET, LIGHT_GRAY_SHULKER_BOX_LOCATION);
    public static final Material CYAN_SHULKER_BOX_MATERIAL = new Material(Sheets.SHULKER_SHEET, CYAN_SHULKER_BOX_LOCATION);
    public static final Material PURPLE_SHULKER_BOX_MATERIAL = new Material(Sheets.SHULKER_SHEET, PURPLE_SHULKER_BOX_LOCATION);
    public static final Material BLUE_SHULKER_BOX_MATERIAL = new Material(Sheets.SHULKER_SHEET, BLUE_SHULKER_BOX_LOCATION);
    public static final Material BROWN_SHULKER_BOX_MATERIAL = new Material(Sheets.SHULKER_SHEET, BROWN_SHULKER_BOX_LOCATION);
    public static final Material GREEN_SHULKER_BOX_MATERIAL = new Material(Sheets.SHULKER_SHEET, GREEN_SHULKER_BOX_LOCATION);
    public static final Material RED_SHULKER_BOX_MATERIAL = new Material(Sheets.SHULKER_SHEET, RED_SHULKER_BOX_LOCATION);
    public static final Material BLACK_SHULKER_BOX_MATERIAL = new Material(Sheets.SHULKER_SHEET, BLACK_SHULKER_BOX_LOCATION);

    public static final ChestMaterial CHEST_OAK = new ChestMaterial(CHEST_OAK_MATERIAL, CHEST_OAK_LEFT_MATERIAL, CHEST_OAK_RIGHT_MATERIAL);
    public static final ChestMaterial CHEST_SPRUCE = new ChestMaterial(CHEST_SPRUCE_MATERIAL, CHEST_SPRUCE_LEFT_MATERIAL, CHEST_SPRUCE_RIGHT_MATERIAL);
    public static final ChestMaterial CHEST_BIRCH = new ChestMaterial(CHEST_BIRCH_MATERIAL, CHEST_BIRCH_LEFT_MATERIAL, CHEST_BIRCH_RIGHT_MATERIAL);
    public static final ChestMaterial CHEST_ACACIA = new ChestMaterial(CHEST_ACACIA_MATERIAL, CHEST_ACACIA_LEFT_MATERIAL, CHEST_ACACIA_RIGHT_MATERIAL);
    public static final ChestMaterial CHEST_JUNGLE = new ChestMaterial(CHEST_JUNGLE_MATERIAL, CHEST_JUNGLE_LEFT_MATERIAL, CHEST_JUNGLE_RIGHT_MATERIAL);
    public static final ChestMaterial CHEST_DARK_OAK = new ChestMaterial(CHEST_DARK_OAK_MATERIAL, CHEST_DARK_OAK_LEFT_MATERIAL, CHEST_DARK_OAK_RIGHT_MATERIAL);
    public static final ChestMaterial CHEST_CRIMSON = new ChestMaterial(CHEST_CRIMSON_MATERIAL, CHEST_CRIMSON_LEFT_MATERIAL, CHEST_CRIMSON_RIGHT_MATERIAL);
    public static final ChestMaterial CHEST_WARPED = new ChestMaterial(CHEST_WARPED_MATERIAL, CHEST_WARPED_LEFT_MATERIAL, CHEST_WARPED_RIGHT_MATERIAL);

    private static Material getChestMaterial(ResourceLocation location) {
        return new Material(Sheets.CHEST_SHEET, location);
    }

    public static Material getChestMaterial(WoodType type, ChestType chestType) {
        if (WoodType.SPRUCE.equals(type)) {
            return getChestTypeMaterial(CHEST_SPRUCE, chestType);
        } else if (WoodType.BIRCH.equals(type)) {
            return getChestTypeMaterial(CHEST_BIRCH, chestType);
        } else if (WoodType.ACACIA.equals(type)) {
            return getChestTypeMaterial(CHEST_ACACIA, chestType);
        } else if (WoodType.JUNGLE.equals(type)) {
            return getChestTypeMaterial(CHEST_JUNGLE, chestType);
        } else if (WoodType.DARK_OAK.equals(type)) {
            return getChestTypeMaterial(CHEST_DARK_OAK, chestType);
        } else if (WoodType.CRIMSON.equals(type)) {
            return getChestTypeMaterial(CHEST_CRIMSON, chestType);
        } else if (WoodType.WARPED.equals(type)) {
            return getChestTypeMaterial(CHEST_WARPED, chestType);
        } else {
            return getChestTypeMaterial(CHEST_OAK, chestType);
        }
    }

    private static Material getChestTypeMaterial(ChestMaterial chestMaterial, ChestType chestType) {
        switch (chestType) {
            case LEFT:
                return chestMaterial.left;
            case RIGHT:
                return chestMaterial.right;
            case SINGLE:
            default:
                return chestMaterial.single;
        }
    }

    public static Material getShulkerBoxMaterial(DyeColor dyeColor) {
        switch (dyeColor) {
            case WHITE:
            default:
                return WHITE_SHULKER_BOX_MATERIAL;
            case ORANGE:
                return ORANGE_SHULKER_BOX_MATERIAL;
            case MAGENTA:
                return MAGENTA_SHULKER_BOX_MATERIAL;
            case LIGHT_BLUE:
                return LIGHT_BLUE_SHULKER_BOX_MATERIAL;
            case YELLOW:
                return YELLOW_SHULKER_BOX_MATERIAL;
            case LIME:
                return LIME_SHULKER_BOX_MATERIAL;
            case PINK:
                return PINK_SHULKER_BOX_MATERIAL;
            case GRAY:
                return GRAY_SHULKER_BOX_MATERIAL;
            case LIGHT_GRAY:
                return LIGHT_GRAY_SHULKER_BOX_MATERIAL;
            case CYAN:
                return CYAN_SHULKER_BOX_MATERIAL;
            case PURPLE:
                return PURPLE_SHULKER_BOX_MATERIAL;
            case BLUE:
                return BLUE_SHULKER_BOX_MATERIAL;
            case BROWN:
                return BROWN_SHULKER_BOX_MATERIAL;
            case GREEN:
                return GREEN_SHULKER_BOX_MATERIAL;
            case RED:
                return RED_SHULKER_BOX_MATERIAL;
            case BLACK:
                return BLACK_SHULKER_BOX_MATERIAL;
        }
    }

    @SubscribeEvent
    public static void onStitch(TextureStitchEvent.Pre event) {
        if (event.getMap().location().equals(Sheets.CHEST_SHEET)) {

            event.addSprite(CHEST_OAK_LOCATION);
            event.addSprite(CHEST_OAK_LEFT_LOCATION);
            event.addSprite(CHEST_OAK_RIGHT_LOCATION);

            event.addSprite(CHEST_SPRUCE_LOCATION);
            event.addSprite(CHEST_SPRUCE_LEFT_LOCATION);
            event.addSprite(CHEST_SPRUCE_RIGHT_LOCATION);

            event.addSprite(CHEST_BIRCH_LOCATION);
            event.addSprite(CHEST_BIRCH_LEFT_LOCATION);
            event.addSprite(CHEST_BIRCH_RIGHT_LOCATION);

            event.addSprite(CHEST_ACACIA_LOCATION);
            event.addSprite(CHEST_ACACIA_LEFT_LOCATION);
            event.addSprite(CHEST_ACACIA_RIGHT_LOCATION);

            event.addSprite(CHEST_JUNGLE_LOCATION);
            event.addSprite(CHEST_JUNGLE_LEFT_LOCATION);
            event.addSprite(CHEST_JUNGLE_RIGHT_LOCATION);

            event.addSprite(CHEST_DARK_OAK_LOCATION);
            event.addSprite(CHEST_DARK_OAK_LEFT_LOCATION);
            event.addSprite(CHEST_DARK_OAK_RIGHT_LOCATION);

            event.addSprite(CHEST_CRIMSON_LOCATION);
            event.addSprite(CHEST_CRIMSON_LEFT_LOCATION);
            event.addSprite(CHEST_CRIMSON_RIGHT_LOCATION);

            event.addSprite(CHEST_WARPED_LOCATION);
            event.addSprite(CHEST_WARPED_LEFT_LOCATION);
            event.addSprite(CHEST_WARPED_RIGHT_LOCATION);

        } else if (event.getMap().location().equals(Sheets.SHULKER_SHEET)) {

            event.addSprite(WHITE_SHULKER_BOX_LOCATION);
            event.addSprite(ORANGE_SHULKER_BOX_LOCATION);
            event.addSprite(MAGENTA_SHULKER_BOX_LOCATION);
            event.addSprite(LIGHT_BLUE_SHULKER_BOX_LOCATION);
            event.addSprite(YELLOW_SHULKER_BOX_LOCATION);
            event.addSprite(LIME_SHULKER_BOX_LOCATION);
            event.addSprite(PINK_SHULKER_BOX_LOCATION);
            event.addSprite(GRAY_SHULKER_BOX_LOCATION);
            event.addSprite(LIGHT_GRAY_SHULKER_BOX_LOCATION);
            event.addSprite(CYAN_SHULKER_BOX_LOCATION);
            event.addSprite(PURPLE_SHULKER_BOX_LOCATION);
            event.addSprite(BLUE_SHULKER_BOX_LOCATION);
            event.addSprite(BROWN_SHULKER_BOX_LOCATION);
            event.addSprite(GREEN_SHULKER_BOX_LOCATION);
            event.addSprite(RED_SHULKER_BOX_LOCATION);
            event.addSprite(BLACK_SHULKER_BOX_LOCATION);

        }
    }

    public static class ChestMaterial {
        private Material single, left, right;

        public ChestMaterial(Material single, Material left, Material right) {
            this.single = single;
            this.left = left;
            this.right = right;
        }
    }
}