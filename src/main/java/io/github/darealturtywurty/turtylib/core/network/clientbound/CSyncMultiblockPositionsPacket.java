package io.github.darealturtywurty.turtylib.core.network.clientbound;

import io.github.darealturtywurty.turtylib.common.blockentity.ModularBlockEntity;
import io.github.darealturtywurty.turtylib.common.blockentity.module.MultiblockModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class CSyncMultiblockPositionsPacket {
    private final BlockPos controller;
    private final List<BlockPos> positions;

    public CSyncMultiblockPositionsPacket(FriendlyByteBuf friendlyByteBuf) {
        this(friendlyByteBuf.readBlockPos(), friendlyByteBuf.readList(FriendlyByteBuf::readBlockPos));
    }

    public CSyncMultiblockPositionsPacket(BlockPos controller, List<BlockPos> positions) {
        this.controller = controller;
        this.positions = positions;
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBlockPos(this.controller);
        friendlyByteBuf.writeCollection(this.positions, FriendlyByteBuf::writeBlockPos);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().setPacketHandled(true);

        ClientLevel level = Minecraft.getInstance().level;
        if(level == null)
            return;

        if(level.getBlockEntity(this.controller) instanceof ModularBlockEntity modularBlockEntity) {
            MultiblockModule multiblockModule = modularBlockEntity.getModule(MultiblockModule.class).orElseThrow(
                    () -> new IllegalStateException("Controller does not container a multiblock module!"));

            multiblockModule.setPositions(this.positions);
        }
    }
}
