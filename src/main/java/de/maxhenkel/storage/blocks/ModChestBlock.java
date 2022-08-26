package de.maxhenkel.storage.blocks;

import de.maxhenkel.corelib.block.IItemBlock;
import de.maxhenkel.storage.ChestTier;
import de.maxhenkel.storage.Main;
import de.maxhenkel.storage.blocks.tileentity.ModChestTileEntity;
import de.maxhenkel.storage.blocks.tileentity.ModTileEntities;
import de.maxhenkel.storage.items.render.ChestItemRenderer;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.client.IBlockRenderProperties;

public class ModChestBlock extends BaseEntityBlock implements SimpleWaterloggedBlock, IItemBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<ChestType> TYPE = BlockStateProperties.CHEST_TYPE;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape SHAPE_NORTH = Block.box(1D, 0D, 0D, 15D, 14D, 15D);
    protected static final VoxelShape SHAPE_SOUTH = Block.box(1D, 0D, 1D, 15D, 14D, 16D);
    protected static final VoxelShape SHAPE_WEST = Block.box(0D, 0D, 1D, 15D, 14D, 15D);
    protected static final VoxelShape SHAPE_EAST = Block.box(1D, 0D, 1D, 16D, 14D, 15D);
    protected static final VoxelShape SHAPE_SINGLE = Block.box(1D, 0D, 1D, 15D, 14D, 15D);

    private static final DoubleBlockCombiner.Combiner<ModChestTileEntity, Optional<MenuProvider>> CALLBACK = new DoubleBlockCombiner.Combiner<ModChestTileEntity, Optional<MenuProvider>>() {
        @Override
        public Optional<MenuProvider> acceptDouble(final ModChestTileEntity iinventory1, ModChestTileEntity iinventory2) {
            final Container iinventory = new CompoundContainer(iinventory1, iinventory2);
            return Optional.of(new MenuProvider() {
                @Nullable
                public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
                    if (iinventory1.canOpen(player) && iinventory2.canOpen(player)) {
                        iinventory1.unpackLootTable(playerInventory.player);
                        iinventory2.unpackLootTable(playerInventory.player);
                        return iinventory1.getTier().getContainer(id, playerInventory, iinventory);
                    } else {
                        return null;
                    }
                }

                public Component getDisplayName() {
                    if (iinventory1.hasCustomName()) {
                        return iinventory1.getDisplayName();
                    } else {
                        return iinventory2.hasCustomName() ? iinventory2.getDisplayName() : new TranslatableComponent("container.storage_overhaul.generic_large", iinventory1.getDisplayName());
                    }
                }
            });
        }

        @Override
        public Optional<MenuProvider> acceptSingle(ModChestTileEntity tileEntity) {
            return Optional.of(tileEntity);
        }

        @Override
        public Optional<MenuProvider> acceptNone() {
            return Optional.empty();
        }
    };

    private static final DoubleBlockCombiner.Combiner<ModChestTileEntity, Optional<Container>> MERGER = new DoubleBlockCombiner.Combiner<ModChestTileEntity, Optional<Container>>() {
        @Override
        public Optional<Container> acceptDouble(ModChestTileEntity chest1, ModChestTileEntity chest2) {
            return Optional.of(new CompoundContainer(chest1, chest2));
        }

        @Override
        public Optional<Container> acceptSingle(ModChestTileEntity chest) {
            return Optional.of(chest);
        }

        @Override
        public Optional<Container> acceptNone() {
            return Optional.empty();
        }
    };

    private WoodType woodType;
    private ChestTier tier;

    protected ModChestBlock(String name, WoodType woodType, ChestTier tier) {
        super(Block.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD));
        this.woodType = woodType;
        this.tier = tier;
        setRegistryName(new ResourceLocation(Main.MODID, name));
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TYPE, ChestType.SINGLE).setValue(WATERLOGGED, false));
    }

    public WoodType getWoodType() {
        return woodType;
    }

    public ChestTier getTier() {
        return tier;
    }

    @Override
    public Item toItem() {
        return new BlockItem(this, new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)).setRegistryName(getRegistryName());
    }

    @Override
    public void initializeClient(Consumer<IBlockRenderProperties> consumer) {
        consumer.accept(new IBlockRenderProperties() {
        });
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ModChestTileEntity(woodType, tier, pos, state);
    }

    @Override
    public MenuProvider getMenuProvider(BlockState state, Level worldIn, BlockPos pos) {
        return getMergerCallback(state, worldIn, pos, false).apply(CALLBACK).orElse(null);
    }

    public static DoubleBlockCombiner.BlockType getType(BlockState blockState) {
        ChestType chesttype = blockState.getValue(TYPE);
        if (chesttype == ChestType.SINGLE) {
            return DoubleBlockCombiner.BlockType.SINGLE;
        } else {
            return chesttype == ChestType.RIGHT ? DoubleBlockCombiner.BlockType.FIRST : DoubleBlockCombiner.BlockType.SECOND;
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (facingState.getBlock() == this && facing.getAxis().isHorizontal()) {
            ChestType chesttype = facingState.getValue(TYPE);
            if (stateIn.getValue(TYPE) == ChestType.SINGLE && chesttype != ChestType.SINGLE && stateIn.getValue(FACING) == facingState.getValue(FACING) && getDirectionToAttached(facingState) == facing.getOpposite()) {
                return stateIn.setValue(TYPE, chesttype.getOpposite());
            }
        } else if (getDirectionToAttached(stateIn) == facing) {
            return stateIn.setValue(TYPE, ChestType.SINGLE);
        }

        return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        if (state.getValue(TYPE) == ChestType.SINGLE) {
            return SHAPE_SINGLE;
        } else {
            switch (getDirectionToAttached(state)) {
                case NORTH:
                default:
                    return SHAPE_NORTH;
                case SOUTH:
                    return SHAPE_SOUTH;
                case WEST:
                    return SHAPE_WEST;
                case EAST:
                    return SHAPE_EAST;
            }
        }
    }

    public static Direction getDirectionToAttached(BlockState state) {
        Direction direction = state.getValue(FACING);
        return state.getValue(TYPE) == ChestType.LEFT ? direction.getClockWise() : direction.getCounterClockWise();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        ChestType chesttype = ChestType.SINGLE;
        Direction direction = context.getHorizontalDirection().getOpposite();
        FluidState ifluidstate = context.getLevel().getFluidState(context.getClickedPos());
        boolean flag = context.isSecondaryUseActive();
        Direction direction1 = context.getClickedFace();
        if (direction1.getAxis().isHorizontal() && flag) {
            Direction direction2 = getDirectionToAttach(context, direction1.getOpposite());
            if (direction2 != null && direction2.getAxis() != direction1.getAxis()) {
                direction = direction2;
                chesttype = direction2.getCounterClockWise() == direction1.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
            }
        }

        if (chesttype == ChestType.SINGLE && !flag) {
            if (direction == getDirectionToAttach(context, direction.getClockWise())) {
                chesttype = ChestType.LEFT;
            } else if (direction == getDirectionToAttach(context, direction.getCounterClockWise())) {
                chesttype = ChestType.RIGHT;
            }
        }

        return defaultBlockState().setValue(FACING, direction).setValue(TYPE, chesttype).setValue(WATERLOGGED, ifluidstate.getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }


    @Nullable
    private Direction getDirectionToAttach(BlockPlaceContext context, Direction direction) {
        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos().relative(direction));
        return blockstate.getBlock() == this && blockstate.getValue(TYPE) == ChestType.SINGLE ? blockstate.getValue(FACING) : null;
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (stack.hasCustomHoverName()) {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof ModChestTileEntity) {
                ((ModChestTileEntity) tileentity).setCustomName(stack.getDisplayName());
            }
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
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (worldIn.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        MenuProvider inamedcontainerprovider = getMenuProvider(state, worldIn, pos);
        if (inamedcontainerprovider != null) {
            player.openMenu(inamedcontainerprovider);
            player.awardStat(getOpenStat());
        }

        return InteractionResult.SUCCESS;
    }

    protected Stat<ResourceLocation> getOpenStat() {
        return Stats.CUSTOM.get(Stats.OPEN_CHEST);
    }

    @Nullable
    public static Container getInventory(ModChestBlock chestBlock, BlockState blockState, Level world, BlockPos blockPos, boolean p_226916_4_) {
        return chestBlock.getMergerCallback(blockState, world, blockPos, p_226916_4_).apply(MERGER).orElse(null);
    }

    public DoubleBlockCombiner.NeighborCombineResult<? extends ModChestTileEntity> getMergerCallback(BlockState blockState, Level world, BlockPos blockPos, boolean b) {
        BiPredicate<LevelAccessor, BlockPos> bipredicate;
        if (b) {
            bipredicate = (world1, pos) -> false;
        } else {
            bipredicate = ModChestBlock::isBlocked;
        }

        return DoubleBlockCombiner.combineWithNeigbour(ModTileEntities.CHEST, ModChestBlock::getType, ModChestBlock::getDirectionToAttached, FACING, blockState, world, blockPos, bipredicate);
    }

    @OnlyIn(Dist.CLIENT)
    public static DoubleBlockCombiner.Combiner<ModChestTileEntity, Float2FloatFunction> lidAngleCallback(final LidBlockEntity lid) {
        return new DoubleBlockCombiner.Combiner<ModChestTileEntity, Float2FloatFunction>() {
            @Override
            public Float2FloatFunction acceptDouble(ModChestTileEntity chest1, ModChestTileEntity chest2) {
                return (partialTicks) -> Math.max(chest1.getOpenNess(partialTicks), chest2.getOpenNess(partialTicks));
            }

            @Override
            public Float2FloatFunction acceptSingle(ModChestTileEntity chest) {
                return chest::getOpenNess;
            }

            @Override
            public Float2FloatFunction acceptNone() {
                return lid::getOpenNess;
            }
        };
    }

    public static boolean isBlocked(LevelAccessor world, BlockPos blockPos) {
        return isBelowSolidBlock(world, blockPos) || isCatSittingOn(world, blockPos);
    }

    private static boolean isBelowSolidBlock(BlockGetter reader, BlockPos pos) {
        BlockPos blockpos = pos.above();
        return reader.getBlockState(blockpos).isRedstoneConductor(reader, blockpos);
    }

    private static boolean isCatSittingOn(LevelAccessor world, BlockPos pos) {
        List<Cat> list = world.getEntitiesOfClass(Cat.class, new AABB(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1));
        if (!list.isEmpty()) {
            for (Cat catentity : list) {
                if (catentity.isOrderedToSit()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromContainer(getInventory(this, blockState, worldIn, pos, false));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE, WATERLOGGED);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
        return false;
    }
}
