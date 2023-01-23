package io.github.darealturtywurty.turtylib.common.blockentity.module;

import java.util.ArrayList;
import java.util.Collection;

import io.github.darealturtywurty.turtylib.common.blockentity.ModularBlockEntity;
import net.minecraft.nbt.CompoundTag;

public class ModuleList extends ArrayList<Module> {
    private static final long serialVersionUID = 3433768095814931240L;

    @Override
    public boolean add(Module module) {
        if (alreadyContains(module))
            return false;
        return super.add(module);
    }

    @Override
    public void add(int index, Module element) {
        throw new UnsupportedOperationException("Cannot add a module directly to a ModuleList!");
    }

    @Override
    public boolean addAll(Collection<? extends Module> c) {
        throw new UnsupportedOperationException("Cannot add a module directly to a ModuleList!");
    }

    @Override
    public boolean addAll(int index, Collection<? extends Module> c) {
        throw new UnsupportedOperationException("Cannot add a module directly to a ModuleList!");
    }

    @Override
    public Module set(int index, Module element) {
        throw new UnsupportedOperationException("Cannot set a module directly in a ModuleList!");
    }

    public boolean alreadyContains(Module module) {
        return stream().anyMatch(m -> m.getClass().isInstance(module));
    }

    public void deserialize(ModularBlockEntity blockEntity, CompoundTag nbt) {
        forEach(module -> module.deserialize(blockEntity, nbt));
    }
    
    public void invalidate() {
        stream().filter(CapabilityModule.class::isInstance).map(CapabilityModule.class::cast)
                .forEach(CapabilityModule::invalidate);
    }
    
    public void onLoad(ModularBlockEntity blockEntity) {
        forEach(module -> module.onLoad(blockEntity));
    }

    public void onRemoved(ModularBlockEntity blockEntity) {
        forEach(module -> module.onRemoved(blockEntity));
    }

    public void serialize(ModularBlockEntity blockEntity, CompoundTag nbt) {
        forEach(module -> module.serialize(blockEntity, nbt));
    }
    
    public void tick(ModularBlockEntity blockEntity) {
        forEach(module -> module.tick(blockEntity));
    }
}
