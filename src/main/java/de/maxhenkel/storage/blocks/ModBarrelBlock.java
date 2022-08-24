package de.maxhenkel.storage.blocks;

import de.maxhenkel.corelib.block.IItemBlock;
import de.maxhenkel.storage.ChestTier;
import de.maxhenkel.storage.Main;
import de.maxhenkel.storage.blocks.tileentity.ModBarrelTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class ModBarrelBlock extends BaseEntityBlock implements IItemBlock {

    public static final DirectionProperty PROPERTY_FACING = BlockStateProperties.FACING;
    public static final BooleanProperty PROPERTY_OPEN = BlockStateProperties.OPEN;

    private ChestTier tier;

    protected ModBarrelBlock(String name, ChestTier tier) {
        super(Block.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD));
        this.tier = tier;

        setRegistryName(new ResourceLocation(Main.MODID, name));
        registerDefaultState(stateDefinition.any().setValue(PROPERTY_FACING, Direction.NORTH).setValue(PROPERTY_OPEN, false));
    }

    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof ModBarrelTileEntity) {
            ((ModBarrelTileEntity) tileentity).barrelTick();
        }
    }

    @Override
    public Item toItem() {
        return new BlockItem(this, new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)).setRegistryName(getRegistryName());
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (worldIn.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof ModBarrelTileEntity) {
                player.openMenu((ModBarrelTileEntity) tileentity);
                player.awardStat(Stats.OPEN_BARREL);
            }

            return InteractionResult.SUCCESS;
        }
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof Container) {
                Containers.dropContents(worldIn, pos, (Container) tileentity);
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
            if (tileentity instanceof ModBarrelTileEntity) {
                ((ModBarrelTileEntity) tileentity).setCustomName(stack.getDisplayName());
            }
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(worldIn.getBlockEntity(pos));
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
        builder.add(PROPERTY_FACING, PROPERTY_OPEN);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(PROPERTY_FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ModBarrelTileEntity(tier, pos, state);
    }
}
