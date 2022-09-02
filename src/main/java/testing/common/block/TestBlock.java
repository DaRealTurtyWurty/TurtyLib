package testing.common.block;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.NotNull;
import testing.client.screen.TestScreen;
import testing.common.blockentity.TestBlockEntity;
import io.github.darealturtywurty.turtylib.common.blockentity.TickableBlockEntity;

public class TestBlock extends Block implements EntityBlock {
    public TestBlock(Properties properties) {
        super(properties);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
        BlockEntityType<T> type) {
        return level.isClientSide() ? null : ($0, $1, $2, blockEntity) -> ((TickableBlockEntity)blockEntity).tick();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TestBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
        BlockHitResult result) {
        if (level.isClientSide() && level.getBlockEntity(pos) instanceof final TestBlockEntity be) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> Minecraft.getInstance().setScreen(new TestScreen(be, TestBlockEntity.TITLE)));
        }
        
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext ctx) {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.1875, 2, 0.8125, 0.3125, 3, 1.1875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-0.3125, 2, 1.1875, 0.3125, 3, 1.3125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-0.3125, 2, 0.6875, 0.3125, 3, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-0.3125, 2, 0.8125, -0.1875, 3, 1.1875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-1, 1.6875, 0, -0.1875, 2, 2), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-1, 0, 0, 1, 0.3125, 2), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-1, 0.3125, 0, -0.6875, 1.6875, 2), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.6875, 0.3125, 0, 1, 1.6875, 2), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-0.6875, 0.3125, 1.6875, 0.6875, 1.6875, 2), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-0.6875, 1.25, 0, 0.6875, 1.6875, 0.3125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-0.6875, 0.3125, 0, 0.6875, 0.6875, 0.3125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 1.6875, 0, 1, 2, 2), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-0.1875, 1.6875, 0, 0.1875, 2, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-0.1875, 1.6875, 1.1875, 0.1875, 2, 2), BooleanOp.OR);

        return shape;
    }
}
