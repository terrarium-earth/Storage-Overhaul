package de.maxhenkel.storage.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

public class AdvancedShulkerboxScreen extends ScreenBase<AdvancedShulkerboxContainer> {

    public static final ResourceLocation DEFAULT_IMAGE = new ResourceLocation("textures/gui/container/shulker_box.png");

    private Inventory playerInventory;

    public AdvancedShulkerboxScreen(AdvancedShulkerboxContainer shulkerboxContainer, Inventory playerInventory, Component name) {
        super(DEFAULT_IMAGE, shulkerboxContainer, playerInventory, name);
        this.playerInventory = playerInventory;
        imageWidth = 176;
        imageHeight = 166;
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        font.draw(matrixStack, getTitle(), 8F, 6F, FONT_COLOR);
        font.draw(matrixStack, playerInventory.getDisplayName(), 8F, (float) (imageHeight - 96 + 3), FONT_COLOR);
    }

}