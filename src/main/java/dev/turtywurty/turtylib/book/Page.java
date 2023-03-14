package dev.turtywurty.turtylib.book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.turtywurty.turtylib.client.util.Resources;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class Page {
    public final Component title;
    public final List<AbstractWidget> widgets = new ArrayList<>();
    public ResourceLocation texture = Resources.BLANK_BOOK_PAGE;
    public ResourceLocation binderTexture = Resources.BOOK_BINDER;

    public Page(Component title) {
        this.title = title;
    }
    
    public Page(String title) {
        this(title, false);
    }

    public Page(String title, boolean translatable) {
        this(translatable ? Component.translatable(title) : Component.literal(title));
    }
    
    public Page addWidgets(AbstractWidget... widgets) {
        Collections.addAll(this.widgets, widgets);
        return this;
    }

    public Page setBinderTexture(ResourceLocation location) {
        this.binderTexture = location;
        return this;
    }
    
    public Page setTexture(ResourceLocation location) {
        this.texture = location;
        return this;
    }
}
