package dev.turtywurty.turtylib.common.blockentity.module;

import dev.turtywurty.turtylib.common.blockentity.ModularBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SidedInventoryModule extends SidedCapabilityModule<IItemHandler> {
    private final SidedInventoryHandler inventoryHandler;
    private final ModularBlockEntity blockEntity;

    public SidedInventoryModule(SidedInventoryHandler.SidedInventoryHandlerBuilder builder) {
        super();
        this.inventoryHandler = builder.handler;
        this.blockEntity = builder.handler.getBlockEntity();
    }

    @Override
    public Capability<IItemHandler> getCapability() {
        return ForgeCapabilities.ITEM_HANDLER;
    }

    public Optional<ItemStackHandler> getInventory(final Direction direction) {
        return this.inventoryHandler.getSide(direction);
    }

    public @Nullable ItemStackHandler getNullableInventory(final Direction direction) {
        return this.inventoryHandler.getSide(direction).orElse(null);
    }

    @Override
    public LazyOptional<IItemHandler> getLazy(Direction direction) {
        return this.inventoryHandler.lazyHandlers.get(direction).cast();
    }

    @Override
    public IItemHandler getCapabilityInstance(Direction direction) {
        return this.inventoryHandler.getSide(direction).orElse(null);
    }

    @Override
    public void serialize(ModularBlockEntity blockEntity, CompoundTag nbt) {
        var inventories = new ListTag();
        this.inventoryHandler.handlers.entrySet().stream().filter(entry -> entry.getValue().isPresent())
                .forEach(entry -> {
                    var inventory = new CompoundTag();
                    inventory.putString("Direction", entry.getKey().name());
                    inventory.put("Inventory", entry.getValue().get().serializeNBT());
                    inventories.add(inventory);
                });

        nbt.put("Inventories", inventories);
    }

    @Override
    public void deserialize(ModularBlockEntity blockEntity, CompoundTag nbt) {
        var inventories = nbt.getList("Inventories", Tag.TAG_COMPOUND);
        for (Tag tag : inventories) {
            if (!(tag instanceof CompoundTag inventory))
                continue;

            var direction = Direction.valueOf(inventory.getString("Direction"));
            var handler = SidedInventoryHandler.createInventory(this.blockEntity, 1);
            handler.deserializeNBT(inventory.getCompound("Inventory"));
            this.inventoryHandler.setSide(direction, handler);
        }
    }

    public static class SidedInventoryHandler implements IItemHandler {
        protected final Map<Direction, Optional<ItemStackHandler>> handlers = new HashMap<>();
        protected final Map<Direction, LazyOptional<ItemStackHandler>> lazyHandlers = new HashMap<>();

        protected final ModularBlockEntity blockEntity;

        public SidedInventoryHandler(final @NotNull ModularBlockEntity blockEntity) {
            for (final Direction direction : Direction.values()) {
                this.handlers.put(direction, Optional.empty());
                this.lazyHandlers.put(direction, LazyOptional.empty());
            }

            this.blockEntity = blockEntity;
        }

        public void setSide(final @NotNull Direction direction, final @NotNull ItemStackHandler handler) {
            this.handlers.put(direction, Optional.of(handler));
            this.lazyHandlers.put(direction, LazyOptional.of(() -> handler));
        }

        public ItemStackHandler setSide(final @NotNull Direction direction, int size) {
            if (size <= 0) throw new IllegalArgumentException("Size must be greater than 0! (was " + size + ")");

            final var handler = createInventory(this.blockEntity, size);

            this.handlers.put(direction, Optional.of(handler));
            this.lazyHandlers.put(direction, LazyOptional.of(() -> handler));
            return handler;
        }

        public static ItemStackHandler createInventory(ModularBlockEntity blockEntity, int size) {
            if (size <= 0) throw new IllegalArgumentException("Size must be greater than 0! (was " + size + ")");

            return new ItemStackHandler(size) {
                @Override
                protected void onContentsChanged(int slot) {
                    super.onContentsChanged(slot);
                    blockEntity.update();
                }
            };
        }

        public Optional<ItemStackHandler> getSide(final Direction direction) {
            Optional<ItemStackHandler> found = this.handlers.get(direction);
            return found == null ? Optional.empty() : found;
        }

        public void removeSide(final Direction direction) {
            if (direction == null || this.handlers.get(direction).isEmpty()) return;

            this.handlers.put(direction, Optional.empty());
            this.lazyHandlers.put(direction, LazyOptional.empty());
        }

        public void clearSides() {
            for (Direction direction : Direction.values()) {
                this.handlers.put(direction, Optional.empty());
                this.lazyHandlers.put(direction, LazyOptional.empty());
            }
        }

        public void setSides(final ItemStackHandler handler) {
            for (final Direction direction : Direction.values()) {
                this.handlers.put(direction, Optional.ofNullable(handler));
                this.lazyHandlers.put(direction, LazyOptional.of(() -> handler));
            }
        }

        public void setSides(final Map<@NotNull Direction, @Nullable ItemStackHandler> handlers) {
            this.handlers.clear();
            handlers.forEach((direction, handler) -> {
                this.handlers.put(direction, Optional.ofNullable(handler));
                this.lazyHandlers.put(direction, LazyOptional.of(() -> handler));
            });
        }

        public Map<Direction, Optional<ItemStackHandler>> getSides() {
            return Map.copyOf(this.handlers);
        }

        public ModularBlockEntity getBlockEntity() {
            return this.blockEntity;
        }

        @Override
        public int getSlots() {
            return this.handlers.values().stream().mapToInt(handler -> handler.map(IItemHandler::getSlots).orElse(0))
                    .sum();
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return false;
        }

        public static class SidedInventoryHandlerBuilder {
            protected final SidedInventoryHandler handler;

            public SidedInventoryHandlerBuilder(final @NotNull ModularBlockEntity blockEntity) {
                this.handler = new SidedInventoryHandler(blockEntity);
            }

            public SidedInventoryHandlerBuilder setSide(final @NotNull Direction direction, final @NotNull ItemStackHandler handler) {
                this.handler.setSide(direction, handler);
                return this;
            }

            public SidedInventoryHandlerBuilder setSide(final @NotNull Direction direction, int size) {
                this.handler.setSide(direction, size);
                return this;
            }

            public SidedInventoryHandlerBuilder setSides(final @NotNull ItemStackHandler handler) {
                this.handler.setSides(handler);
                return this;
            }

            public SidedInventoryHandlerBuilder setSides(final @NotNull Map<@NotNull Direction, @Nullable ItemStackHandler> handlers) {
                this.handler.setSides(handlers);
                return this;
            }
        }
    }
}
