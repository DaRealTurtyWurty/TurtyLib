package io.github.darealturtywurty.turtylib.common.blocks;

import io.github.darealturtywurty.turtylib.common.blockentity.ModularBlockEntity;
import io.github.darealturtywurty.turtylib.common.blockentity.MultiblockBlockEntity;
import io.github.darealturtywurty.turtylib.common.blockentity.module.MultiblockModule;
import io.github.darealturtywurty.turtylib.core.init.BlockEntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MultiblockBlock extends Block implements EntityBlock {
    public MultiblockBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos,
            @NotNull CollisionContext ctx) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof MultiblockBlockEntity multiblock) || multiblock.getController() == null)
            return Shapes.empty();

        BlockEntity controller = level.getBlockEntity(multiblock.getController());
        if (controller == null) return Shapes.empty();

        return controller.getBlockState().getShape(level, pos, ctx)
                .move(multiblock.getController().getX() - pos.getX(), multiblock.getController().getY() - pos.getY(),
                        multiblock.getController().getZ() - pos.getZ());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return BlockEntityInit.MULTIBLOCK.get().create(pos, state);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos,
            @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        MultiblockBlockEntity blockEntity = level.getBlockEntity(pos, BlockEntityInit.MULTIBLOCK.get()).orElse(null);
        if (blockEntity == null) return super.use(state, level, pos, player, hand, hitResult);

        if (blockEntity.getController() == null) return super.use(state, level, pos, player, hand, hitResult);

        BlockEntity controller = level.getBlockEntity(blockEntity.getController());
        if (controller == null) return super.use(state, level, pos, player, hand, hitResult);

        if (controller instanceof ModularBlockEntity modular) {
            MultiblockModule multiblock = modular.getModule(MultiblockModule.class).orElse(null);
            if (multiblock == null) return super.use(state, level, pos, player, hand, hitResult);

            return multiblock.getMultiblock().getUseFunction()
                    .use(state, level, blockEntity.getController(), player, hand, hitResult, blockEntity.getController().subtract(pos));
        }

        return super.use(state, level, pos, player, hand, hitResult);
    }
}
