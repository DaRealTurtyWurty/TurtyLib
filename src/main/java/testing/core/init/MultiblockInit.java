package testing.core.init;

import io.github.darealturtywurty.turtylib.TurtyLib;
import io.github.darealturtywurty.turtylib.core.init.AbstractInit;
import io.github.darealturtywurty.turtylib.core.multiblock.Multiblock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import testing.TestMod;

public final class MultiblockInit extends AbstractInit {
    public static final DeferredRegister<Multiblock> MULTIBLOCKS = DeferredRegister.create(TurtyLib.MULTIBLOCK_REGISTRY_KEY, TestMod.MODID);

    public static final RegistryObject<Multiblock> TEST = MULTIBLOCKS.register("test",
            () -> new Multiblock(
                    Multiblock.Builder.start()
                            .aisle("AB", "BA").aisle("AB", "BA").aisle("AB", "BA")
                            .where('A', BlockStatePredicate.forBlock(Blocks.BIRCH_STAIRS))
                            .where('B', BlockStatePredicate.forBlock(Blocks.BEACON))
                            .finish()
                            .controller(0, 0,0, BlockInit.TEST.get().defaultBlockState())
            ));
}
