package testing.common.block;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import testing.client.screen.TestScreen;
import testing.common.blockentity.TestBlockEntity;

public class TestBlock extends Block implements EntityBlock {
    public TestBlock(Properties properties) {
        super(properties);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
        BlockEntityType<T> type) {
        return ($0, $1, $2, be) -> {
            if (!level.isClientSide()) {
                ((TestBlockEntity) be).tick();
            }
        };
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TestBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
        BlockHitResult result) {
        if (level.isClientSide && level.getBlockEntity(pos) instanceof final TestBlockEntity be) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> Minecraft.getInstance().setScreen(new TestScreen(be, TestBlockEntity.TITLE)));
        }
        
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
