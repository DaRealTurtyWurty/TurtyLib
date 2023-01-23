package io.github.darealturtywurty.turtylib.common.blockentity.module;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public interface CapabilityModule<T> extends Module {
    T getCapabilityInstance();
    Capability<T> getCapability();

    default LazyOptional<T> getLazy() {
        return LazyOptional.of(this::getCapabilityInstance);
    }

    void invalidate();
}
