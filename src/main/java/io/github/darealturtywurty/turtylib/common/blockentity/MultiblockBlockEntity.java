package io.github.darealturtywurty.turtylib.common.blockentity;

import io.github.darealturtywurty.turtylib.core.init.BlockEntityInit;
import io.github.darealturtywurty.turtylib.core.network.PacketHandler;
import io.github.darealturtywurty.turtylib.core.network.serverbound.SClientBlockEntityLoadPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class MultiblockBlockEntity extends BlockEntity {
    private BlockPos controller;

    public MultiblockBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.MULTIBLOCK.get(), pos, state);
    }

    public BlockPos getController() {
        return this.controller;
    }

    public void setController(BlockPos controller) {
        this.controller = controller;
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.controller = NbtUtils.readBlockPos(tag.getCompound("controller"));
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("controller", NbtUtils.writeBlockPos(this.controller));
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (level == null)
            return;

        if (level.isClientSide()) {
            PacketHandler.CHANNEL.sendToServer(new SClientBlockEntityLoadPacket(this.getBlockPos()));
        }
    }
}
