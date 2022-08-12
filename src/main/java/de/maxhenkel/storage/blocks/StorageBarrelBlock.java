package de.maxhenkel.storage.blocks;

import de.maxhenkel.corelib.block.DirectionalVoxelShape;
import de.maxhenkel.corelib.block.IItemBlock;
import de.maxhenkel.corelib.item.ItemUtils;
import de.maxhenkel.corelib.sound.SoundUtils;
import de.maxhenkel.storage.Main;
import de.maxhenkel.storage.blocks.tileentity.StorageBarrelTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;

public class StorageBarrelBlock extends BaseEntityBlock implements IItemBlock {

    private static final DirectionalVoxelShape SHAPES = new DirectionalVoxelShape.Builder()
            .direction(Direction.NORTH,
                    Block.box(0D, 0D, 1D, 16D, 16D, 16D),
                    Block.box(0D, 0D, 0D, 1D, 16D, 1D),
                    Block.box(0D, 15D, 0D, 16D, 16D, 1D),
                    Block.box(15D, 0D, 0D, 16D, 16D, 1D),
                    Block.box(0D, 0D, 0D, 16D, 1D, 1D)
            )
            .direction(Direction.SOUTH,
                    Block.box(0D, 0D, 0D, 16D, 16D, 15D),
                    Block.box(0D, 0D, 15D, 1D, 16D, 16D),
                    Block.box(0D, 15D, 15D, 16D, 16D, 16D),
                    Block.box(15D, 0D, 15D, 16D, 16D, 16D),
                    Block.box(0D, 0D, 15D, 16D, 1D, 16D)
            )
            .direction(Direction.EAST,
                    Block.box(0D, 0D, 0D, 15D, 16D, 16D),
                    Block.box(15D, 0D, 0D, 16D, 16D, 1D),
                    Block.box(15D, 15D, 0D, 16D, 16D, 16D),
                    Block.box(15D, 16D, 15D, 16D, 0D, 16D),
                    Block.box(15D, 0D, 0D, 16D, 1D, 16D)
            )
            .direction(Direction.WEST,
                    Block.box(1D, 0D, 0D, 16D, 16D, 16D),
                    Block.box(0D, 0D, 0D, 1D, 16D, 1D),
                    Block.box(0D, 15D, 0D, 1D, 16D, 16D),
                    Block.box(0D, 16D, 15D, 1D, 0D, 16D),
                    Block.box(0D, 0D, 0D, 1D, 1D, 16D)
            )
            .direction(Direction.UP,
                    Block.box(0D, 0D, 0D, 16D, 15D, 16D),
                    Block.box(0D, 15D, 0D, 1D, 16D, 16D),
                    Block.box(0D, 15D, 15D, 16D, 16D, 16D),
                    Block.box(16D, 16D, 16D, 15D, 15D, 0D),
                    Block.box(0D, 16D, 0D, 16D, 15D, 1D)
            )
            .direction(Direction.DOWN,
                    Block.box(0D, 1D, 0D, 16D, 16D, 16D),
                    Block.box(0D, 0D, 0D, 1D, 1D, 16D),
                    Block.box(0D, 0D, 15D, 16D, 1D, 16D),
                    Block.box(16D, 1D, 16D, 15D, 0D, 0D),
                    Block.box(0D, 0D, 0D, 16D, 1D, 1D)
            ).build();

    public static final DirectionProperty PROPERTY_FACING = BlockStateProperties.FACING;

