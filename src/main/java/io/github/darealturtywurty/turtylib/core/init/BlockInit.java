package io.github.darealturtywurty.turtylib.core.init;

import io.github.darealturtywurty.turtylib.TurtyLib;
import io.github.darealturtywurty.turtylib.common.blocks.MultiblockBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class BlockInit extends AbstractInit {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TurtyLib.MODID);

    public static final RegistryObject<MultiblockBlock> MULTIBLOCK = BLOCKS.register("multiblock",
            () -> new MultiblockBlock(BlockBehaviour.Properties.of(Material.BARRIER).noLootTable().noOcclusion().dynamicShape()));
}
