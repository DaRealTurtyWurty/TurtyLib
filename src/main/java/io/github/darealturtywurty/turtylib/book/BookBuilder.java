package io.github.darealturtywurty.turtylib.book;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.world.item.Item;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;

public class BookBuilder {
    protected final String modid, name;
    protected final Item.Properties itemProperties = new Item.Properties();
    protected final SafeRunnable openGui;
    protected Supplier<Item> item;

    public BookBuilder(String modid, String name, SafeRunnable openGui) {
        this.modid = modid;
        this.name = name;
        this.openGui = openGui;
        this.item = () -> new AdvancedBookItem(this.itemProperties, this.openGui);
    }

    public BookBuilder modifyProperties(Consumer<Item.Properties> modifyProperties) {
        modifyProperties.accept(this.itemProperties);
        return this;
    }
}
