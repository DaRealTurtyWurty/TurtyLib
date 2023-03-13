package dev.turtywurty.turtylib.core.network.clientbound;

import dev.turtywurty.turtylib.common.blockentity.ModularBlockEntity;
import dev.turtywurty.turtylib.common.blockentity.module.MultiblockModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class CSyncMultiblockControllerPacket {
    private final BlockPos controller;
    private final List<BlockPos> positions;
    private final BlockState previous;

    public CSyncMultiblockControllerPacket(FriendlyByteBuf friendlyByteBuf) {
        this(friendlyByteBuf.readBlockPos(), friendlyByteBuf.readList(FriendlyByteBuf::readBlockPos),
                NbtUtils.readBlockState(friendlyByteBuf.readNbt()));
    }

    public CSyncMultiblockControllerPacket(BlockPos controller, List<BlockPos> positions, BlockState previous) {
        this.controller = controller;
        this.positions = positions;
        this.previous = previous;
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBlockPos(this.controller);
        friendlyByteBuf.writeCollection(this.positions, FriendlyByteBuf::writeBlockPos);
        friendlyByteBuf.writeNbt(NbtUtils.writeBlockState(this.previous));
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().setPacketHandled(true);

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null)
            return;

        if (level.getBlockEntity(this.controller) instanceof ModularBlockEntity modularBlockEntity) {
            MultiblockModule multiblockModule = modularBlockEntity.getModule(MultiblockModule.class).orElseThrow(
                    () -> new IllegalStateException("Controller does not container a multiblock module!"));

            multiblockModule.setPositions(this.positions);
            multiblockModule.setPrevious(this.previous);
        }
    }
}
