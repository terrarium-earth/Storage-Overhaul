package de.maxhenkel.storage.gui;

import de.maxhenkel.corelib.inventory.ContainerBase;
import de.maxhenkel.corelib.inventory.LockedSlot;
import de.maxhenkel.storage.items.AdvancedShulkerBoxItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;

public class AdvancedShulkerboxContainer extends ContainerBase {

    public AdvancedShulkerboxContainer(int id, Inventory playerInventory, Container shulkerboxInventory) {
        super(Containers.SHULKERBOX_CONTAINER, id, playerInventory, shulkerboxInventory);

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new AdvancedShulkerboxSlot(shulkerboxInventory, x + y * 9, 8 + x * 18, 18 + y * 18));
            }
        }

        for (int l = 0; l < 3; l++) {
            for (int j1 = 0; j1 < 9; j1++) {
                addSlot(new Slot(playerInventory, j1 + l * 9 + 9, 8 + j1 * 18, 102 + l * 18 - 18));
            }
        }

        int locked = getLockedSlot(playerInventory.player);
        for (int i = 0; i < 9; i++) {
            int x = 8 + i * 18;
            int y = 142;
            if (i == locked) {
                addSlot(new LockedSlot(playerInventory, i, x, y));
            } else {
                addSlot(new Slot(playerInventory, i, x, y));
            }
        }
    }

    public AdvancedShulkerboxContainer(int id, Inventory playerInventory) {
        this(id, playerInventory, new ShulkerBoxItemInventory(playerInventory.player, getShulkerBox(playerInventory.player)));
    }

    public static int getLockedSlot(Player player) {
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (isOpenableShulkerBox(stack)) {
            return player.getInventory().selected;
        }
        return -1;
    }

    public static ItemStack getShulkerBox(Player player) {
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (isOpenableShulkerBox(stack)) {
            return stack;
        }
        stack = player.getItemInHand(InteractionHand.OFF_HAND);
        if (isOpenableShulkerBox(stack)) {
            return stack;
        }
        return null;
    }

    public static boolean isOpenableShulkerBox(ItemStack stack) {
        if (stack == null || stack.getCount() != 1) {
            return false;
        }

        if (stack.getItem() instanceof AdvancedShulkerBoxItem) {
            return true;
        }

        return false;
    }

}
