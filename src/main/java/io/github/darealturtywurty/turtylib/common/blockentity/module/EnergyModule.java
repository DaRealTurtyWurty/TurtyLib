package io.github.darealturtywurty.turtylib.common.blockentity.module;

import io.github.darealturtywurty.turtylib.common.blockentity.ModularBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyModule implements CapabilityModule<EnergyStorage> {


    private static final String CURRENT_ENERGY_KEY = "current_energy";

    private final EnergyStorage energyStorage;
    protected LazyOptional<IEnergyStorage> handler;


    public EnergyModule(final int capacity, final boolean canTransfer, final boolean canReceive, final int maxReceive, final int maxExtract) {

        if (canReceive && !canTransfer) {
            if (maxReceive <= 0) {
                throw new IllegalArgumentException("this energy storage must have a receive greater than 0");
            }
            energyStorage = new EnergyStorage(capacity, maxReceive, 0);
        } else if (canTransfer && !canReceive) {
            if (maxExtract <= 0) {
                throw new IllegalArgumentException("this energy storage must have an extract greater than 0");
            }
            energyStorage = new EnergyStorage(capacity, maxExtract);
        } else {
            energyStorage = new EnergyStorage(capacity, maxReceive, maxExtract);
        }


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
