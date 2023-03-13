package dev.turtywurty.turtylib.core.network.clientbound;

import dev.turtywurty.turtylib.common.blockentity.MultiblockBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CSyncMultiblockPacket {
    private final BlockPos position;
    private final BlockPos controller;
    private final BlockState previous;

    public CSyncMultiblockPacket(BlockPos position, BlockPos controller, BlockState previous) {
        this.position = position;
        this.controller = controller;
        this.previous = previous;
    }

    public CSyncMultiblockPacket(FriendlyByteBuf friendlyByteBuf) {
        this(friendlyByteBuf.readBlockPos(), friendlyByteBuf.readBlockPos(),
                NbtUtils.readBlockState(friendlyByteBuf.readNbt()));
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBlockPos(this.position);
        friendlyByteBuf.writeBlockPos(this.controller);
        friendlyByteBuf.writeNbt(NbtUtils.writeBlockState(this.previous));
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.setPacketHandled(true);

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null)
            return;

        if (level.getBlockEntity(this.position) instanceof MultiblockBlockEntity multiblockBlockEntity) {
            multiblockBlockEntity.setController(this.controller);
            multiblockBlockEntity.setPrevious(this.previous);
        }
    }
}
