package io.github.darealturtywurty.turtylib.common.blockentity.module;

import net.minecraftforge.common.util.LazyOptional;

public interface CapabilityModule<T> extends Module {
    T getCapability();

    default LazyOptional<T> getLazy() {
        return LazyOptional.of(this::getCapability);
    }

    void invalidate();
}
