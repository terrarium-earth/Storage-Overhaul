package de.maxhenkel.storage.blocks.tileentity;

import de.maxhenkel.corelib.sound.SoundUtils;
import de.maxhenkel.storage.ChestTier;
import de.maxhenkel.storage.blocks.ModBarrelBlock;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import javax.annotation.Nullable;

public class ModBarrelTileEntity extends RandomizableContainerBlockEntity {

    @Nullable
    private NonNullList<ItemStack> barrelContents;
    private int numPlayersUsing;

    @Nullable
    private ChestTier tier;

    public ModBarrelTileEntity(ChestTier tier) {
        super(ModTileEntities.BARREL);
        this.tier = tier;
    }

    public ChestTier getTier() {
        return tier;
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        if (barrelContents == null) {
            barrelContents = NonNullList.withSize(getTier().numSlots(), ItemStack.EMPTY);
        }
        return barrelContents;
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        super.save(compound);

        compound.putInt("Tier", getTier().getTier());

        if (!trySaveLootTable(compound)) {
            ContainerHelper.saveAllItems(compound, getItems());
        }

        return compound;
    }

    @Override
    public void load(BlockState blockState, CompoundTag compound) {
        super.load(blockState, compound);

        tier = ChestTier.byTier(compound.getInt("Tier"));

        barrelContents = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        if (!tryLoadLootTable(compound)) {
            ContainerHelper.loadAllItems(compound, barrelContents);
        }
    }

    @Override
    public int getContainerSize() {
        return getTier().numSlots();
    }

    @Override
    protected void setItems(NonNullList<ItemStack> itemsIn) {
        barrelContents = itemsIn;
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory player) {
        return getTier().getContainer(id, player, this);
    }

    @Override
    public void startOpen(Player player) {
        if (!player.isSpectator()) {
            if (numPlayersUsing < 0) {
                numPlayersUsing = 0;
            }

            numPlayersUsing++;
            BlockState blockstate = getBlockState();
            if (!blockstate.getValue(BarrelBlock.OPEN)) {
                playSound(blockstate, SoundEvents.BARREL_OPEN);
                setOpenProperty(blockstate, true);
            }
            scheduleTick();
        }

    }

    private void scheduleTick() {
        level.getBlockTicks().scheduleTick(getBlockPos(), getBlockState().getBlock(), 5);
    }

    public void barrelTick() {
        int x = worldPosition.getX();
        int y = worldPosition.getY();
        int z = worldPosition.getZ();
        numPlayersUsing = ChestBlockEntity.getOpenCount(level, this, x, y, z);
        if (numPlayersUsing > 0) {
            scheduleTick();
        } else {
            BlockState blockstate = getBlockState();
            if (!(blockstate.getBlock() instanceof ModBarrelBlock)) {
                setRemoved();
                return;
            }

            boolean flag = blockstate.getValue(BarrelBlock.OPEN);
            if (flag) {
                playSound(blockstate, SoundEvents.BARREL_CLOSE);
                setOpenProperty(blockstate, false);
            }
        }

    }

    @Override
    public void stopOpen(Player player) {
        if (!player.isSpectator()) {
            numPlayersUsing--;
        }
    }

    private void setOpenProperty(BlockState blockState, boolean open) {
        level.setBlock(getBlockPos(), blockState.setValue(BarrelBlock.OPEN, open), 3);
    }

    private void playSound(BlockState blockState, SoundEvent soundEvent) {
        Vec3i vec3i = blockState.getValue(BarrelBlock.FACING).getNormal();
        double x = (double) this.worldPosition.getX() + 0.5D + (double) vec3i.getX() / 2D;
        double y = (double) this.worldPosition.getY() + 0.5D + (double) vec3i.getY() / 2D;
        double z = (double) this.worldPosition.getZ() + 0.5D + (double) vec3i.getZ() / 2D;
        level.playSound(null, x, y, z, soundEvent, SoundSource.BLOCKS, 0.5F, SoundUtils.getVariatedPitch(level));
    }

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent(getBlockState().getBlock().getDescriptionId());
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

}
