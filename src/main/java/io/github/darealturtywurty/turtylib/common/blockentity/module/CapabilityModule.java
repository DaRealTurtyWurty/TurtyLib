package io.github.darealturtywurty.turtylib.common.blockentity.module;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public interface CapabilityModule<T> extends Module {
    T getCapabilityInstance();

    default T getCapabilityInstance(final Direction direction) {
        return getCapabilityInstance();
    }

    Capability<T> getCapability();

    default LazyOptional<T> getLazy() {
        return LazyOptional.of(this::getCapabilityInstance);
    }

    default LazyOptional<T> getLazy(final Direction direction) {
        return LazyOptional.of(() -> getCapabilityInstance(direction));
    }

    void invalidate();

    default void invalidate(final Direction direction) {
        getLazy(direction).invalidate();
    }
}
