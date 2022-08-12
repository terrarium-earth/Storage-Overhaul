package de.maxhenkel.storage.blocks;

import de.maxhenkel.corelib.block.IItemBlock;
import de.maxhenkel.storage.Main;
import de.maxhenkel.storage.blocks.tileentity.AdvancedShulkerBoxTileEnitity;
import de.maxhenkel.storage.items.AdvancedShulkerBoxItem;
import de.maxhenkel.storage.items.render.AdvancedShulkerBoxItemRenderer;
import net.minecraft.block.*;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.item.*;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.text.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.Callable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

public class AdvancedShulkerBoxBlock extends BaseEntityBlock implements IItemBlock {

    public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;
    public static final ResourceLocation CONTENTS = new ResourceLocation("contents");

    private DyeColor color;

    public AdvancedShulkerBoxBlock(String name, DyeColor color) {
        super(Block.Properties.of(Material.SHULKER_SHELL, color.getMaterialColor()).strength(0F, 2F).dynamicShape().noOcclusion());
        this.color = color;
        setRegistryName(Main.MODID, name);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Override
    public BlockEntity newBlockEntity(BlockGetter worldIn) {
        return new AdvancedShulkerBoxTileEnitity(color);
    }

    private Callable renderer = () -> new AdvancedShulkerBoxItemRenderer(color);

    @Override
    public Item toItem() {
        return new AdvancedShulkerBoxItem(this, new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_DECORATIONS).setISTER(() -> renderer)).setRegistryName(getRegistryName());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (worldIn.isClientSide) {
            return InteractionResult.SUCCESS;
        } else if (player.isSpectator()) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof AdvancedShulkerBoxTileEnitity) {
                AdvancedShulkerBoxTileEnitity box = (AdvancedShulkerBoxTileEnitity) tileentity;

                if (box.canOpen()) {
                    player.openMenu(box);
                    player.awardStat(Stats.OPEN_SHULKER_BOX);
                }

                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.PASS;
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        BlockEntity tileentity = world.getBlockEntity(pos);
        if (tileentity instanceof AdvancedShulkerBoxTileEnitity) {
            AdvancedShulkerBoxTileEnitity box = (AdvancedShulkerBoxTileEnitity) tileentity;
            if (player.isCreative() && !world.isClientSide && !box.isEmpty()) {
                LootContext.Builder builder = new LootContext.Builder((ServerLevel) world)
                        .withRandom(world.random)
                        .withParameter(LootContextParams.ORIGIN, new Vec3(pos.getX(), pos.getY(), pos.getZ()))
                        .withParameter(LootContextParams.BLOCK_STATE, state)
                        .withParameter(LootContextParams.BLOCK_ENTITY, box)
                        .withParameter(LootContextParams.TOOL, ItemStack.EMPTY);
                List<ItemStack> drops = getDrops(state, builder);
                drops.forEach((itemStack) -> {
                    popResource(world, pos, itemStack);
                });
            } else {
                box.unpackLootTable(player);
            }
        }
        super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        BlockEntity tileentity = builder.getParameter(LootContextParams.BLOCK_ENTITY);
        if (tileentity instanceof AdvancedShulkerBoxTileEnitity) {
            AdvancedShulkerBoxTileEnitity box = (AdvancedShulkerBoxTileEnitity) tileentity;
            builder = builder.withDynamicDrop(CONTENTS, (lootContext, stackConsumer) -> {
                for (int i = 0; i < box.getContainerSize(); i++) {
                    stackConsumer.accept(box.getItem(i));
                }
            });
        }
        return super.getDrops(state, builder);
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof AdvancedShulkerBoxTileEnitity) {
            if (stack.hasCustomHoverName()) {
                ((AdvancedShulkerBoxTileEnitity) tileentity).setCustomName(stack.getDisplayName());
            }
            CompoundTag tag = stack.getTag();
            if (tag != null) {
                ((AdvancedShulkerBoxTileEnitity) tileentity).readFromItemStackNbt(tag);
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof AdvancedShulkerBoxTileEnitity) {
                worldIn.updateNeighbourForOutputSignal(pos, state.getBlock());
            }
            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        CompoundTag compoundnbt = stack.getTagElement("BlockEntityTag");
        if (compoundnbt != null) {
            if (compoundnbt.contains("LootTable", 8)) {
                tooltip.add(new TextComponent("???????"));
            }

            if (compoundnbt.contains("Items", 9)) {
                NonNullList<ItemStack> nonnulllist = NonNullList.withSize(27, ItemStack.EMPTY);
                ContainerHelper.loadAllItems(compoundnbt, nonnulllist);
                int shownCount = 0;
                int itemCount = 0;

                for (ItemStack itemstack : nonnulllist) {
                    if (!itemstack.isEmpty()) {
                        itemCount++;
                        if (shownCount <= 4) {
                            shownCount++;
                            MutableComponent itextcomponent = itemstack.getDisplayName().copy();
                            itextcomponent.append(" x").append(String.valueOf(itemstack.getCount()));
                            tooltip.add(itextcomponent);
                        }
                    }
                }

                if (itemCount - shownCount > 0) {
                    tooltip.add((new TranslatableComponent("container.shulkerBox.more", itemCount - shownCount)).withStyle(ChatFormatting.ITALIC));
                }
            }
        }

    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromContainer((Container) worldIn.getBlockEntity(pos));
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter worldIn, BlockPos pos, BlockState state) {
        ItemStack itemstack = super.getCloneItemStack(worldIn, pos, state);
        AdvancedShulkerBoxTileEnitity tileEntity = (AdvancedShulkerBoxTileEnitity) worldIn.getBlockEntity(pos);
        CompoundTag compoundnbt = tileEntity.saveToNbt(new CompoundTag());
        if (!compoundnbt.isEmpty()) {
            itemstack.addTagElement("BlockEntityTag", compoundnbt);
        }

        return itemstack;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Nullable
    public DyeColor getColor() {
        return color;
    }
}