    protected StorageBarrelBlock(String name) {
        super(Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD));
        setRegistryName(new ResourceLocation(Main.MODID, name));
        registerDefaultState(stateDefinition.any().setValue(PROPERTY_FACING, Direction.NORTH));
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public Item toItem() {
        return new BlockItem(this, new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)).setRegistryName(getRegistryName());
    }

    @SubscribeEvent
    public void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        BlockState state = event.getWorld().getBlockState(event.getPos());
        if (state.getBlock() != this) {
            return;
        }

        if (event.getFace().equals(state.getValue(PROPERTY_FACING))) {
            if (event.getPlayer().isCreative()) {
                event.setCanceled(true);
                onFrontClicked(state, event.getWorld(), event.getPos(), event.getPlayer());
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!hit.getDirection().equals(state.getValue(PROPERTY_FACING))) {
            return InteractionResult.PASS;
        }

        if (worldIn.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (!(tileentity instanceof StorageBarrelTileEntity)) {
            return InteractionResult.SUCCESS;
        }

        StorageBarrelTileEntity barrel = (StorageBarrelTileEntity) tileentity;

        if (barrel.onInsert(player)) {
            boolean inserted = false;
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                ItemStack rest = barrel.insertItem(0, stack, false);
                if (!ItemStack.matches(rest, stack)) {
                    player.getInventory().setItem(i, rest);
                    inserted = true;
                }
            }

            if (inserted) {
                playInsertSound(worldIn, player);
            }
            return InteractionResult.SUCCESS;
        }

        ItemStack held = player.getItemInHand(handIn);

        ItemStack remaining = barrel.insertItem(0, held, false);

        if (ItemStack.matches(remaining, held)) {
            return InteractionResult.SUCCESS;
        }

        held.setCount(remaining.getCount());
        playInsertSound(worldIn, player);

        return InteractionResult.SUCCESS;
    }

    public void playInsertSound(Level world, Player player) {
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.5F, SoundUtils.getVariatedPitch(world));
    }

    public void playExtractSound(Level world, Player player) {
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.5F, world.random.nextFloat() * 0.1F + 1.3F);
    }

    @Override
    public void attack(BlockState state, Level worldIn, BlockPos pos, Player player) {
        super.attack(state, worldIn, pos, player);
        if (!player.isCreative()) {
            onFrontClicked(state, worldIn, pos, player);
        }
    }

    public void onFrontClicked(BlockState state, Level worldIn, BlockPos pos, Player player) {
        if (worldIn.isClientSide) {
            return;
        }
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (!(tileentity instanceof StorageBarrelTileEntity)) {
            return;
        }

        StorageBarrelTileEntity barrel = (StorageBarrelTileEntity) tileentity;

        if (barrel.isEmpty()) {
            return;
        }

        ItemStack barrelContent = barrel.getBarrelContent();

        int amount = Math.min(player.isShiftKeyDown() ? barrelContent.getMaxStackSize() : 1, barrelContent.getCount());

        ItemStack heldItem = player.getMainHandItem();

        if (heldItem.isEmpty()) {
            ItemStack newItem = barrelContent.copy();
            newItem.setCount(amount);
            barrel.removeCount(amount);
            player.getInventory().add(player.getInventory().selected, newItem);
            playExtractSound(worldIn, player);
            return;
        } else if (ItemUtils.isStackable(heldItem, barrelContent)) {
            int space = Math.max(heldItem.getMaxStackSize() - heldItem.getCount(), 0);

            if (space > 0) {
                amount = Math.min(space, amount);
                ItemStack newItem = heldItem.copy();
                newItem.setCount(amount);
                barrel.removeCount(amount);
                player.getInventory().add(player.getInventory().selected, newItem);
                playExtractSound(worldIn, player);
                return;
            }
        }

        ItemStack newItem = barrelContent.copy();
        newItem.setCount(amount);
        barrel.removeCount(amount);

        player.getInventory().add(newItem);

        if (!newItem.isEmpty()) {
            popResource(worldIn, pos.relative(state.getValue(PROPERTY_FACING)), newItem);
        }

        playExtractSound(worldIn, player);
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof StorageBarrelTileEntity) {
                ItemStack content = ((StorageBarrelTileEntity) tileentity).getBarrelContent();
                while (!content.isEmpty()) {
                    ItemStack split = content.split(content.getMaxStackSize());
                    Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), split);
                }
                worldIn.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (stack.hasCustomHoverName()) {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof StorageBarrelTileEntity) {
                ((StorageBarrelTileEntity) tileentity).setCustomName(stack.getDisplayName());
            }
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        return calcRedstone((StorageBarrelTileEntity) worldIn.getBlockEntity(pos));
    }

    public int calcRedstone(StorageBarrelTileEntity barrel) {
        if (barrel.isEmpty()) {
            return 0;
        }
        float percentage = (float) barrel.getBarrelContent().getCount() / (float) barrel.getSlotLimit(0);
        return Mth.floor(percentage * 14F) + 1;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(PROPERTY_FACING, rot.rotate(state.getValue(PROPERTY_FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(PROPERTY_FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PROPERTY_FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(PROPERTY_FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new StorageBarrelTileEntity();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(PROPERTY_FACING));
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        if (((BlockHitResult) target).getDirection().equals(state.getValue(PROPERTY_FACING))) {
            StorageBarrelTileEntity barrel = (StorageBarrelTileEntity) level.getBlockEntity(pos);
            ItemStack stack = barrel.getBarrelContent().copy();
            if (!stack.isEmpty()) {
                stack.setCount(1);
                return stack;
            }
        }
        return super.getCloneItemStack(state, target, level, pos, player);
    }
}
