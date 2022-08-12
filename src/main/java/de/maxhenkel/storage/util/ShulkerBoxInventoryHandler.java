package de.maxhenkel.storage.util;

import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.items.ItemStackHandler;


public class ShulkerBoxInventoryHandler extends ItemStackHandler {

    protected final ItemStack box;
    protected CompoundTag blockEntityTag;

    public ShulkerBoxInventoryHandler(ItemStack box) {
        super(27);
        this.box = box;
        CompoundTag tag = box.getTag();
        if (tag != null) {
            if (tag.contains("BlockEntityTag")) {
                this.blockEntityTag = tag.getCompound("BlockEntityTag");
                ContainerHelper.loadAllItems(this.blockEntityTag, stacks);
            }
        }
    }

    @Override
    protected void onContentsChanged(int slot) {
        CompoundTag tag = box.getOrCreateTag();
        if (this.blockEntityTag == null) {
            tag.put("BlockEntityTag", this.blockEntityTag = new CompoundTag());
        } else {
            tag.put("BlockEntityTag", this.blockEntityTag);
        }
        ContainerHelper.saveAllItems(this.blockEntityTag, stacks, true);
    }


}
