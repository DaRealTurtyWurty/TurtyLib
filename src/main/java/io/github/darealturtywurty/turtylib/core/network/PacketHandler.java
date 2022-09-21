package io.github.darealturtywurty.turtylib.core.network;

import io.github.darealturtywurty.turtylib.TurtyLib;
import io.github.darealturtywurty.turtylib.core.init.AbstractInit;
import io.github.darealturtywurty.turtylib.core.network.clientbound.CSyncMultiblockPacket;
import io.github.darealturtywurty.turtylib.core.network.clientbound.CSyncMultiblockControllerPacket;
import io.github.darealturtywurty.turtylib.core.network.serverbound.SClientBlockEntityLoadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class PacketHandler extends AbstractInit {
        private static final String PROTOCOL_VERSION = "1";

        public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(TurtyLib.MODID, "main"), () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

        public static void init() {
            int index = 0;
            CHANNEL.messageBuilder(CSyncMultiblockControllerPacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                            .encoder(CSyncMultiblockControllerPacket::encode).decoder(CSyncMultiblockControllerPacket::new)
                            .consumerMainThread(CSyncMultiblockControllerPacket::handle).add();
            CHANNEL.messageBuilder(SClientBlockEntityLoadPacket.class, index++, NetworkDirection.PLAY_TO_SERVER)
                            .encoder(SClientBlockEntityLoadPacket::encode).decoder(SClientBlockEntityLoadPacket::new)
                            .consumerMainThread(SClientBlockEntityLoadPacket::handle).add();
            CHANNEL.messageBuilder(CSyncMultiblockPacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                            .encoder(CSyncMultiblockPacket::encode).decoder(CSyncMultiblockPacket::new)
                            .consumerMainThread(CSyncMultiblockPacket::handle).add();
            TurtyLib.LOGGER.info("Registered {} packets!", index);
        }
}
