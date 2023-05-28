package dev.turtywurty.turtylib.core.multiblock.modes;

import com.google.common.collect.Sets;
import dev.turtywurty.turtylib.core.multiblock.Multiblock;
import dev.turtywurty.turtylib.core.multiblock.UseFunction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Predicate;

public class PlayerBuiltMultiblock extends Multiblock {
    private final BlockPattern patternMatcher;
    private final List<Predicate<BlockState>> validStates;
    private final Set<Vec3i> ignoredPositions;
    private final Pair<Vec3i, BlockState> controller;
    private final boolean requiresActivation;
    private final Optional<Ingredient> activationItem;
    private final Optional<Vec3i> activationPosition;

    protected PlayerBuiltMultiblock(Builder builder) {
        super(builder.useFunction);
        this.patternMatcher = builder.pattern;
        this.validStates = builder.validStates.stream().toList();
        this.ignoredPositions = builder.ignorePositions;
        this.controller = builder.controller;
        this.requiresActivation = builder.activationItem.isPresent() || builder.activationPosition.isPresent();
        this.activationItem = builder.activationItem;
        this.activationPosition = builder.activationPosition;
    }

    public List<Predicate<BlockState>> getValidStates() {
        return validStates;
    }

    public BlockPattern getPatternMatcher() {
        return this.patternMatcher;
    }

    public boolean isValid(BlockState state) {
        return this.validStates.stream().anyMatch(it -> it.test(state));
    }

    public Pair<Vec3i, BlockState> getController() {
        return this.controller;
    }

    public boolean requiresActivation() {
        return this.requiresActivation;
    }

    public Optional<Ingredient> getActivationItem() {
        return this.activationItem;
    }

    public Optional<Vec3i> getActivationPosition() {
        return this.activationPosition;
    }

    public boolean isValidItem(ItemStack stack) {
        return this.activationItem.map(it -> it.test(stack)).orElse(true);
    }

    public boolean isIgnoredPosition(Vec3i pos) {
        return this.ignoredPositions.contains(pos);
    }

    public static class Builder {
        private BlockPattern pattern;
        private final Set<Predicate<BlockState>> validStates = new HashSet<>();
        private final Set<Vec3i> ignorePositions = new HashSet<>();
        private Pair<Vec3i, BlockState> controller;
        private UseFunction useFunction = ($0, $1, $2, $3, $5, $6, $7) -> InteractionResult.PASS;
        private Optional<Ingredient> activationItem = Optional.empty();
        private Optional<Vec3i> activationPosition = Optional.empty();

        public final class Pattern {
            private final BlockPatternBuilder pattern = BlockPatternBuilder.start();
            private final Set<Predicate<BlockState>> validStates = new HashSet<>();
            private final Map<Vec3i, Character> charPosMap = new HashMap<>();
            private final Set<Character> ignoreKeys = Sets.newHashSet(' ', '.');

            private Pattern() {
                this.pattern.where(' ', BlockInWorld.hasState(state -> true));
                this.pattern.where('.', BlockInWorld.hasState(state -> true));
            }

            public Pattern where(char key, Predicate<BlockState> state) {
                this.validStates.add(state);
                this.pattern.where(key, BlockInWorld.hasState(state));
                return this;
            }

            public Pattern aisle(String... aisles) {
                this.pattern.aisle(aisles);

                for (int y = 0; y < aisles.length; y++) {
                    String aisle = aisles[y];
                    for (int z = 0; z < aisle.length(); z++) {
                        char key = aisle.charAt(z);
                        for (int x = 0; x < aisles.length; x++) {
                            Vec3i pos = new Vec3i(x, y, z);
                            if (ignoreKeys.contains(key)) {
                                Builder.this.ignorePositions.add(pos);
                            }
                        }
                    }
                }

                return this;
            }

            public Pattern ignore(Character... keys) {
                Collections.addAll(this.ignoreKeys, keys);
                for (Character key : keys) {
                    this.pattern.where(key, BlockInWorld.hasState(state -> true));
                }

                return this;
            }

            public Builder finish() {
                Builder.this.pattern = pattern.build();
                Builder.this.validStates.addAll(this.validStates);
                return Builder.this;
            }
        }

        private Pattern pattern() {
            return new Pattern();
        }

        public static Pattern start() {
            var builder = new Builder();
            return builder.pattern();
        }

        public final Builder controller(int x, int y, int z, BlockState state) {
            if (x < 0 || x > this.pattern.getWidth()) throw new IndexOutOfBoundsException(
                    "'x' is out of the range of this multiblock. The width is: " + this.pattern.getWidth());

            if (y < 0 || y > this.pattern.getHeight()) throw new IndexOutOfBoundsException(
                    "'y' is out of the range of this multiblock. The height is: " + this.pattern.getHeight());

            if (z < 0 || z > this.pattern.getDepth()) throw new IndexOutOfBoundsException(
                    "'z' is out of the range of this multiblock. The depth is: " + this.pattern.getDepth());

            this.controller = Pair.of(new Vec3i(x, y, z), state);
            return this;
        }

        public Builder useFunction(UseFunction useFunction) {
            this.useFunction = useFunction;
            return this;
        }

        public Builder activationItem(Ingredient item) {
            this.activationItem = Optional.of(item);
            return this;
        }

        public Builder activationPosition(Vec3i position) {
            this.activationPosition = Optional.of(position);
            return this;
        }

        public PlayerBuiltMultiblock build() {
            return new PlayerBuiltMultiblock(this);
        }
    }
}
