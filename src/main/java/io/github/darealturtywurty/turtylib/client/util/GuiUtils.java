package io.github.darealturtywurty.turtylib.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Matrix4f;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

public final class GuiUtils {
    private GuiUtils() {
        throw new IllegalStateException("Attempted to construct utility class!");
    }
    
    public static void drawCenteredString(PoseStack stack, Component text, int xPos, int yPos, int colour) {
        final int textWidth = ClientUtils.getFont().width(text);
        ClientUtils.getFont().draw(stack, text, xPos - textWidth / 2, yPos, colour);
    }
    
    public static void drawLine(double startX, double startY, double endX, double endY, int red, int green, int blue,
            int alpha, float lineWidth) {
        RenderSystem.assertOnRenderThread();
        RenderSystem.disableTexture();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
        final var tesselator = RenderSystem.renderThreadTesselator();
        final BufferBuilder buffer = tesselator.getBuilder();
        RenderSystem.lineWidth(lineWidth);
        buffer.begin(Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
        buffer.vertex(startX, startY, 0).color(red, green, blue, alpha).normal(1.0F, 0.0F, 0.0F).endVertex();
        buffer.vertex(endX, endY, 0).color(red, green, blue, alpha).normal(1.0F, 0.0F, 0.0F).endVertex();
        tesselator.end();
        RenderSystem.lineWidth(1.0F);
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
    }
    
    public static void drawQuadSplitTexture(PoseStack stack, int x, int y, int width, int height, int imageWidth,
            int imageHeight) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, Resources.TAB_BACKGROUND);
        GuiComponent.blit(stack, x, y, 0, 0, width / 2, height / 2, imageWidth, imageHeight);
        GuiComponent.blit(stack, x + width / 2, y, imageWidth - width / 2, 0, width / 2, height / 2, imageWidth,
                imageHeight);
        GuiComponent.blit(stack, x, y + height / 2, 0, imageHeight - height / 2, width / 2, height / 2, imageWidth,
                imageHeight);
        GuiComponent.blit(stack, x + width / 2, y + height / 2, imageWidth - width / 2, imageHeight - height / 2,
                width / 2, height / 2, imageWidth, imageHeight);
    }
    
    public static void drawWordWrap(PoseStack stack, FormattedText text, float x, float y, int length, int color) {
        final Matrix4f matrix4f = stack.last().pose();

        for (final FormattedCharSequence line : ClientUtils.getFont().split(text, length)) {
            ClientUtils.getFont().drawInternal(line, x, y, color, matrix4f, false);
            y += 9;
        }
    }
    
    public static boolean isMouseInArea(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }
}
