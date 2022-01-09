package io.github.darealturtywurty.turtylib.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.server.packs.resources.ResourceManager;

@SuppressWarnings("resource")
public final class ClientUtils {
    private ClientUtils() {
        throw new IllegalStateException("Attempted to construct utility class!");
    }
    
    public static Font getFont() {
        return getMinecraft().font;
    }
    
    public static Minecraft getMinecraft() {
        return Minecraft.getInstance();
    }

    public static ResourceManager getResourceManager() {
        return getMinecraft().getResourceManager();
    }
}
