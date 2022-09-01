package io.github.darealturtywurty.turtylib.core.network.serverbound;

import io.github.darealturtywurty.turtylib.common.blockentity.ModularBlockEntity;
import io.github.darealturtywurty.turtylib.common.blockentity.module.MultiblockModule;
import io.github.darealturtywurty.turtylib.core.network.PacketHandler;
import io.github.darealturtywurty.turtylib.core.network.clientbound.CSyncMultiblockPositionsPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class SClientBlockEntityLoadPacket {
    private final BlockPos position;

    public SClientBlockEntityLoadPacket(BlockPos worldPosition) {
        this.position = worldPosition;
    }

    public SClientBlockEntityLoadPacket(FriendlyByteBuf friendlyByteBuf) {
        this(friendlyByteBuf.readBlockPos());
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBlockPos(this.position);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.setPacketHandled(true);

        if(context.getSender() == null)
            return;

        ServerLevel level = context.getSender().getLevel();

        if(level.getBlockEntity(this.position) instanceof ModularBlockEntity modularBlockEntity) {
            if(!modularBlockEntity.hasModule(MultiblockModule.class))
                return;

            MultiblockModule multiblockModule = modularBlockEntity.getModule(MultiblockModule.class).orElseThrow(
                    () -> new IllegalStateException("Controller does not container a multiblock module!"));
            PacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new CSyncMultiblockPositionsPacket(this.position, multiblockModule.getPositions()));
        }
    }
}
