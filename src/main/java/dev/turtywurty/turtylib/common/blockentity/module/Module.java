package dev.turtywurty.turtylib.common.blockentity.module;

import dev.turtywurty.turtylib.common.blockentity.ModularBlockEntity;
import net.minecraft.nbt.CompoundTag;

public interface Module {
    void deserialize(ModularBlockEntity blockEntity, CompoundTag nbt);

    default void onLoad(ModularBlockEntity blockEntity) {

    }

    default void onRemoved(ModularBlockEntity blockEntity) {

    }

    void serialize(ModularBlockEntity blockEntity, CompoundTag nbt);

    default void tick(ModularBlockEntity blockEntity) {

    }
}
