package de.maxhenkel.storage.blocks.tileentity;

import de.maxhenkel.corelib.sound.SoundUtils;
import de.maxhenkel.storage.blocks.AdvancedShulkerBoxBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.util.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class AdvancedShulkerBoxTileEnitity extends RandomizableContainerBlockEntity implements WorldlyContainer, TickableBlockEntity {

    private static final int[] SLOTS = IntStream.range(0, 27).toArray();
    private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
    private int openCount;
    private ShulkerBoxBlockEntity.AnimationStatus animationStatus = ShulkerBoxBlockEntity.AnimationStatus.CLOSED;
    private float progress;
    private float progressOld;
    private Tag enchantments;

    @Nullable
    private DyeColor color;

    public AdvancedShulkerBoxTileEnitity(DyeColor colorIn) {
        super(ModTileEntities.SHULKER_BOX);
        this.color = colorIn;
    }

    @Override
    public void tick() {
        progressOld = progress;
        switch (animationStatus) {
            case CLOSED:
                progress = 0F;
                break;
            case OPENING:
                progress += 0.25F;
                if (progress >= 1F) {
                    animationStatus = ShulkerBoxBlockEntity.AnimationStatus.OPENED;
                    progress = 1F;
                    updateNeighbors();
                }
                break;
            case CLOSING:
                progress -= 0.25F;
                if (progress <= 0F) {
                    animationStatus = ShulkerBoxBlockEntity.AnimationStatus.CLOSED;
                    progress = 0F;
                    updateNeighbors();
                }
                break;
            case OPENED:
                progress = 1F;
        }
    }

    public boolean canOpen() {
        if (animationStatus == ShulkerBoxBlockEntity.AnimationStatus.CLOSED) {
            Direction direction = getBlockState().getValue(AdvancedShulkerBoxBlock.FACING);
            AABB axisalignedbb = Shapes.block().bounds().expandTowards(0.5F * (float) direction.getStepX(), 0.5F * (float) direction.getStepY(), 0.5F * (float) direction.getStepZ()).contract(direction.getStepX(), direction.getStepY(), direction.getStepZ());
            return level.noCollision(axisalignedbb.move(worldPosition.relative(direction)));
        } else {
            return true;
        }
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == 1) {
            openCount = type;
            if (type == 0) {
                animationStatus = ShulkerBoxBlockEntity.AnimationStatus.CLOSING;
                updateNeighbors();
            }

            if (type == 1) {
                animationStatus = ShulkerBoxBlockEntity.AnimationStatus.OPENING;
                updateNeighbors();
            }

            return true;
        } else {
            return super.triggerEvent(id, type);
        }
    }

    private void updateNeighbors() {
        getBlockState().updateNeighbourShapes(getLevel(), getBlockPos(), 3);
    }

    @Override
    public void startOpen(Player player) {
        if (!player.isSpectator()) {
            if (openCount < 0) {
                openCount = 0;
            }

            openCount++;
            level.blockEvent(worldPosition, getBlockState().getBlock(), 1, openCount);
            if (openCount == 1) {
                level.playSound(null, worldPosition, getOpenSound(), SoundSource.BLOCKS, 0.5F, SoundUtils.getVariatedPitch(level));
            }
        }

    }

    public static SoundEvent getOpenSound() {
        return SoundEvents.SHULKER_OPEN;
    }

    public static SoundEvent getCloseSound() {
        return SoundEvents.SHULKER_CLOSE;
    }

    @Override
    public void stopOpen(Player player) {
        if (!player.isSpectator()) {
            openCount--;
            level.blockEvent(worldPosition, getBlockState().getBlock(), 1, openCount);
            if (openCount <= 0) {
                level.playSound(null, worldPosition, getCloseSound(), SoundSource.BLOCKS, 0.5F, SoundUtils.getVariatedPitch(level));
            }
        }

    }

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent(getBlockState().getBlock().getDescriptionId());
    }

    @Override
    public void load(BlockState blockState, CompoundTag compound) {
        super.load(blockState, compound);
        loadFromNbt(compound);
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        super.save(compound);
        return saveToNbt(compound);
    }

    public void loadFromNbt(CompoundTag compound) {
        items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        if (!tryLoadLootTable(compound) && compound.contains("Items", 9)) {
            ContainerHelper.loadAllItems(compound, items);
        }
        Tag enchantmentsNbt = compound.get("Enchantments");
        if (enchantmentsNbt != null) {
            enchantments = enchantmentsNbt.copy();
        }
    }

    public CompoundTag saveToNbt(CompoundTag compound) {
        if (!trySaveLootTable(compound)) {
            ContainerHelper.saveAllItems(compound, items, false);
        }
        if (enchantments != null) {
            compound.put("Enchantments", enchantments);
        }
        return compound;
    }

    public void readFromItemStackNbt(CompoundTag nbtIn) {
        Tag nbt = nbtIn.get("Enchantments");
        if (nbt != null) {
            this.enchantments = nbt.copy();
        }
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> itemsIn) {
        items = itemsIn;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        return !(Block.byItem(itemStackIn.getItem()) instanceof ShulkerBoxBlock);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return true;
    }

    public float getProgress(float partialTicks) {
        return Mth.lerp(partialTicks, progressOld, progress);
    }

    public DyeColor getColor() {
        if (color == null) {
            color = ((AdvancedShulkerBoxBlock) getBlockState().getBlock()).getColor();
        }
        return color;
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory player) {
        return new ShulkerBoxMenu(id, player, this);
    }

    @Override
    protected IItemHandler createUnSidedHandler() {
        return new SidedInvWrapper(this, Direction.UP);
    }
}