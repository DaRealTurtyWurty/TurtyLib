package io.github.darealturtywurty.turtylib.common.blocks;

import io.github.darealturtywurty.turtylib.common.blockentity.MultiblockBlockEntity;
import io.github.darealturtywurty.turtylib.core.init.BlockEntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
        return RenderShape.INVISIBLE;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext ctx) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof MultiblockBlockEntity multiblock) || multiblock.getController() == null)
            return Shapes.empty();

        BlockEntity controller = level.getBlockEntity(multiblock.getController());
        if (controller == null)
            return Shapes.empty();

        return controller.getBlockState().getShape(level, pos, ctx).move(multiblock.getController().getX() - pos.getX(),
                multiblock.getController().getY() - pos.getY(), multiblock.getController().getZ() - pos.getZ());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return BlockEntityInit.MULTIBLOCK.get().create(pos, state);
    }
}
