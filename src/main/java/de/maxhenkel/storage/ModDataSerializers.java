package de.maxhenkel.storage;

import net.minecraft.world.level.block.Block;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.registries.ForgeRegistries;

public class ModDataSerializers {

    public static final EntityDataSerializer<Block> BLOCK = new EntityDataSerializer<Block>() {

        @Override
        public void write(FriendlyByteBuf buf, Block value) {
            buf.writeResourceLocation(value.getRegistryName());
        }

        @Override
        public Block read(FriendlyByteBuf buf) {
            return ForgeRegistries.BLOCKS.getValue(buf.readResourceLocation());
        }

        @Override
        public Block copy(Block value) {
            return value;
        }

    };

}