package dev.turtywurty.turtylib.core.multiblock.modes;

import dev.turtywurty.turtylib.core.multiblock.Multiblock;
import dev.turtywurty.turtylib.core.multiblock.UseFunction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Supplier;

public class SelfBuildingMultiblock extends Multiblock {
    private final Pair<Vec3i, BlockState> controller;
    private final Supplier<Item> placeItem;
    private final Supplier<BlockStatePredicate> blockStatePredicate;
    private final List<BlockPatternWrapper> patterns = new ArrayList<>();

    protected SelfBuildingMultiblock(Builder builder) {
        super(builder.useFunction);
        this.controller = builder.controller;
        this.placeItem = builder.placeItem;
        this.blockStatePredicate = builder.blockStatePredicate;
        this.patterns.addAll(builder.patterns);
    }

    public Pair<Vec3i, BlockState> getController() {
        return this.controller;
    }

    public BlockStatePredicate getBlockStatePredicate() {
        return this.blockStatePredicate.get();
    }

    public Item getPlaceItem() {
        return this.placeItem.get();
    }

    public boolean isValidItem(ItemStack stack) {
        return this.placeItem.get().equals(stack.getItem());
    }

    public static class Builder {
        private UseFunction useFunction = ($, $0, $1, $2, $3, $4, $5) -> InteractionResult.PASS;
        private Pair<Vec3i, BlockState> controller = Pair.of(Vec3i.ZERO, Blocks.AIR.defaultBlockState());
        private Supplier<Item> placeItem = () -> Items.AIR;
        private Supplier<BlockStatePredicate> blockStatePredicate = () -> (BlockStatePredicate) BlockStatePredicate.ANY;
        private final List<BlockPatternWrapper> patterns = new ArrayList<>();

        public Builder useFunction(UseFunction useFunction) {
            this.useFunction = useFunction;
            return this;
        }

        public Builder controller(Vec3i pos, BlockState state) {
            this.controller = Pair.of(pos, state);
            return this;
        }

        public Builder placeItem(Supplier<Item> placeItem) {
            this.placeItem = placeItem;
            return this;
        }

        public Builder blockStatePredicate(Supplier<BlockStatePredicate> blockStatePredicate) {
            this.blockStatePredicate = blockStatePredicate;
            return this;
        }

        public final class Pattern {
            private Pattern() {
            }

            private final BlockPatternBuilder pattern = BlockPatternBuilder.start();
            private final Map<Character, BlockState> states = new HashMap<>();

            public Builder.Pattern where(char key, BlockState state) {
                this.states.put(key, state);
                this.pattern.where(key, BlockInWorld.hasState(s -> s.equals(state)));
                return this;
            }

            public Builder.Pattern aisle(String... aisles) {
                this.pattern.aisle(aisles);
                return this;
            }

            public Builder finish() {
                Builder.this.patterns.add(new BlockPatternWrapper(this.pattern.build(), this.states));
                return Builder.this;
            }
        }

        private Builder.Pattern pattern() {
            return new Builder.Pattern();
        }

        public static Builder.Pattern start() {
            var builder = new Builder();
            return builder.pattern();
        }

        public SelfBuildingMultiblock build() {
            return new SelfBuildingMultiblock(this);
        }
    }

    public record BlockPatternWrapper(BlockPattern pattern, Map<Character, BlockState> states) {
    }
}
