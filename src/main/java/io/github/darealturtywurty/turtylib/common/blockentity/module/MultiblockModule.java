package io.github.darealturtywurty.turtylib.common.blockentity.module;

import io.github.darealturtywurty.turtylib.common.blockentity.ModularBlockEntity;
import io.github.darealturtywurty.turtylib.core.multiblock.Multiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class MultiblockModule implements Module {
    private final Supplier<Multiblock> multiblock;
    private final List<BlockPos> positions = new ArrayList<>();

    public MultiblockModule(Supplier<Multiblock> multiblockSupplier) {
        this.multiblock = multiblockSupplier;
    }

    public Multiblock getMultiblock() {
        return this.multiblock.get();
    }

    public List<BlockPos> getPositions() {
        return positions;
    }

    public void setPositions(Collection<BlockPos> positions) {
        this.positions.addAll(positions);
    }

    @Override
    public void deserialize(ModularBlockEntity modularBlockEntity, CompoundTag compoundTag) {
        ListTag compounds = compoundTag.getList("Positions", Tag.TAG_COMPOUND);
        for(Tag tag : compounds) {
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
}
