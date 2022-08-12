package de.maxhenkel.storage.blocks.tileentity;

import de.maxhenkel.storage.ChestTier;
import de.maxhenkel.storage.blocks.ModChestBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.TickableBlockEntity;

@OnlyIn(value = Dist.CLIENT, _interface = LidBlockEntity.class)
public class ModChestTileEntity extends RandomizableContainerBlockEntity implements LidBlockEntity, TickableBlockEntity {

    @Nullable
    protected NonNullList<ItemStack> chestContents;
    protected float lidAngle;
    protected float prevLidAngle;
    protected int numPlayersUsing;
    protected LazyOptional<IItemHandlerModifiable> chestHandler;

    @Nullable
    protected WoodType woodType;
    @Nullable
    protected ChestTier tier;

    public ModChestTileEntity(WoodType woodType, ChestTier tier) {
        super(ModTileEntities.CHEST);
        this.woodType = woodType;
        this.tier = tier;
    }

    public WoodType getWoodType() {
        return woodType;
    }

    public ChestTier getTier() {
        return tier;
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory player) {
        return getTier().getContainer(id, player, this);
    }

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent(getBlockState().getBlock().getDescriptionId());
    }

    @Override
    public int getContainerSize() {
        return getTier().numSlots();
    }

    @Override
    public void load(BlockState blockState, CompoundTag compound) {
        super.load(blockState, compound);
        tier = ChestTier.byTier(compound.getInt("Tier"));

        chestContents = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        if (!tryLoadLootTable(compound)) {
            ContainerHelper.loadAllItems(compound, chestContents);
        }
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
    public void tick() {
        prevLidAngle = lidAngle;
        if (numPlayersUsing > 0 && lidAngle == 0F) {
            playSound(SoundEvents.CHEST_OPEN);
        }

        if (numPlayersUsing == 0 && lidAngle > 0F || numPlayersUsing > 0 && lidAngle < 1F) {
            float oldAngle = lidAngle;
            if (numPlayersUsing > 0) {
                lidAngle += 0.1F;
            } else {
                lidAngle -= 0.1F;
            }

            if (lidAngle > 1F) {
                lidAngle = 1F;
            }

            if (lidAngle < 0.5F && oldAngle >= 0.5F) {
                playSound(SoundEvents.CHEST_CLOSE);
            }

            if (lidAngle < 0F) {
                lidAngle = 0F;
            }
        }

    }

    private void playSound(SoundEvent soundIn) {
        ChestType chesttype = getBlockState().getValue(ModChestBlock.TYPE);
        if (chesttype != ChestType.LEFT) {
            double x = (double) worldPosition.getX() + 0.5D;
            double y = (double) worldPosition.getY() + 0.5D;
            double z = (double) worldPosition.getZ() + 0.5D;
            if (chesttype == ChestType.RIGHT) {
                Direction direction = ChestBlock.getConnectedDirection(getBlockState());
                x += (double) direction.getStepX() * 0.5D;
                z += (double) direction.getStepZ() * 0.5D;
            }

            level.playSound(null, x, y, z, soundIn, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
        }
    }

    @Override
    public boolean triggerEvent(int id, int value) {
        if (id == 1) {
            numPlayersUsing = value;
            return true;
        } else {
            return super.triggerEvent(id, value);
        }
    }

    @Override
    public void startOpen(Player player) {
        if (!player.isSpectator()) {
            if (numPlayersUsing < 0) {
                numPlayersUsing = 0;
            }

            numPlayersUsing++;
            onOpenOrClose();
        }

    }

    @Override
    public void stopOpen(Player player) {
        if (!player.isSpectator()) {
            numPlayersUsing--;
            onOpenOrClose();
        }

    }

    protected void onOpenOrClose() {
        Block block = getBlockState().getBlock();
        if (block instanceof ModChestBlock) {
            level.blockEvent(worldPosition, block, 1, numPlayersUsing);
            level.updateNeighborsAt(worldPosition, block);
        }
    }

    protected NonNullList<ItemStack> getItems() {
        if (chestContents == null) {
            chestContents = NonNullList.withSize(getTier().numSlots(), ItemStack.EMPTY);
        }
        return chestContents;
    }

    protected void setItems(NonNullList<ItemStack> items) {
        chestContents = items;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getOpenNess(float partialTicks) {
        return Mth.lerp(partialTicks, prevLidAngle, lidAngle);
    }

    @Override
    public void clearCache() {
        super.clearCache();
        if (chestHandler != null) {
            chestHandler.invalidate();
            chestHandler = null;
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (chestHandler == null) {
                chestHandler = LazyOptional.of(this::createHandler);
            }
            return chestHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    private IItemHandlerModifiable createHandler() {
        BlockState state = this.getBlockState();
        if (!(state.getBlock() instanceof ModChestBlock)) {
            return new InvWrapper(this);
        }
        ChestType type = state.getValue(ModChestBlock.TYPE);
        if (type != ChestType.SINGLE) {
            BlockPos opos = getBlockPos().relative(ModChestBlock.getDirectionToAttached(state));
            BlockState ostate = getLevel().getBlockState(opos);
            if (state.getBlock() == ostate.getBlock()) {
                ChestType otype = ostate.getValue(ModChestBlock.TYPE);
                if (otype != ChestType.SINGLE && type != otype && state.getValue(ModChestBlock.FACING) == ostate.getValue(ModChestBlock.FACING)) {
                    BlockEntity ote = getLevel().getBlockEntity(opos);
                    if (ote instanceof ModChestTileEntity) {
                        Container top = type == ChestType.RIGHT ? this : (Container) ote;
                        Container bottom = type == ChestType.RIGHT ? (Container) ote : this;
                        return new CombinedInvWrapper(new InvWrapper(top), new InvWrapper(bottom));
                    }
                }
            }
        }
        return new InvWrapper(this);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (chestHandler != null)
            chestHandler.invalidate();
    }
}
