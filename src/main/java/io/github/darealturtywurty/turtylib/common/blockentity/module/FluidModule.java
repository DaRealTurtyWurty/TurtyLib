package io.github.darealturtywurty.turtylib.common.blockentity.module;

import java.util.function.Predicate;

import io.github.darealturtywurty.turtylib.common.blockentity.ModularBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class FluidModule implements CapabilityModule<IFluidTank> {
    protected final int capacity;
    protected Predicate<FluidStack> validator;
    
    private final FluidTank tank;
    protected LazyOptional<IFluidTank> handler;
    
    public FluidModule(ModularBlockEntity be, int capacity) {
        this(be, capacity, stack -> true);
    }
    
    public FluidModule(ModularBlockEntity be, int capacity, Predicate<FluidStack> validator) {
        this.capacity = capacity;
        this.validator = validator;
        
        this.tank = createTank(be);
        this.handler = LazyOptional.of(() -> this.tank);
    }
    
    @Override
    public void deserialize(ModularBlockEntity blockEntity, CompoundTag nbt) {
        this.tank.readFromNBT(nbt);
    }

    @Override
    public IFluidTank getCapability() {
        return this.tank;
    }

    @Override
    public void invalidate() {
        this.handler.invalidate();
    }

    @Override
    public void serialize(ModularBlockEntity blockEntity, CompoundTag nbt) {
        this.tank.writeToNBT(nbt);
    }

    protected FluidTank createTank(ModularBlockEntity be) {
        return new FluidTank(this.capacity, this.validator) {
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                be.update();
            }
        };
    }
}
