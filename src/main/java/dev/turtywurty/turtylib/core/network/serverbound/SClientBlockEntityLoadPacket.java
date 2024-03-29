package dev.turtywurty.turtylib.core.network.serverbound;

import dev.turtywurty.turtylib.common.blockentity.ModularBlockEntity;
import dev.turtywurty.turtylib.common.blockentity.MultiblockBlockEntity;
import dev.turtywurty.turtylib.core.network.clientbound.CSyncMultiblockPacket;
import dev.turtywurty.turtylib.common.blockentity.module.MultiblockModule;
import dev.turtywurty.turtylib.core.network.PacketHandler;
import dev.turtywurty.turtylib.core.network.clientbound.CSyncMultiblockControllerPacket;
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

        if (context.getSender() == null)
            return;

        ServerLevel level = context.getSender().getLevel();

        if (level.getBlockEntity(this.position) instanceof ModularBlockEntity modularBlockEntity) {
            if (!modularBlockEntity.hasModule(MultiblockModule.class))
                return;

            MultiblockModule multiblockModule = modularBlockEntity.getModule(MultiblockModule.class).orElseThrow(
                    () -> new IllegalStateException("Controller does not container a multiblock module!"));
            PacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(),
                    new CSyncMultiblockControllerPacket(this.position, multiblockModule.getPositions(), multiblockModule.getPrevious()));
        } else if (level.getBlockEntity(this.position) instanceof MultiblockBlockEntity multiblockBlockEntity) {
            PacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(),
                    new CSyncMultiblockPacket(this.position, multiblockBlockEntity.getController(),
                            multiblockBlockEntity.getPrevious()));
        }
    }
}
