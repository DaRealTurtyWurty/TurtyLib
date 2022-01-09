package testing.core.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import testing.TestMod;
import testing.common.block.TestBlock;

public final class BlockInit {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TestMod.MODID);
    
    public static final RegistryObject<Block> TEST = BLOCKS.register("test",
            () -> new TestBlock(BlockBehaviour.Properties.of(Material.WOOD)));
    
    private BlockInit() {
    }
}
