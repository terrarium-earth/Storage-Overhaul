package de.maxhenkel.storage.blocks.tileentity;

import de.maxhenkel.corelib.entity.EntityUtils;
import de.maxhenkel.corelib.item.ItemUtils;
import de.maxhenkel.storage.Main;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.Nameable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StorageBarrelTileEntity extends BlockEntity implements IItemHandler, Nameable {

    private ItemStack barrelContent = ItemStack.EMPTY;
    private Component customName;

    private Map<UUID, Long> clicks;

    public StorageBarrelTileEntity() {
        super(ModTileEntities.STORAGE_BARREL);
        clicks = new HashMap<>();
    }

    public boolean onInsert(Player player) {
        boolean flag = false;
        if (level.getGameTime() - clicks.getOrDefault(player.getUUID(), 0L) <= 4) {
            flag = true;
        }
        clicks.put(player.getUUID(), level.getGameTime());
        return flag;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level instanceof ServerLevel) {
            EntityUtils.forEachPlayerAround((ServerLevel) level, getBlockPos(), 128D, this::syncContents);
        }
    }

    public void syncContents(ServerPlayer player) {
        player.connection.send(getUpdatePacket());
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        super.save(compound);

        compound.put("Item", ItemUtils.writeOverstackedItem(new CompoundTag(), barrelContent));

        if (this.customName != null) {
            compound.putString("CustomName", Component.Serializer.toJson(customName));
        }

        return compound;
    }

    @Override
    public void load(BlockState blockState, CompoundTag compound) {
        super.load(blockState, compound);
        barrelContent = ItemUtils.readOverstackedItem(compound.getCompound("Item"));


        if (compound.contains("CustomName")) {
            customName = Component.Serializer.fromJson(compound.getString("CustomName"));
        }
    }

    public ItemStack getBarrelContent() {
        return barrelContent;
    }

    public void setBarrelContent(ItemStack barrelContent) {
        this.barrelContent = barrelContent;
        setChanged();
    }

    public void addCount(int amount) {
        if (barrelContent.isEmpty()) {
            return;
        }
        barrelContent.grow(amount);
        setChanged();
    }

    public void removeCount(int amount) {
        if (barrelContent.isEmpty()) {
            return;
        }
        barrelContent.shrink(amount);
        setChanged();
    }

    public boolean isEmpty() {
        return barrelContent.isEmpty();
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return barrelContent;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        ItemStack ret = stack.copy();
        int freeSpace = Main.SERVER_CONFIG.storageBarrelSize.get() - getBarrelContent().getCount();
        int amount = Math.min(stack.getCount(), freeSpace);
        if (ItemUtils.isStackable(barrelContent, stack)) {
            if (!simulate) {
                addCount(amount);
            }
            ret.shrink(amount);
        } else if (isEmpty() && !stack.isEmpty()) {
            if (!simulate) {
                ItemStack insert = stack.copy();
                insert.setCount(amount);
                setBarrelContent(insert);
            }
            ret.shrink(amount);
        }
        if (ret.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return ret;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack content = getBarrelContent().copy();
        int count = Math.min(content.getCount(), amount);
        if (!simulate) {
            removeCount(count);
        }
        content.setCount(count);
        if (content.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return content;
    }

    @Override
    public int getSlotLimit(int slot) {
        return Main.SERVER_CONFIG.storageBarrelSize.get();
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return true;
    }

    public void setCustomName(Component customName) {
        this.customName = customName;
    }

    @Override
    public Component getName() {
        return customName != null ? customName : new TranslatableComponent(getBlockState().getBlock().getDescriptionId());
    }

    @Override
    public Component getDisplayName() {
        return getName();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(worldPosition, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        load(getBlockState(), pkt.getTag());
    }

    @Override
    public CompoundTag getUpdateTag() {
        return save(new CompoundTag());
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return LazyOptional.of(() -> this).cast();
        }
        return super.getCapability(cap, side);
    }
}
