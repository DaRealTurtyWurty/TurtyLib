package io.github.darealturtywurty.turtylib.common.blockentity.module;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import io.github.darealturtywurty.turtylib.common.blockentity.ModularBlockEntity;
import io.github.darealturtywurty.turtylib.core.data.MultiblockData;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class MultiblockModule implements Module {
    private final Map<BlockPos, BlockState> posMap = new HashMap<>();
    private BlockPos controller;
    private final MultiblockData data;
    private final Level level;
    
    public MultiblockModule(ModularBlockEntity controller, MultiblockData data) {
        this.controller = controller.getBlockPos();
        this.data = data;
        this.level = controller.getLevel();
    }

    @Override
    public void deserialize(ModularBlockEntity blockEntity, CompoundTag nbt) {
        this.controller = NbtUtils.readBlockPos(nbt.getCompound("ControllerPos"));
        this.posMap.clear();
        nbt.getList("Positions", Tag.TAG_COMPOUND).stream().map(CompoundTag.class::cast).map(NbtUtils::readBlockPos)
            .forEach(position -> this.posMap.put(position, this.level.getBlockState(position)));
    }

    @Override
    public void onLoad(ModularBlockEntity blockEntity) {
        final Map<Vec3i, BlockState> offsets = new HashMap<>();
        final var controller = new AtomicReference<Vec3i>();
        
        setupOffsets(this.data, offsets, controller);
        
        final Vec3i controllerPos = controller.get();
        offsetOffsetsAndPlace(offsets, this.posMap, this.controller, this.level, controllerPos);
    }

    @Override
    public void serialize(ModularBlockEntity blockEntity, CompoundTag nbt) {
        final var positions = new ListTag();
        this.posMap.keySet().forEach(pos -> positions.add(NbtUtils.writeBlockPos(pos)));
        nbt.put("Positions", positions);
        nbt.put("ControllerPos", NbtUtils.writeBlockPos(this.controller));
    }

    private static void offsetOffsetsAndPlace(Map<Vec3i, BlockState> offsets, Map<BlockPos, BlockState> posMap,
        BlockPos controllerPos, Level level, Vec3i controllerOffset) {
        offsets.entrySet().parallelStream().forEachOrdered(entry -> {
            final Vec3i pos = entry.getKey();
            final BlockState state = entry.getValue();
            
            final Vec3i offset = Vec3i.ZERO;
            if (pos.getX() < controllerOffset.getX()) {
                offset.west(controllerOffset.getX() - pos.getX());
            } else {
                offset.east(controllerOffset.getX() - pos.getX());
            }
            
            if (pos.getY() < controllerOffset.getY()) {
                offset.below(controllerOffset.getY() - pos.getY());
            } else {
                offset.above(controllerOffset.getY() - pos.getY());
            }
            
            if (pos.getZ() < controllerOffset.getZ()) {
                offset.south(controllerOffset.getZ() - pos.getZ());
            } else {
                offset.north(controllerOffset.getZ() - pos.getZ());
            }
            
            posMap.put(controllerPos.offset(offset), state);
            level.setBlockAndUpdate(controllerPos.offset(offset), state);
        });
    }

    private static void setupOffsets(MultiblockData data, Map<Vec3i, BlockState> offsets,
        AtomicReference<Vec3i> controller) {
        data.layers().forEach((yPos, keyList) -> {
            int xPos = 0;
            for (final String x : keyList) {
                int zPos = 0;
                for (final String z : x.split("")) {
                    if (z.equalsIgnoreCase(data.controllerKey())) {
                        controller.set(new Vec3i(xPos, yPos, zPos));
                        zPos++;
                        continue;
                    }
                    
                    final List<BlockState> states = data.keys().get(z);
                    offsets.put(new Vec3i(xPos, yPos, zPos),
                        states.get(ThreadLocalRandom.current().nextInt(states.size() - 1)));

                    zPos++;
                }

                xPos++;
            }
        });
    }
}
