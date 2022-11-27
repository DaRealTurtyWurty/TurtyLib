package io.github.darealturtywurty.turtylib.common.blockentity;

import net.minecraftforge.energy.EnergyStorage;

public class ImprovedEnergyStorage extends EnergyStorage {
    private final ModularBlockEntity blockEntity;

    public ImprovedEnergyStorage(ModularBlockEntity blockEntity, int capacity) {
        super(capacity);
        this.blockEntity = blockEntity;
    }

    public ImprovedEnergyStorage(ModularBlockEntity blockEntity, int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
        this.blockEntity = blockEntity;
    }

    public ImprovedEnergyStorage(ModularBlockEntity blockEntity, int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
        this.blockEntity = blockEntity;
    }

    public ImprovedEnergyStorage(ModularBlockEntity blockEntity, int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
        this.blockEntity = blockEntity;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
        this.blockEntity.update();
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int energyReceived = super.receiveEnergy(maxReceive, simulate);
        if (!simulate && energyReceived > 0) {
            this.blockEntity.update();
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int energyExtracted = super.extractEnergy(maxExtract, simulate);
        if (!simulate && energyExtracted > 0) {
            this.blockEntity.update();
        }
        return energyExtracted;
    }
}
