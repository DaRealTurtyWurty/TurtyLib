package testing.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import testing.common.blockentity.TestBlockEntity;

public class Renderer implements BlockEntityRenderer<TestBlockEntity> {
    private final BlockEntityRendererProvider.Context context;

    public Renderer(BlockEntityRendererProvider.Context ctx) {
        this.context = ctx;
    }

    @Override
    public void render(@NotNull TestBlockEntity blockEntity, float partialTicks, @NotNull PoseStack stack, @NotNull MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {
        blockEntity.multiblock.getPositions().forEach(pos -> {
            if(!pos.equals(blockEntity.getBlockPos())) {
                stack.pushPose();

                int x = pos.getX() - blockEntity.getBlockPos().getX();
                int y = pos.getY() - blockEntity.getBlockPos().getY();
                int z = pos.getZ() - blockEntity.getBlockPos().getZ();
                stack.translate(x, y, z);

                this.context.getBlockRenderDispatcher().renderSingleBlock(
                        Blocks.PINK_STAINED_GLASS.defaultBlockState(),
                        stack,
                        buffer,
                        packedLight,
                        packedOverlay,
                        ModelData.EMPTY,
                        RenderType.translucent()
                );

                stack.popPose();
            }
        });
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TestBlockEntity blockEntity) {
        return true;
    }
}
