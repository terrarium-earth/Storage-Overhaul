package de.maxhenkel.storage.gui;

import de.maxhenkel.corelib.inventory.ContainerBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

public class HugeChestContainer extends ContainerBase {

    private static final int NUM_COLS = 18;
    private static final int NUM_PLAYER_INV_COLS = 9;
    private final Container inventory;
    private final int numRows;

    public HugeChestContainer(MenuType type, int id, Inventory playerInventory, Container inventory, int numRows) {
        super(type, id, playerInventory, inventory);
        this.inventory = inventory;
        this.numRows = numRows;

        inventory.startOpen(playerInventory.player);
        int i = (numRows - 4) * 18;

        for (int j = 0; j < numRows; j++) {
            for (int k = 0; k < NUM_COLS; k++) {
                this.addSlot(new Slot(inventory, k + j * NUM_COLS, 8 + k * 18, 18 + j * 18));
            }
        }

        for (int l = 0; l < 3; l++) {
            for (int j1 = 0; j1 < NUM_PLAYER_INV_COLS; j1++) {
                this.addSlot(new Slot(playerInventory, j1 + l * NUM_PLAYER_INV_COLS + 9, 89 + j1 * 18, 104 + l * 18 + i));
            }
        }

        for (int i1 = 0; i1 < NUM_PLAYER_INV_COLS; i1++) {
            this.addSlot(new Slot(playerInventory, i1, 89 + i1 * 18, 162 + i));
        }
    }

    public HugeChestContainer(MenuType type, int id, Inventory playerInventory, int numRows) {
        this(type, id, playerInventory, new SimpleContainer(numRows * NUM_COLS), numRows);
    }

    public int getNumRows() {
        return numRows;
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return inventory.stillValid(playerIn);
    }

}
