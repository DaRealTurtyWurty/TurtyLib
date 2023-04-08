package dev.turtywurty.turtylib.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("resource")
public final class ClientUtils {
    private ClientUtils() {
        throw new IllegalStateException("Attempted to construct utility class!");
    }

    // TODO: Move to math utils
    public static Vector4f ARGBtoRGBA(int argb) {
        return new Vector4f((argb >> 16 & 0xFF) / 255f, (argb >> 8 & 0xFF) / 255f, (argb & 0xFF) / 255f,
            (argb >> 24 & 0xFF) / 255f);
    }
    
    public static int getAverageBlockColor(BlockState state, BlockPos pos) {
        final BlockRenderDispatcher dispatcher = ClientUtils.getMinecraft().getBlockRenderer();
        final TextureAtlasSprite texture = dispatcher.getBlockModelShaper().getTexture(state,
            ClientUtils.getMinecraft().level, pos);
        final int pixelCount = texture.getWidth() * texture.getHeight();
        int totalAlpha = 0, totalRed = 0, totalGreen = 0, totalBlue = 0;
        for (int x = 0; x < texture.getWidth(); x++) {
            for (int y = 0; y < texture.getHeight(); y++) {
                final int color = texture.getPixelRGBA(0, x, y);
                totalAlpha += color >> 24 & 0xFF;
                totalBlue += color >> 16 & 0xFF;
                totalGreen += color >> 8 & 0xFF;
                totalRed += color & 0xFF;
            }
        }
        
        final int alpha = totalAlpha / pixelCount;
        final int red = totalRed / pixelCount;
        final int green = totalGreen / pixelCount;
        final int blue = totalBlue / pixelCount;
        
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    public static TextureAtlasSprite getBlock(ResourceLocation texture) {
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture);
    }
    
    public static Font getFont() {
        return getMinecraft().font;
    }

    public static Minecraft getMinecraft() {
        return Minecraft.getInstance();
    }

    public static ResourceManager getResourceManager() {
        return getMinecraft().getResourceManager();
    }

    public static IClientFluidTypeExtensions getClientFluidExtensions(FluidStack fluid) {
        return getClientFluidExtensions(fluid.getFluid());
    }

    public static IClientFluidTypeExtensions getClientFluidExtensions(Fluid fluid) {
        return IClientFluidTypeExtensions.of(fluid);
    }

    public static void renderTintedModel(PoseStack.Pose pPose, VertexConsumer pConsumer, @Nullable BlockState pState, BakedModel pModel, float pRed, float pGreen, float pBlue, int pPackedLight, int pPackedOverlay, ModelData modelData, RenderType renderType) {
        renderTintedModel(pPose, pConsumer, pState, pModel, 1.0F, 1.0F, 1.0F, 1.0F, pRed, pGreen, pBlue, pPackedLight,
                pPackedOverlay, modelData, renderType);
    }

    public static void renderTintedModel(PoseStack.Pose pPose, VertexConsumer pConsumer, @Nullable BlockState pState, BakedModel pModel, float redMul, float greenMul, float blueMul, float alphaMul, float pRed, float pGreen, float pBlue, int pPackedLight, int pPackedOverlay, ModelData modelData, RenderType renderType) {
        var randomsource = RandomSource.create();
        for (Direction direction : Direction.values()) {
            randomsource.setSeed(42L);
            renderQuadList(pPose, pConsumer, redMul, greenMul, blueMul, alphaMul, pRed, pGreen, pBlue,
                    pModel.getQuads(pState, direction, randomsource, modelData, renderType), pPackedLight,
                    pPackedOverlay);
        }

        randomsource.setSeed(42L);
        renderQuadList(pPose, pConsumer, redMul, greenMul, blueMul, alphaMul, pRed, pGreen, pBlue,
                pModel.getQuads(pState, null, randomsource, modelData, renderType), pPackedLight, pPackedOverlay);
    }

    public static void renderQuadList(PoseStack.Pose pPose, VertexConsumer pConsumer, float redMul, float greenMul, float blueMul, float alphaMul, float pRed, float pGreen, float pBlue, List<BakedQuad> pQuads, int pPackedLight, int pPackedOverlay) {
        for (BakedQuad quad : pQuads) {
            float red;
            float green;
            float blue;
            if (quad.isTinted()) {
                red = Mth.clamp(pRed, 0.0F, 1.0F);
                green = Mth.clamp(pGreen, 0.0F, 1.0F);
                blue = Mth.clamp(pBlue, 0.0F, 1.0F);
            } else {
                red = 1.0F;
                green = 1.0F;
                blue = 1.0F;
            }

            pConsumer.putBulkData(pPose, quad, new float[]{redMul, greenMul, blueMul, alphaMul}, red, green, blue,
                    new int[]{pPackedLight, pPackedLight, pPackedLight, pPackedLight}, pPackedOverlay, true);
        }
    }
}
