package dev.turtywurty.turtylib.core.init;

import dev.turtywurty.turtylib.common.blockentity.MultiblockBlockEntity;
import dev.turtywurty.turtylib.TurtyLib;
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
