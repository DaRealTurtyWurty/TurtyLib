package io.github.darealturtywurty.turtylib.common.blockentity.module;

import io.github.darealturtywurty.turtylib.common.blockentity.ModularBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyModule implements CapabilityModule<EnergyStorage> {


    private static final String CURRENT_ENERGY_KEY = "current_energy";

    private final EnergyStorage energyStorage;
    protected LazyOptional<IEnergyStorage> handler;

    public EnergyModule(final int capacity) {

        energyStorage = new EnergyStorage(capacity) {
            @Override
            public void deserializeNBT(final Tag nbt) {
            }

            @Override
            public Tag serializeNBT() {
                return null;
            }


        };
        this.handler = LazyOptional.of(() -> this.energyStorage);
    }

    public EnergyModule(final int capacity, final int maxTransfer) {

        energyStorage = new EnergyStorage(capacity, maxTransfer) {
            @Override
            public void deserializeNBT(final Tag nbt) {
            }

            @Override
            public Tag serializeNBT() {
                return null;
            }


        };
        this.handler = LazyOptional.of(() -> this.energyStorage);

    }

    public EnergyModule(final int capacity, final int maxRecieve, final int maxExtract) {

        energyStorage = new EnergyStorage(capacity, maxRecieve, maxExtract) {
            @Override
            public void deserializeNBT(final Tag nbt) {
            }

            @Override
            public Tag serializeNBT() {
                return null;
            }


        };
        this.handler = LazyOptional.of(() -> this.energyStorage);

    }

    public EnergyModule(final int capacity, final int maxRecieve, final int maxExtract, final int startingEnergy) {

        energyStorage = new EnergyStorage(capacity, maxRecieve, maxExtract, startingEnergy) {
            @Override
            public void deserializeNBT(final Tag nbt) {
            }

            @Override
            public Tag serializeNBT() {
                return null;
            }


        };
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
