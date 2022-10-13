package io.github.darealturtywurty.turtylib.common.blockentity.module;

import io.github.darealturtywurty.turtylib.common.blockentity.ModularBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.Optional;

public class EnergyModule implements CapabilityModule<EnergyStorage> {


    private static final String CURRENT_ENERGY_KEY = "current_energy";

    private final EnergyStorage energyStorage;
    protected LazyOptional<IEnergyStorage> handler;


    public EnergyModule(final int capacity, @Nullable Integer maxReceive, @Nullable Integer maxExtract) {
        maxReceive = Optional.ofNullable(maxReceive).orElse(0);
        maxExtract = Optional.ofNullable(maxExtract).orElse(0);
        energyStorage = new EnergyStorage(capacity, maxReceive, maxExtract);
        this.handler = LazyOptional.of(() -> this.energyStorage);
    }

    @Override
    public EnergyStorage getCapability() {
        return this.energyStorage;
    }

    @Override
    public void invalidate() {
        this.handler.invalidate();
    }

    @Override
    public void deserialize(final ModularBlockEntity blockEntity, final CompoundTag nbt) {
        this.handler.ifPresent(iEnergyStorage -> iEnergyStorage.receiveEnergy(nbt.getInt(CURRENT_ENERGY_KEY), false));
    }

    @Override
    public void serialize(final ModularBlockEntity blockEntity, final CompoundTag nbt) {
        nbt.putInt(CURRENT_ENERGY_KEY, this.energyStorage.getEnergyStored());
    }
}
