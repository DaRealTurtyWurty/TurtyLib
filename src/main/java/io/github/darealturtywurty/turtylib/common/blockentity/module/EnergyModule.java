package io.github.darealturtywurty.turtylib.common.blockentity.module;

import io.github.darealturtywurty.turtylib.common.blockentity.ImprovedEnergyStorage;
import io.github.darealturtywurty.turtylib.common.blockentity.ModularBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyModule implements CapabilityModule<IEnergyStorage> {
    private static final String ENERGY_KEY = "Energy";

    protected int energy;
    protected int capacity;
    protected int maxReceive;
    protected int maxExtract;

    private final ImprovedEnergyStorage storage;
    protected LazyOptional<IEnergyStorage> handler;

    public EnergyModule(ModularBlockEntity be, Builder builder) {
        this(be, builder.energy, builder.capacity, builder.maxReceive, builder.maxExtract);
    }

    public EnergyModule(ModularBlockEntity be, int energy, int capacity, int maxReceive, int maxExtract) {
        this.energy = energy;
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;

        this.storage = createStorage(be);
        this.handler = LazyOptional.of(() -> this.storage);
    }

    public EnergyModule(ModularBlockEntity be) {
        this(be, 0, 1000, 1000, 1000);
    }

    @Override
    public ImprovedEnergyStorage getCapabilityInstance() {
        return this.storage;
    }

    @Override
    public Capability<IEnergyStorage> getCapability() {
        return ForgeCapabilities.ENERGY;
    }

    @Override
    public void invalidate() {
        this.handler.invalidate();
    }

    @Override
    public void deserialize(ModularBlockEntity blockEntity, CompoundTag nbt) {
        this.storage.deserializeNBT(nbt.get(ENERGY_KEY));
    }

    @Override
    public void serialize(ModularBlockEntity blockEntity, CompoundTag nbt) {
        nbt.put(ENERGY_KEY, this.storage.serializeNBT());
    }

    protected ImprovedEnergyStorage createStorage(ModularBlockEntity be) {
        return new ImprovedEnergyStorage(be, this.capacity, this.maxReceive, this.maxExtract, this.energy);
    }

    public static class Builder {
        private int energy;
        private int capacity;
        private int maxReceive;
        private int maxExtract;

        public Builder energy(int energy) {
            this.energy = energy;
            return this;
        }

        public Builder capacity(int capacity) {
            this.capacity = capacity;
            return this;
        }

        public Builder maxReceive(int maxReceive) {
            this.maxReceive = maxReceive;
            return this;
        }

        public Builder maxExtract(int maxExtract) {
            this.maxExtract = maxExtract;
            return this;
        }
    }
}
