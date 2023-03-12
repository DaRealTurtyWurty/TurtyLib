package io.github.darealturtywurty.turtylib.client.util;

import com.mojang.math.Vector4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

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
}
