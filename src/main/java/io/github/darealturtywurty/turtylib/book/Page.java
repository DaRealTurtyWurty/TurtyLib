package io.github.darealturtywurty.turtylib.book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.darealturtywurty.turtylib.client.util.Resources;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class Page {
    public final Component title;
    public final List<BookWidget> widgets = new ArrayList<>();
    public ResourceLocation texture = Resources.BLANK_BOOK_PAGE;
    public ResourceLocation binderTexture = Resources.BOOK_BINDER;
    
    public Page(Component title) {
        this.title = title;
    }

    public Page(String title) {
        this(title, false);
    }
    
    public Page(String title, boolean translatable) {
        this(translatable ? new TranslatableComponent(title) : new TextComponent(title));
    }

    public Page addWidgets(BookWidget... widgets) {
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
