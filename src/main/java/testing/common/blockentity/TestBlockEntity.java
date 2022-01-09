package testing.common.blockentity;

import io.github.darealturtywurty.turtylib.common.blockentity.ModularBlockEntity;
import io.github.darealturtywurty.turtylib.common.blockentity.module.InventoryModule;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import testing.core.init.BlockEntityInit;

public class TestBlockEntity extends ModularBlockEntity {
    public final InventoryModule inventory;

    public TestBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.TEST.get(), pos, state);
        this.inventory = addModule(new InventoryModule(this, 2));
    }
    
}
