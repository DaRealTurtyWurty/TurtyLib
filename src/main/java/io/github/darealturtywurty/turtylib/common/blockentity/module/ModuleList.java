package io.github.darealturtywurty.turtylib.common.blockentity.module;

import java.util.ArrayList;

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
