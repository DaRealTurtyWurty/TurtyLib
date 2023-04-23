package dev.turtywurty.turtylib.core.multiblock;

import dev.turtywurty.turtylib.TurtyLib;
import dev.turtywurty.turtylib.common.blockentity.ModularBlockEntity;
import dev.turtywurty.turtylib.common.blockentity.module.MultiblockModule;
import dev.turtywurty.turtylib.core.init.BlockEntityInit;
import dev.turtywurty.turtylib.core.init.BlockInit;
import dev.turtywurty.turtylib.core.multiblock.modes.PlayerBuiltMultiblock;
import dev.turtywurty.turtylib.core.multiblock.modes.SelfBuildingMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Mod.EventBusSubscriber(modid = TurtyLib.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MultiblockListener {
    @SubscribeEvent
    public static void blockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof Player) || event.getLevel().isClientSide()) return;

        final LevelAccessor level = event.getLevel();
        final BlockPos position = event.getPos();
        final BlockState block = event.getPlacedBlock();
        for (PlayerBuiltMultiblock multiblock : TurtyLib.MULTIBLOCK_REGISTRY.get().getValues().stream()
                .filter(PlayerBuiltMultiblock.class::isInstance).map(PlayerBuiltMultiblock.class::cast)
                .filter(mblock -> !mblock.requiresActivation()).toList()) {
            if (!multiblock.isValid(block)) {
                continue;
            }

            long startTime = System.currentTimeMillis();
            BlockPattern.BlockPatternMatch match = find(multiblock.getPatternMatcher(), level, position);
            long endTime = System.currentTimeMillis();
            TurtyLib.LOGGER.info("Took {}ms to {}find a match", endTime - startTime, match == null ? "not " : "");

            if (match == null) continue;

            final Pair<Vec3i, BlockState> controller = multiblock.getController();
            final BlockPos controllerPosition = match.getBlock(controller.getKey().getX(), controller.getKey().getY(),
                    controller.getKey().getZ()).getPos();

            List<BlockPos> positions = new ArrayList<>();

            final int multiBlockWidth = multiblock.getPatternMatcher().getWidth();
            final int multiBlockHeight = multiblock.getPatternMatcher().getDepth();
            final int multiBlockDepth = multiblock.getPatternMatcher().getHeight();
            for (int x = 0; x < multiBlockWidth; x++) {
                for (int y = 0; y < multiBlockHeight; y++) {
                    for (int z = 0; z < multiBlockDepth; z++) {
                        BlockInWorld inWorld = match.getBlock(x, z, y);
                        BlockPos pos = inWorld.getPos();

                        if (!pos.equals(controllerPosition)) {
                            level.setBlock(pos, BlockInit.MULTIBLOCK.get().defaultBlockState(), Block.UPDATE_ALL);
                            level.getBlockEntity(pos, BlockEntityInit.MULTIBLOCK.get()).ifPresent(blockEntity -> {
                                blockEntity.setController(controllerPosition);
                                blockEntity.setPrevious(inWorld.getState());
                            });
                        }

                        positions.add(pos);
                    }
                }
            }

            BlockState currentControllerState = event.getLevel().getBlockState(controllerPosition);
            event.getLevel().setBlock(controllerPosition, controller.getValue(), Block.UPDATE_ALL);
            if (event.getLevel().getBlockEntity(controllerPosition) instanceof ModularBlockEntity modularBlockEntity) {
                MultiblockModule multiblockModule = modularBlockEntity.getModule(MultiblockModule.class).orElseThrow(
                        () -> new IllegalStateException("Controller does not container a multiblock module!"));


                multiblockModule.setPositions(positions);
                multiblockModule.setPrevious(currentControllerState);
            }

            break;
        }
    }

    @SubscribeEvent
    public static void itemUse(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos position = event.getPos();
        BlockState block = level.getBlockState(position);
        ItemStack stack = event.getItemStack();

        activationMultiblockValidation(level, position, block, stack);
        selfBuildingMultiblockValidation(level, position, block, stack);
    }

    private static @Nullable BlockPattern.BlockPatternMatch find(BlockPattern pattern, LevelAccessor level, BlockPos pos) {
        final int patternWidth = pattern.getWidth();
        final int patternDepth = pattern.getDepth();
        final int patternHeight = pattern.getHeight();

        for (int x = -patternWidth; x < patternWidth; x++) {
            for (int y = -patternDepth; y < patternDepth; y++) {
                for (int z = -patternHeight; z < patternHeight; z++) {
                    final BlockPos offset = pos.offset(x, y, z);
                    final BlockPattern.BlockPatternMatch found = pattern.find(level, offset);

                    if (found != null) return found;
                }
            }
        }

        return null;
    }

    private static void activationMultiblockValidation(Level level, BlockPos position, BlockState block, ItemStack stack) {
        // filter out multiblocks that either don't require an activation item or don't have a valid item
        List<PlayerBuiltMultiblock> multiblocks = TurtyLib.MULTIBLOCK_REGISTRY.get().getValues().stream()
                .filter(PlayerBuiltMultiblock.class::isInstance).map(PlayerBuiltMultiblock.class::cast)
                .filter(multiblock -> multiblock.requiresActivation() && multiblock.isValidItem(stack)).toList();

        if (multiblocks.isEmpty()) return;

        for (PlayerBuiltMultiblock multiblock : multiblocks) {
            if (!multiblock.isValid(block)) continue;

            long startTime = System.currentTimeMillis();
            BlockPattern.BlockPatternMatch match = find(multiblock.getPatternMatcher(), level, position);
            long endTime = System.currentTimeMillis();
            TurtyLib.LOGGER.info("Took {}ms to {}find a match", endTime - startTime, match == null ? "not " : "");

            if (match == null) continue;

            final Pair<Vec3i, BlockState> controller = multiblock.getController();
            final BlockPos controllerPosition = match.getBlock(controller.getKey().getX(), controller.getKey().getY(),
                    controller.getKey().getZ()).getPos();

            // if the multiblock has an activation position then validate that this is that position
            var isValid = new AtomicBoolean(true);
            multiblock.getActivationPosition().ifPresent(activationPosition -> {
                final BlockPos blockPos = match.getBlock(activationPosition.getX(), activationPosition.getY(),
                        activationPosition.getZ()).getPos();

                if (!blockPos.equals(position)) {
                    isValid.set(false);
                }
            });

            if (!isValid.get()) continue;

            List<BlockPos> positions = new ArrayList<>();

            final int multiBlockWidth = multiblock.getPatternMatcher().getWidth();
            final int multiBlockHeight = multiblock.getPatternMatcher().getDepth();
            final int multiBlockDepth = multiblock.getPatternMatcher().getHeight();
            for (int x = 0; x < multiBlockWidth; x++) {
                for (int y = 0; y < multiBlockHeight; y++) {
                    for (int z = 0; z < multiBlockDepth; z++) {
                        BlockInWorld inWorld = match.getBlock(x, z, y);
                        BlockPos pos = inWorld.getPos();

                        if (!pos.equals(controllerPosition)) {
                            level.setBlock(pos, BlockInit.MULTIBLOCK.get().defaultBlockState(), Block.UPDATE_ALL);
                            level.getBlockEntity(pos, BlockEntityInit.MULTIBLOCK.get()).ifPresent(blockEntity -> {
                                blockEntity.setController(controllerPosition);
                                blockEntity.setPrevious(inWorld.getState());
                            });
                        }

                        positions.add(pos);
                    }
                }
            }

            BlockState currentControllerState = level.getBlockState(controllerPosition);
            level.setBlock(controllerPosition, controller.getValue(), Block.UPDATE_ALL);
            if (level.getBlockEntity(controllerPosition) instanceof ModularBlockEntity modularBlockEntity) {
                MultiblockModule multiblockModule = modularBlockEntity.getModule(MultiblockModule.class).orElseThrow(
                        () -> new IllegalStateException("Controller does not container a multiblock module!"));

                multiblockModule.setPositions(positions);
                multiblockModule.setPrevious(currentControllerState);
            }

            break;
        }
    }

    private static void selfBuildingMultiblockValidation(Level level, BlockPos position, BlockState block, ItemStack stack) {
        // filter out multiblocks that are not SelfBuildingMultiblocks or don't have a valid item
        List<SelfBuildingMultiblock> multiblocks = TurtyLib.MULTIBLOCK_REGISTRY.get().getValues().stream()
                .filter(SelfBuildingMultiblock.class::isInstance).map(SelfBuildingMultiblock.class::cast)
                .filter(multiblock -> multiblock.isValidItem(stack)).toList();

        if (multiblocks.isEmpty()) return;

        for (SelfBuildingMultiblock multiblock : multiblocks) {
            if (!multiblock.getBlockStatePredicate().test(block)) continue;


        }
    }
}