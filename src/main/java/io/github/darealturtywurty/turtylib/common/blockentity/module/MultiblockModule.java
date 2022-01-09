package io.github.darealturtywurty.turtylib.common.blockentity.module;

import java.util.HashMap;
import java.util.Map;

import com.mojang.math.Vector3f;

import io.github.darealturtywurty.turtylib.common.blockentity.ModularBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class MultiblockModule implements Module {
    private final Map<Vector3f, BlockState> offsetMap = new HashMap<>();

    @Override
    public void deserialize(ModularBlockEntity blockEntity, CompoundTag nbt) {

    }

    @Override
    public void onLoad(ModularBlockEntity blockEntity) {
        
    }

    @Override
    public void serialize(ModularBlockEntity blockEntity, CompoundTag nbt) {
        
    }
    
    @Override
    public void tick(ModularBlockEntity blockEntity) {

    }
}
