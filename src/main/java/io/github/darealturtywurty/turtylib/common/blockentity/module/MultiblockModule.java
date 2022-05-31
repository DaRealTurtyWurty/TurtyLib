package io.github.darealturtywurty.turtylib.common.blockentity.module;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.mojang.math.Vector3f;

import io.github.darealturtywurty.turtylib.common.blockentity.ModularBlockEntity;
import io.github.darealturtywurty.turtylib.core.data.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class MultiblockModule implements Module {
    public final Map<Vector3f, BlockState> offsetMap = new HashMap<>();
    public final MultiblockData data;
    public final BlockPos controllerPos;
    
    public MultiblockModule(MultiblockData data, ModularBlockEntity controller) {
        this.data = data;
        this.controllerPos = controller.getBlockPos();
        final AtomicInteger xPos = new AtomicInteger(), yPos = new AtomicInteger(), zPos = new AtomicInteger();
        
        this.data.layers().forEach((y, keyList) -> {
            yPos.incrementAndGet();
            for (final String x : keyList) {
                xPos.incrementAndGet();
                for (final String z : x.split("")) {
                    zPos.incrementAndGet();
                    if (!z.equalsIgnoreCase(data.controllerKey())) {
                        
                    }
                }

                zPos.set(0);
            }

            xPos.set(0);
        });
    }

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
