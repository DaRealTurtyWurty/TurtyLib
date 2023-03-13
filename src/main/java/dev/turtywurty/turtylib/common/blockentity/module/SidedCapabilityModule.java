package dev.turtywurty.turtylib.common.blockentity.module;

import net.minecraft.core.Direction;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public abstract class SidedCapabilityModule<T> implements CapabilityModule<T> {
    protected final Map<Direction, T> sides = new EnumMap<>(Direction.class);
    protected final Map<Direction, LazyOptional<T>> lazySides = new EnumMap<>(Direction.class);

    public SidedCapabilityModule() {
        for (final Direction direction : Direction.values()) {
            this.sides.put(direction, getCapabilityInstance());
        }

        for (final Direction direction : Direction.values()) {
            this.lazySides.put(direction, LazyOptional.of(() -> this.sides.get(direction)));
        }
    }

    @Override
    public T getCapabilityInstance(final Direction direction) {
        return this.sides.get(direction);
    }

    @Override
    public T getCapabilityInstance() {
        return this.sides.get(Direction.NORTH);
    }

    @Override
    public void invalidate() {
        this.lazySides.values().forEach(LazyOptional::invalidate);
    }

    public void setSide(final Direction direction, final T instance) {
        this.sides.put(direction, instance);
    }

    public void removeSide(final Direction direction) {
        this.sides.remove(direction);
    }

    public void clearSides() {
        this.sides.clear();
    }

    public void setSides(final T instance) {
        for (final Direction direction : Direction.values()) {
            this.sides.put(direction, instance);
        }
    }

    public void setSides(final Map<Direction, T> instances) {
        this.sides.clear();
        this.sides.putAll(instances);
    }

    public Map<Direction, T> getSides() {
        return Collections.unmodifiableMap(this.sides);
    }

    public boolean hasSide(final Direction direction) {
        return this.sides.containsKey(direction);
    }
}
