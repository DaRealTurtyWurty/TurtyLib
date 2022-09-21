package io.github.darealturtywurty.turtylib.mixins;

import io.github.darealturtywurty.turtylib.common.blockentity.ModularBlockEntity;
import io.github.darealturtywurty.turtylib.common.blockentity.module.MultiblockModule;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.class)
public class BlockMixin {
    @Inject(method = "onRemove", at = @At("TAIL"))
    private void turtylib$onRemove(BlockState state, Level level, BlockPos pos, BlockState newState,
            boolean isMoving, CallbackInfo callback) {
        if (level.isClientSide)
            return;

        if(state.is(newState.getBlock()))
            return;

        final BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ModularBlockEntity modularBlockEntity) {
            MultiblockModule multiblock = modularBlockEntity.getModule(MultiblockModule.class).orElse(null);
            if(multiblock == null)
                return;

            if (multiblock.isForRemoval()) {
                return;
            }

            multiblock.setForRemoval(true);
            multiblock.removeMultiblock(blockEntity.getLevel(), blockEntity.getBlockPos());
        }
    }
}
