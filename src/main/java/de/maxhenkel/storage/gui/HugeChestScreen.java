package de.maxhenkel.storage.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.maxhenkel.storage.Main;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

public class HugeChestScreen extends AbstractContainerScreen<HugeChestContainer> {

    protected static final int FONT_COLOR = 4210752;
    public static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/container/huge_chest.png");

    private final Inventory playerInventory;
    private final int inventoryRows;

    public HugeChestScreen(HugeChestContainer container, Inventory playerInventory, Component name) {
        super(container, playerInventory, name);
        this.playerInventory = playerInventory;
        imageWidth = 338;

        inventoryRows = container.getNumRows();
        imageHeight = 114 + inventoryRows * 18;
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        font.draw(matrixStack, getTitle(), 8F, 6F, FONT_COLOR);
        font.draw(matrixStack, playerInventory.getDisplayName(), 89F, (float) (imageHeight - 96 + 2), FONT_COLOR);
    }

    @Override
    public void render(PoseStack matrixStack, int x, int y, float f) {
        super.render(matrixStack, x, y, f);
        renderTooltip(matrixStack, x, y);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        renderBackground(matrixStack);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        minecraft.getTextureManager().bind(TEXTURE);

        int invStart = inventoryRows * 18 + 17;
        blit(matrixStack, leftPos, topPos, 0, 0, imageWidth, invStart, 512, 512);
        blit(matrixStack, leftPos, topPos + invStart, 0, 179, imageWidth, 96, 512, 512);
    }

}