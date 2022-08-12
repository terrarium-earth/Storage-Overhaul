package de.maxhenkel.storage.gui;

import de.maxhenkel.storage.items.AdvancedShulkerBoxItem;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ShulkerBoxSlot;
import net.minecraft.world.item.ItemStack;

public class AdvancedShulkerboxSlot extends ShulkerBoxSlot {

    public AdvancedShulkerboxSlot(Container inventoryIn, int slotIndexIn, int xPosition, int yPosition) {
        super(inventoryIn, slotIndexIn, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (!super.mayPlace(stack)) {
            return false;
        }

        if (stack.getItem() instanceof AdvancedShulkerBoxItem) {
            return false;
        }

        return true;
    }

}
