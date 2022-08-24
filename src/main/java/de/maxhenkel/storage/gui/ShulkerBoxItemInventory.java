package de.maxhenkel.storage.gui;

import de.maxhenkel.corelib.inventory.ShulkerBoxInventory;
import de.maxhenkel.storage.blocks.tileentity.AdvancedShulkerBoxTileEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundEvent;

import javax.annotation.Nullable;

public class ShulkerBoxItemInventory extends ShulkerBoxInventory {

    public ShulkerBoxItemInventory(Player player, ItemStack shulkerBox) {
        super(player, shulkerBox);
    }

    @Override
    protected SoundEvent getOpenSound() {
        return AdvancedShulkerBoxTileEntity.getOpenSound();
    }

    @Override
    protected SoundEvent getCloseSound() {
        return AdvancedShulkerBoxTileEntity.getCloseSound();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
        return new ShulkerBoxMenu(i, playerInventory);
    }

}
