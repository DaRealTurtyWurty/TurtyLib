package testing.core.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import testing.TestMod;
import testing.common.blockentity.TestBlockEntity;

public final class BlockEntityInit {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister
            .create(ForgeRegistries.BLOCK_ENTITIES, TestMod.MODID);

    public static final RegistryObject<BlockEntityType<TestBlockEntity>> TEST = BLOCK_ENTITIES.register("test",
            () -> BlockEntityType.Builder.of(TestBlockEntity::new, BlockInit.TEST.get()).build(null));

    private BlockEntityInit() {
    }
}
