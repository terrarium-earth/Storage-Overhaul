package de.maxhenkel.storage.items;

import de.maxhenkel.storage.gui.AdvancedShulkerboxContainer;
import de.maxhenkel.storage.gui.ShulkerBoxItemInventory;
import de.maxhenkel.storage.util.ShulkerBoxInventoryHandler;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.ShulkerBoxDispenseBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AdvancedShulkerBoxItem extends BlockItem {

    public AdvancedShulkerBoxItem(Block blockIn, Properties builder) {
        super(blockIn, builder);
        DispenserBlock.registerBehavior(this, new ShulkerBoxDispenseBehavior());
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        if (context.getPlayer() != null && !context.getPlayer().isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        return super.place(context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player player, InteractionHand handIn) {
        ItemStack stack = player.getItemInHand(handIn);
        if (player instanceof ServerPlayer) {
            NetworkHooks.openGui((ServerPlayer) player, new MenuProvider() {
                @Override
                public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player playerEntity) {
                    return new AdvancedShulkerboxContainer(id, playerInventory, new ShulkerBoxItemInventory(player, stack));
                }

                @Override
                public Component getDisplayName() {
                    return stack.getDisplayName();
                }
            });
        }
        return InteractionResultHolder.success(stack);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilityProvider() {
            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                    return LazyOptional.of(() -> new ShulkerBoxInventoryHandler(stack)).cast();
                }
                return LazyOptional.empty();
            }
        };
    }
}
