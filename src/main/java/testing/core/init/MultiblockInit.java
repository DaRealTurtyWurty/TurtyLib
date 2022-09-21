package testing.core.init;

import io.github.darealturtywurty.turtylib.TurtyLib;
import io.github.darealturtywurty.turtylib.core.init.AbstractInit;
import io.github.darealturtywurty.turtylib.core.multiblock.Multiblock;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import testing.TestMod;
import testing.client.screen.TestScreen;
import testing.common.blockentity.TestBlockEntity;

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
                            .useFunction((state, level, pos, player, hand, result, offsetFromMaster) -> {
                                if (level.isClientSide() && level.getBlockEntity(pos) instanceof final TestBlockEntity be) {
                                    DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                                            () -> () -> Minecraft.getInstance().setScreen(new TestScreen(be, TestBlockEntity.TITLE)));
                                }

                                return InteractionResult.sidedSuccess(level.isClientSide());
                            })
            ));
}
