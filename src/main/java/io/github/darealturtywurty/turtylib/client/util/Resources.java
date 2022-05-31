package io.github.darealturtywurty.turtylib.client.util;

import io.github.darealturtywurty.turtylib.TurtyLib;
import net.minecraft.resources.ResourceLocation;

public final class Resources {
    public static final ResourceLocation TAB_LOC = component("tabs");
    public static final ResourceLocation INFORMATION = component("information");
    public static final ResourceLocation TOGGLE_SWITCH = component("toggle_switch");
    public static final ResourceLocation TOGGLE_SWITCH_RED_GREEN = component("toggle_switch_red_green");
    public static final ResourceLocation POPOUT = component("popout");
    public static final ResourceLocation TAB_BACKGROUND = component("tab_background");
    public static final ResourceLocation BLANK_BOOK_PAGE = book("blank_book_page");
    public static final ResourceLocation BOOK_BINDER = book("binder");
    
    private Resources() {
        throw new IllegalStateException("Attempted to construct constants class!");
    }
    
    public static ResourceLocation book(String fileName) {
        return component("book/" + fileName);
    }
    
    public static ResourceLocation component(String fileName) {
        return gui("components/" + fileName);
    }

    public static ResourceLocation gui(String fileName) {
        return new ResourceLocation(TurtyLib.MODID, "textures/gui/" + fileName + ".png");
    }
    
    public static final class Icons {
        public static final ResourceLocation HOME = icon("home");
        public static final ResourceLocation FLAME = icon("flame");
        public static final ResourceLocation WATER_DROPLET = icon("water_droplet");
        public static final ResourceLocation GAS = icon("gas");
        public static final ResourceLocation BACKPACK = icon("backpack");
        public static final ResourceLocation RADAR = icon("radar");
        public static final ResourceLocation MINECART = icon("minecart");
        public static final ResourceLocation LIGHTNING_BOLT = icon("lightning_bolt");
        public static final ResourceLocation ERROR = icon("error");
        public static final ResourceLocation WORLD = icon("world");
        
        private Icons() {
            throw new IllegalStateException("Attempted to construct constants class!");
        }
        
        public static ResourceLocation icon(String name) {
            return gui("icons/" + name);
        }
    }
}
