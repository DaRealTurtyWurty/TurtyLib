package io.github.darealturtywurty.turtylib;

import io.github.darealturtywurty.turtylib.core.init.BlockEntityInit;
import io.github.darealturtywurty.turtylib.core.init.BlockInit;
import io.github.darealturtywurty.turtylib.core.multiblock.Multiblock;
import io.github.darealturtywurty.turtylib.core.network.PacketHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

@Mod(TurtyLib.MODID)
public class TurtyLib {
    public static final String MODID = "turtylib";
    public static final Logger LOGGER = LogManager.getLogger();

    public TurtyLib() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        BlockInit.BLOCKS.register(bus);
        BlockEntityInit.BLOCK_ENTITIES.register(bus);
        TurtyLib.MULTIBLOCKS.register(bus);

        bus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        PacketHandler.init();
    }

    public static final ResourceLocation MULTIBLOCK_REGISTRY_KEY = new ResourceLocation(TurtyLib.MODID, "multiblocks");

    private static final DeferredRegister<Multiblock> MULTIBLOCKS = DeferredRegister.create(MULTIBLOCK_REGISTRY_KEY,
            TurtyLib.MODID);

    public static final Supplier<IForgeRegistry<Multiblock>> MULTIBLOCK_REGISTRY = MULTIBLOCKS.makeRegistry(
            RegistryBuilder::new);
}
