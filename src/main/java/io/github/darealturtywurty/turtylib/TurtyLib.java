package io.github.darealturtywurty.turtylib;

import io.github.darealturtywurty.turtylib.core.init.BlockInit;
import io.github.darealturtywurty.turtylib.core.multiblock.Multiblock;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

@Mod(TurtyLib.MODID)
public class TurtyLib {
    public static final String MODID = "turtylib";

    public TurtyLib() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        BlockInit.BLOCKS.register(bus);
        TurtyLib.MULTIBLOCKS.register(bus);
    }

    private static final DeferredRegister<Multiblock> MULTIBLOCKS =
            DeferredRegister.create(new ResourceLocation(TurtyLib.MODID, "multiblocks"), TurtyLib.MODID);

    public static final Supplier<IForgeRegistry<Multiblock>> MULTIBLOCK_REGISTRY = MULTIBLOCKS.makeRegistry(RegistryBuilder::new);
}
