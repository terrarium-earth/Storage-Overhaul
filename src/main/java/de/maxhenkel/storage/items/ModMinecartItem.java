package de.maxhenkel.storage.items;

import de.maxhenkel.storage.entity.ModChestMinecartEntity;
import de.maxhenkel.storage.entity.ModEntities;
import de.maxhenkel.storage.items.render.ChestMinecartItemRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.IItemRenderProperties;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModMinecartItem extends Item {

    private final DispenseItemBehavior MINECART_DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior() {
        private final DefaultDispenseItemBehavior behaviourDefaultDispenseItem = new DefaultDispenseItemBehavior();

        @Override
        public ItemStack execute(BlockSource source, ItemStack stack) {
            Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
            Level world = source.getLevel();
            double x = source.x() + (double) direction.getStepX() * 1.125D;
            double y = Math.floor(source.y()) + (double) direction.getStepY();
            double z = source.z() + (double) direction.getStepZ() * 1.125D;
            BlockPos blockpos = source.getPos().relative(direction);
            BlockState blockstate = world.getBlockState(blockpos);
            RailShape railshape = blockstate.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock) blockstate.getBlock()).getRailDirection(blockstate, world, blockpos, null) : RailShape.NORTH_SOUTH;
            double varY;
            if (blockstate.is(BlockTags.RAILS)) {
                if (railshape.isAscending()) {
                    varY = 0.6D;
                } else {
                    varY = 0.1D;
                }
            } else {
                if (!blockstate.isAir() || !world.getBlockState(blockpos.below()).is(BlockTags.RAILS)) {
                    return this.behaviourDefaultDispenseItem.dispense(source, stack);
                }

                BlockState blockstate1 = world.getBlockState(blockpos.below());
                RailShape railshape1 = blockstate1.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock) blockstate1.getBlock()).getRailDirection(blockstate1, world, blockpos.below(), null) : RailShape.NORTH_SOUTH;
                if (direction != Direction.DOWN && railshape1.isAscending()) {
                    varY = -0.4D;
                } else {
                    varY = -0.9D;
                }
            }
            ModChestMinecartEntity cart = create(world);
            cart.setPos(x, y + varY, z);
            if (stack.hasCustomHoverName()) {
                cart.setCustomName(stack.getDisplayName());
            }

            world.addFreshEntity(cart);
            stack.shrink(1);
            return stack;
        }

        @Override
        protected void playSound(BlockSource source) {
            source.getLevel().levelEvent(1000, source.getPos(), 0);
        }
    };

    private Supplier<Block> block;

    public ModMinecartItem(Supplier<Block> block) {
        super(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_TRANSPORTATION));
        this.block = block;
        DispenserBlock.registerBehavior(this, MINECART_DISPENSER_BEHAVIOR);
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return new ChestMinecartItemRenderer(block);
            }
        });
    }

    public ModChestMinecartEntity create(Level world) {
        ModChestMinecartEntity cart = ModEntities.CHEST_MINECART.create(world);
        cart.setBlock(block.get());
        return cart;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState blockstate = world.getBlockState(blockpos);
        if (!blockstate.is(BlockTags.RAILS)) {
            return InteractionResult.FAIL;
        } else {
            ItemStack itemstack = context.getItemInHand();
            if (!world.isClientSide) {
                RailShape railshape = blockstate.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock) blockstate.getBlock()).getRailDirection(blockstate, world, blockpos, null) : RailShape.NORTH_SOUTH;
                double height = 0.0D;
                if (railshape.isAscending()) {
                    height = 0.5D;
                }

                ModChestMinecartEntity cart = create(world);
                cart.setPos((double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 0.0625D + height, (double) blockpos.getZ() + 0.5D);
                if (itemstack.hasCustomHoverName()) {
                    cart.setCustomName(itemstack.getDisplayName());
                }

                world.addFreshEntity(cart);
            }

            itemstack.shrink(1);
            return InteractionResult.SUCCESS;
        }
    }

}
