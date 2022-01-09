package io.github.darealturtywurty.turtylib.core.events;

import io.github.darealturtywurty.turtylib.TurtyLib;
import io.github.darealturtywurty.turtylib.core.data.MultiblockDataManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = TurtyLib.MODID, bus = Bus.FORGE, value = Dist.DEDICATED_SERVER)
public final class ForgeServerEvents {
    protected static final MultiblockDataManager MULTIBLOCK_LISTENER = new MultiblockDataManager();
    
    private ForgeServerEvents() {
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(MULTIBLOCK_LISTENER);
    }
}
