package io.github.darealturtywurty.turtylib.common.blockentity;

import io.github.darealturtywurty.turtylib.common.blockentity.module.CapabilityModule;
import io.github.darealturtywurty.turtylib.common.blockentity.module.Module;
import io.github.darealturtywurty.turtylib.common.blockentity.module.ModuleList;
import io.github.darealturtywurty.turtylib.core.network.PacketHandler;
import io.github.darealturtywurty.turtylib.core.network.serverbound.SClientBlockEntityLoadPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ModularBlockEntity extends TickableBlockEntity {
    protected final ModuleList modules = new ModuleList();

    protected ModularBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected <T extends Module> T addModule(T module) {
        this.modules.add(module);
        return module;
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> Optional<T> getModule(Class<T> moduleClass) {
        return this.modules.stream().filter(moduleClass::isInstance).map(it -> (T) it).findFirst();
    }

    public <T extends Module> boolean hasModule(Class<T> moduleClass) {
        return this.modules.stream().anyMatch(moduleClass::isInstance);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
        Optional<CapabilityModule> module = Optional.empty();
        for (Module m : this.modules) {
            if (m instanceof CapabilityModule<?> capModule) {
                if (capModule.getCapability() == cap) {
                    module = Optional.of(capModule);
                    break;
                }
            }
        }

        return module.map(capabilityModule -> capabilityModule.getLazy().cast()).orElse(super.getCapability(cap, side));
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.modules.invalidate();
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.modules.deserialize(this, nbt);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.modules.onLoad(this);

        if (this.level == null) return;

        if (this.level.isClientSide()) {
            PacketHandler.CHANNEL.sendToServer(new SClientBlockEntityLoadPacket(this.worldPosition));
        }
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

    protected List<Consumer<CompoundTag>> getWriteSyncData() {
        return this.modules.stream().map(module -> (Consumer<CompoundTag>) (tag) -> module.serialize(this, tag))
                .collect(Collectors.toList());
    }

    protected List<Consumer<CompoundTag>> getReadSyncData() {
        return this.modules.stream().map(module -> (Consumer<CompoundTag>) (tag) -> module.deserialize(this, tag))
                .collect(Collectors.toList());
    }

    @Override
    public CompoundTag getUpdateTag() {
        var nbt = super.getUpdateTag();
        getWriteSyncData().forEach(writer -> writer.accept(nbt));
        return nbt;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        if (getWriteSyncData().isEmpty()) return super.getUpdatePacket();

        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (getReadSyncData().isEmpty()) {
            return;
        }

        CompoundTag nbt = pkt.getTag();
        getReadSyncData().forEach(reader -> reader.accept(nbt));
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        if (getReadSyncData().isEmpty()) return;

        getReadSyncData().forEach(reader -> reader.accept(tag));
    }
}
