package de.maxhenkel.storage.entity;

import de.maxhenkel.storage.ChestTier;
import de.maxhenkel.storage.ModDataSerializers;
import de.maxhenkel.storage.blocks.ModBlocks;
import de.maxhenkel.storage.blocks.ModChestBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class ModChestMinecartEntity extends AbstractMinecart implements Container {

    private static final EntityDataAccessor<Block> BLOCK = SynchedEntityData.defineId(ModChestMinecartEntity.class, ModDataSerializers.BLOCK);

    protected NonNullList<ItemStack> inventoryContents;
    private boolean dropContentsWhenDead = true;
    private BlockState cachedBlock;

    public ModChestMinecartEntity(Level world) {
        super(ModEntities.CHEST_MINECART, world);

    }

    public NonNullList<ItemStack> getInventoryContents() {
        if (inventoryContents == null) {
            inventoryContents = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        }
        return inventoryContents;
    }

    public BlockState getBlock() {
        if (cachedBlock == null) {
            cachedBlock = entityData.get(BLOCK).defaultBlockState();
        }

        return cachedBlock;
    }

    public void setBlock(Block block) {
        entityData.set(BLOCK, block);
    }

    public ChestTier getChestTier() {
        Block block = getBlock().getBlock();
        if (block instanceof ModChestBlock) {
            return ((ModChestBlock) block).getTier();
        }
        return ChestTier.BASE_TIER;
    }

    public ItemStack getItem() {
        return new ItemStack(getBlock().getBlock());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(BLOCK, ModBlocks.OAK_CHEST);
    }

    @Override
    public BlockState getDefaultDisplayBlockState() {
        return getBlock();
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void startOpen(Player player) {
        level.playSound(null, getX(), getY(), getZ(), SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public void stopOpen(Player player) {
        level.playSound(null, getX(), getY(), getZ(), SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public int getContainerSize() {
        return getChestTier().numSlots();
    }

    @Override
    public AbstractMinecart.Type getMinecartType() {
        return AbstractMinecart.Type.CHEST;
    }

    @Override
    public int getDefaultDisplayOffset() {
        return 8;
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        return getItem().copy();
    }


    @Override
    protected Component getTypeName() {
        return new TranslatableComponent("entity.storage_overhaul.chest_minecart_generic", getItem().getDisplayName());
    }

    @Override
    public void destroy(DamageSource source) {
        super.destroy(source);
        if (level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            Containers.dropContents(level, this, this);
            spawnAtLocation(getItem());
        }
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : getInventoryContents()) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return getInventoryContents().get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        return ContainerHelper.removeItem(getInventoryContents(), index, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack itemstack = getInventoryContents().get(index);
        if (itemstack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            getInventoryContents().set(index, ItemStack.EMPTY);
            return itemstack;
        }
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        getInventoryContents().set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }

    }

    @Override
    public boolean canPlaceItem(int inventorySlot, ItemStack itemStackIn) {
        //setItem(inventorySlot, itemStackIn);
        return inventorySlot >= 0 && inventorySlot < this.getContainerSize();
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(Player player) {
        if (isRemoved()) {
            return false;
        } else {
            return !(player.distanceToSqr(this) > 64.0D);
        }
    }



    @Nullable
    @Override
    public Entity changeDimension(ServerLevel world) {
        this.dropContentsWhenDead = false;
        return super.changeDimension(world);
    }

    @Override
    public void remove(RemovalReason removalReason) {
        if (!level.isClientSide && dropContentsWhenDead) {
            Containers.dropContents(level, this, this);
        }
        super.remove(removalReason);
        if (!removalReason.shouldSave()) itemHandler.invalidate();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        ContainerHelper.saveAllItems(compound, getInventoryContents());
        compound.putString("Block", getBlock().getBlock().getRegistryName().toString());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setBlock(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(compound.getString("Block"))));
        inventoryContents = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compound, inventoryContents);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (super.interact(player, hand).consumesAction()) return InteractionResult.SUCCESS;
        player.openMenu(new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return new TranslatableComponent(getBlock().getBlock().getDescriptionId());
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
                return getChestTier().getContainer(id, playerInventory, ModChestMinecartEntity.this);
            }
        });
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void applyNaturalSlowdown() {
        float motion = 0.98F;
        int i = 15 - AbstractContainerMenu.getRedstoneSignalFromContainer(this);
        motion += (float) i * 0.001F;

        setDeltaMovement(getDeltaMovement().multiply(motion, 0D, motion));
    }

    @Override
    public void clearContent() {
        getInventoryContents().clear();
    }

    private LazyOptional<?> itemHandler = LazyOptional.of(() -> new InvWrapper(this));

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (this.isAlive() && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemHandler.cast();
        }
        return super.getCapability(capability, facing);
    }

    static {
        MinecraftForge.EVENT_BUS.register(new Object() {
            @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
            public void onTravelToDimension(EntityTravelToDimensionEvent event) {
                if (event.getEntity() instanceof ModChestMinecartEntity) {
                    if (event.isCanceled()) {
                        ((ModChestMinecartEntity) event.getEntity()).dropContentsWhenDead = true;
                    }
                }
            }
        });
    }

}
