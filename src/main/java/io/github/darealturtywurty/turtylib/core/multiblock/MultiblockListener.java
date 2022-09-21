package io.github.darealturtywurty.turtylib.core.multiblock;

import io.github.darealturtywurty.turtylib.TurtyLib;
import io.github.darealturtywurty.turtylib.common.blockentity.ModularBlockEntity;
import io.github.darealturtywurty.turtylib.common.blockentity.module.MultiblockModule;
import io.github.darealturtywurty.turtylib.core.init.BlockEntityInit;
import io.github.darealturtywurty.turtylib.core.init.BlockInit;
import io.github.darealturtywurty.turtylib.core.network.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = TurtyLib.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MultiblockListener {
    @SubscribeEvent
    public static void blockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof Player) || event.getLevel().isClientSide())
            return;

        final LevelAccessor level = event.getLevel();
        final BlockPos position = event.getPos();
        final BlockState block = event.getPlacedBlock();
        for (Multiblock multiblock : TurtyLib.MULTIBLOCK_REGISTRY.get()) {
            if (!multiblock.isValid(block)) {
                continue;
            }

            final BlockPattern.BlockPatternMatch match = find(
                    multiblock.getPatternMatcher(),
                    level,
                    position
            );

            if (match == null)
                continue;

            final Pair<Vec3i, BlockState> controller = multiblock.getController();
            final BlockPos controllerPosition = match.getBlock(controller.getKey().getX(), controller.getKey().getY(),
                    controller.getKey().getZ()).getPos();

            List<BlockPos> positions = new ArrayList<>();

            // Cache the width, height, and depth
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
                            level.getBlockEntity(pos, BlockEntityInit.MULTIBLOCK.get())
                                    .ifPresent(blockEntity -> {
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

    // TODO: Make this more efficient
    private static @Nullable BlockPattern.BlockPatternMatch find(BlockPattern pattern, LevelAccessor level, BlockPos pos) {
        // Cache the width, height, and depth
        final int patternWidth = pattern.getWidth();
        final int patternDepth = pattern.getDepth();
        final int patternHeight = pattern.getHeight();

        for (int x = -patternWidth; x < patternWidth; x++) {
            for (int y = -patternDepth; y < patternDepth; y++) {
                for (int z = -patternHeight; z < patternHeight; z++) {
                    final BlockPos offset = pos.offset(x, y, z);
                    final BlockPattern.BlockPatternMatch found = pattern.find(level, offset);

                    if (found != null)
                        return found;
                }
            }
        }

        return null;
    }


}
