package dev.turtywurty.turtylib.common.blockentity.module;

import dev.turtywurty.turtylib.common.blockentity.ModularBlockEntity;
import dev.turtywurty.turtylib.core.init.BlockEntityInit;
import dev.turtywurty.turtylib.core.multiblock.Multiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class MultiblockModule implements Module {
    private final Supplier<? extends Multiblock> multiblock;
    private final List<BlockPos> positions = new ArrayList<>();
    private BlockState previous;
    private boolean forRemoval;

    public MultiblockModule(Supplier<? extends Multiblock> multiblockSupplier) {
        this.multiblock = multiblockSupplier;
    }

    public Multiblock getMultiblock() {
        return this.multiblock.get();
    }
    public List<BlockPos> getPositions() {
        return positions;
    }
    public BlockState getPrevious() {
        return this.previous;
    }
    public boolean isForRemoval() {
        return this.forRemoval;
    }

    public void setPositions(Collection<BlockPos> positions) {
        this.positions.addAll(positions);
    }
    public void setPrevious(BlockState previous) {
        this.previous = previous;
    }
    public void setForRemoval(boolean forRemoval) {
        this.forRemoval = forRemoval;
    }

    @Override
    public void deserialize(ModularBlockEntity modularBlockEntity, CompoundTag compoundTag) {
        ListTag compounds = compoundTag.getList("Positions", Tag.TAG_COMPOUND);
        for (Tag tag : compounds) {
            var compound = (CompoundTag) tag;
            this.positions.add(NbtUtils.readBlockPos(compound));
        }
    }

    @Override
    public void serialize(ModularBlockEntity modularBlockEntity, CompoundTag compoundTag) {
        var compounds = new ListTag();
        this.positions.stream().map(NbtUtils::writeBlockPos).forEach(compounds::add);
        compoundTag.put("Positions", compounds);
    }

    // TODO: Find a way to call this method only when if it is replaced by a different block
    public void removeMultiblock(Level level, BlockPos excluded) {
        var positions = new ArrayList<>(this.positions);
        positions.remove(excluded);

        for (BlockPos position : positions) {
            level.getBlockEntity(position, BlockEntityInit.MULTIBLOCK.get()).ifPresentOrElse(blockEntity -> {
                blockEntity.setForRemoval(true);
                level.setBlock(position, blockEntity.getPrevious(), Block.UPDATE_ALL);
            }, () -> {
                BlockEntity blockEntity = level.getBlockEntity(position);
                if(blockEntity instanceof ModularBlockEntity modularBlockEntity) {
                    modularBlockEntity.getModule(MultiblockModule.class).ifPresent(multiblockModule -> {
                        multiblockModule.forRemoval = true;
                        level.setBlock(position, getPrevious(), Block.UPDATE_ALL);
                    });
                }
            });
        }
    }
}
