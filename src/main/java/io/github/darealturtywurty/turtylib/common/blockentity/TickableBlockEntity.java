package io.github.darealturtywurty.turtylib.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TickableBlockEntity extends BlockEntity implements BlockEntityTicker<TickableBlockEntity> {
    protected TickableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void tick() {
        
    }
    
    @Override
    public final void tick(Level level, BlockPos pos, BlockState state, TickableBlockEntity blockEntity) {
        blockEntity.tick();
    }

    public void update() {
        requestModelDataUpdate();
        this.setChanged();
        if (this.level != null) {
            this.level.setBlockAndUpdate(this.worldPosition, getBlockState());
        }
    }
}
