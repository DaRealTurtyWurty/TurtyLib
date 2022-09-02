package io.github.darealturtywurty.turtylib.core.network.clientbound;

import io.github.darealturtywurty.turtylib.common.blockentity.MultiblockBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CSyncControllerPositionPacket {
    private final BlockPos position;
    private final BlockPos controller;

    public CSyncControllerPositionPacket(BlockPos position, BlockPos controller) {
        this.position = position;
        this.controller = controller;
    }

    public CSyncControllerPositionPacket(FriendlyByteBuf friendlyByteBuf) {
        this(friendlyByteBuf.readBlockPos(), friendlyByteBuf.readBlockPos());
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBlockPos(this.position);
        friendlyByteBuf.writeBlockPos(this.controller);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.setPacketHandled(true);

        ClientLevel level = Minecraft.getInstance().level;
        if(level == null)
            return;

        if(level.getBlockEntity(this.position) instanceof MultiblockBlockEntity multiblockBlockEntity) {
            multiblockBlockEntity.setController(this.controller);
        }
    }
}
