package dev.turtywurty.turtylib.common.blockentity.module;

import dev.turtywurty.turtylib.common.blockentity.ModularBlockEntity;
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

    protected final IItemHandlerModifiable inventory;
    protected LazyOptional<IItemHandler> handler;

    public InventoryModule(ModularBlockEntity be, int size) {
        this.size = size;
        this.inventory = createInventory(be);
        this.handler = LazyOptional.of(() -> this.inventory);
    }

    @Override
    public void deserialize(ModularBlockEntity blockEntity, CompoundTag nbt) {
        final ListTag items = nbt.getList("Items", 10);
        for (int itemIndex = 0; itemIndex < items.size(); ++itemIndex) {
            final CompoundTag compound = items.getCompound(itemIndex);
            final int slotIndex = compound.getByte("Slot") & 255;
            final int invSlots = this.inventory.getSlots();
            if (slotIndex < invSlots) {
                this.inventory.setStackInSlot(slotIndex, ItemStack.of(compound));
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
        final var items = new ListTag();
        final int slots = this.inventory.getSlots();
        for (int slotIndex = 0; slotIndex < slots; ++slotIndex) {
            final ItemStack stack = this.inventory.getStackInSlot(slotIndex);
            final var item = new CompoundTag();
            item.putByte("Slot", (byte) slotIndex);
            stack.save(item);
            items.add(item);
        }

        nbt.put("Items", items);
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
