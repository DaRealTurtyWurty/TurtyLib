package io.github.darealturtywurty.turtylib.book;

import io.github.darealturtywurty.turtylib.client.util.ClientUtils;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;

public class ClientBookBuilder {
    protected AdvancedBookScreen screen;
    protected final int pageCount;
    protected final Component title;
    
    public ClientBookBuilder(int pageCount, Component title) {
        this.pageCount = pageCount;
        this.title = title;
    }

    public ClientBookBuilder setScreen() {
        this.screen = new AdvancedBookScreen(null);
        return this;
    }

    @SuppressWarnings("resource")
    public static SafeRunnable constructGuiOpen(ClientBookBuilder builder) {
        return () -> {
            if (ClientUtils.getMinecraft().level != null) {
                ClientUtils.getMinecraft().setScreen(builder.screen);
            }
        };
    }
    
}
