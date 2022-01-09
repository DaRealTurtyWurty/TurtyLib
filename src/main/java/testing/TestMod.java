package testing;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import testing.core.init.BlockEntityInit;
import testing.core.init.BlockInit;

@Mod(TestMod.MODID)
public class TestMod {
    public static final String MODID = "testmod";

    public TestMod() {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        BlockInit.BLOCKS.register(bus);
        BlockEntityInit.BLOCK_ENTITIES.register(bus);
    }
}
