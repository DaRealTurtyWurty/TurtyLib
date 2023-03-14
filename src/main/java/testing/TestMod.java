package testing;

import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import testing.client.Renderer;
import testing.core.init.BlockEntityInit;
import testing.core.init.BlockInit;
import testing.core.init.ItemInit;
import testing.core.init.MultiblockInit;

@Mod(TestMod.MODID)
public class TestMod {
    public static final String MODID = "testmod";

    public TestMod() {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        BlockInit.BLOCKS.register(bus);
        BlockEntityInit.BLOCK_ENTITIES.register(bus);
        MultiblockInit.MULTIBLOCKS.register(bus);
        ItemInit.ITEMS.register(bus);

        bus.addListener(this::registerRenderers);
    }

    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntityInit.TEST.get(), Renderer::new);
    }
}
