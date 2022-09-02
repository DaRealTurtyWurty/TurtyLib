package io.github.darealturtywurty.turtylib.core.init;

import io.github.darealturtywurty.turtylib.TurtyLib;
import io.github.darealturtywurty.turtylib.common.blockentity.MultiblockBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class BlockEntityInit extends AbstractInit {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(
            ForgeRegistries.BLOCK_ENTITY_TYPES, TurtyLib.MODID);

    public static final RegistryObject<BlockEntityType<MultiblockBlockEntity>> MULTIBLOCK = BLOCK_ENTITIES.register(
            "multiblock",
            () -> BlockEntityType.Builder.of(MultiblockBlockEntity::new, BlockInit.MULTIBLOCK.get()).build(null));
}
