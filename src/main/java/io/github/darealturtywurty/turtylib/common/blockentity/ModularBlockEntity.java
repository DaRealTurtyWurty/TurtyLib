package io.github.darealturtywurty.turtylib.common.blockentity;

import java.util.Optional;

import io.github.darealturtywurty.turtylib.common.blockentity.module.CapabilityModule;
import io.github.darealturtywurty.turtylib.common.blockentity.module.Module;
import io.github.darealturtywurty.turtylib.common.blockentity.module.ModuleList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class ModularBlockEntity extends TickableBlockEntity {
    protected final ModuleList modules = new ModuleList();

    protected ModularBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public <T extends Module> T addModule(T module) {
        this.modules.add(module);
        return module;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap) {
        final Optional<CapabilityModule> module = this.modules.stream().filter(CapabilityModule.class::isInstance)
                .map(CapabilityModule.class::cast).filter(m -> m.getCapability() == cap).findFirst();
        if (!module.isPresent())
            return null;
        return module.get().getLazy().cast();
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.modules.invalidate();
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.modules.deserialize(this, nbt);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.modules.onLoad(this);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.modules.onRemoved(this);
    }

    @Override
    public void tick() {
        super.tick();
        this.modules.tick(this);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        this.modules.serialize(this, nbt);
    }
}
