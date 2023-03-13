package dev.turtywurty.turtylib.common.container.slot;

import net.minecraftforge.items.IItemHandler;

public final class SlotNoPlace extends SlotWithRestriction {
    public SlotNoPlace(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition, stack -> false);
    }
}
