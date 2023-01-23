package io.github.darealturtywurty.turtylib.common.blockentity.module;

import io.github.darealturtywurty.turtylib.common.blockentity.ModularBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class InventoryModule implements CapabilityModule<IItemHandler> {
    protected final int size;

    private final IItemHandlerModifiable inventory;
    protected LazyOptional<IItemHandler> handler;

    public InventoryModule(ModularBlockEntity be, int size) {
        this.size = size;
        this.inventory = createInventory(be);
        this.handler = LazyOptional.of(() -> this.inventory);
    }

    @Override
    public void deserialize(ModularBlockEntity blockEntity, CompoundTag nbt) {
        final ListTag list = nbt.getList("Items", 10);
        for (int x = 0; x < list.size(); ++x) {
            final CompoundTag compound = list.getCompound(x);
            final int r = compound.getByte("Slot") & 255;
            final int invslots = this.inventory.getSlots();
            if (r >= 0 && r < invslots) {
                this.inventory.setStackInSlot(r, ItemStack.of(compound));
            }
        }
    }

    @Override
    public IItemHandlerModifiable getCapabilityInstance() {
        return this.inventory;
    }

    @Override
    public Capability<IItemHandler> getCapability() {
        return ForgeCapabilities.ITEM_HANDLER;
    }

    @Override
    public void invalidate() {
        this.handler.invalidate();
    }

    @Override
    public void serialize(ModularBlockEntity blockEntity, CompoundTag nbt) {
        final var list = new ListTag();
        final int slots = this.inventory.getSlots();
        for (int x = 0; x < slots; ++x) {
            final ItemStack stack = this.inventory.getStackInSlot(x);
            final var compound = new CompoundTag();
            compound.putByte("Slot", (byte) x);
            stack.save(compound);
            list.add(compound);
        }

        nbt.put("Items", list);
    }

    protected IItemHandlerModifiable createInventory(ModularBlockEntity be) {
        return new ItemStackHandler(this.size) {
            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                be.update();
            }
        };
    }
}
