package dev.turtywurty.turtylib.common.blockentity;

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

    /**
     * This method is used to internally receive energy, and avoids the super method's max receive check.
     * @param maxReceive The maximum amount of energy to receive.
     * @param simulate If true, the charge will only be simulated.
     * @return The amount of energy that was (or would have been, if simulated) received.
     */
    public int receiveEnergyInternal(int maxReceive, boolean simulate) {
        int energyReceived = Math.min(this.capacity - this.energy, maxReceive);
        if (!simulate) {
            this.energy += energyReceived;
            this.blockEntity.update();
        }

        return energyReceived;
    }

    /**
     * This method is used to internally extract energy, and avoids the super method's max extract check.
     * @param maxExtract The maximum amount of energy to extract.
     * @param simulate If true, the discharge will only be simulated.
     * @return The amount of energy that was (or would have been, if simulated) extracted.
     */
    public int extractEnergyInternal(int maxExtract, boolean simulate) {
        int energyExtracted = Math.min(this.energy, maxExtract);
        if (!simulate) {
            this.energy -= energyExtracted;
            this.blockEntity.update();
        }

        return energyExtracted;
    }
}
