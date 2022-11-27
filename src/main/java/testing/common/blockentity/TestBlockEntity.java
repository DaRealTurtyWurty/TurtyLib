package testing.common.blockentity;

import io.github.darealturtywurty.turtylib.common.blockentity.ModularBlockEntity;
import io.github.darealturtywurty.turtylib.common.blockentity.module.EnergyModule;
import io.github.darealturtywurty.turtylib.common.blockentity.module.FluidModule;
import io.github.darealturtywurty.turtylib.common.blockentity.module.InventoryModule;
import io.github.darealturtywurty.turtylib.common.blockentity.module.MultiblockModule;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import testing.TestMod;
import testing.core.init.BlockEntityInit;
import testing.core.init.MultiblockInit;

public class TestBlockEntity extends ModularBlockEntity {
    public static final Component TITLE = Component.translatable(TestMod.MODID + ".test.gui");
    public final InventoryModule inventory;
    public final FluidModule fluidTank;
    public final MultiblockModule multiblock;
    public final EnergyModule energy;

    public TestBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.TEST.get(), pos, state);
        this.inventory = addModule(new InventoryModule(this, 2));
        this.fluidTank = addModule(new FluidModule(this, 10000));
        this.multiblock = addModule(new MultiblockModule(MultiblockInit.TEST));
        this.energy = addModule(
                new EnergyModule(this, new EnergyModule.Builder().capacity(10000).maxReceive(1000).maxExtract(1000)));
    }
}