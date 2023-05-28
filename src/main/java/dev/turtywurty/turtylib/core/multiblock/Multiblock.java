package dev.turtywurty.turtylib.core.multiblock;

import net.minecraft.world.InteractionResult;

public abstract class Multiblock {
    private final UseFunction useFunction;

    public Multiblock(Builder builder) {
        this.useFunction = builder.useFunction;
    }

    protected Multiblock(UseFunction useFunction) {
        this.useFunction = useFunction;
    }

    public UseFunction getUseFunction() {
        return this.useFunction;
    }

    public static class Builder {
        private UseFunction useFunction = ($0, $1, $2, $3, $5, $6, $7) -> InteractionResult.PASS;

        public Builder useFunction(UseFunction useFunction) {
            this.useFunction = useFunction;
            return this;
        }
    }
}
