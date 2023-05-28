package dev.turtywurty.turtylib.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;

public abstract class Gradient {
    protected abstract int getTopLeftColor();

    protected abstract int getTopRightColor();

    protected abstract int getBottomLeftColor();

    protected abstract int getBottomRightColor();

    private static final class Gradient2 extends Gradient {
        private final int startColor, endColor;

        private Gradient2(int startColor, int endColor) {
            this.startColor = startColor;
            this.endColor = endColor;
        }

        @Override
        protected int getTopLeftColor() {
            return this.startColor;
        }

        @Override
        protected int getTopRightColor() {
            return this.startColor;
        }

        @Override
        protected int getBottomLeftColor() {
            return this.endColor;
        }

        @Override
        protected int getBottomRightColor() {
            return this.endColor;
        }
    }

    private static final class Gradient4 extends Gradient {
        private final int topLeftColor, topRightColor, bottomLeftColor, bottomRightColor;

        private Gradient4(int topLeftColor, int topRightColor, int bottomLeftColor, int bottomRightColor) {
            this.topLeftColor = topLeftColor;
            this.topRightColor = topRightColor;
            this.bottomLeftColor = bottomLeftColor;
            this.bottomRightColor = bottomRightColor;
        }

        @Override
        protected int getTopLeftColor() {
            return this.topLeftColor;
        }

        @Override
        protected int getTopRightColor() {
            return this.topRightColor;
        }

        @Override
        protected int getBottomLeftColor() {
            return this.bottomLeftColor;
        }

        @Override
        protected int getBottomRightColor() {
            return this.bottomRightColor;
        }
    }

    public static Gradient of(int startColor, int endColor) {
        return new Gradient2(startColor, endColor);
    }

    public static Gradient of(int topLeftColor, int topRightColor, int bottomLeftColor, int bottomRightColor) {
        return new Gradient4(topLeftColor, topRightColor, bottomLeftColor, bottomRightColor);
    }

    public void draw(PoseStack poseStack, int x0, int y0, int x1, int x2) {
        draw(poseStack.last().pose(), x0, y0, x1, x2, getTopLeftColor(), getTopRightColor(), getBottomLeftColor(),
                getBottomRightColor());
    }

    private static void draw(Matrix4f matrix4f, int x0, int y0, int x1, int y1, int topLeftColor, int topRightColor, int bottomLeftColor, int bottomRightColor) {
        // Setup
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        // Draw
        float f = (float) (topLeftColor >> 24 & 255) / 255.0F;
        float f1 = (float) (topLeftColor >> 16 & 255) / 255.0F;
        float f2 = (float) (topLeftColor >> 8 & 255) / 255.0F;
        float f3 = (float) (topLeftColor & 255) / 255.0F;
        float f4 = (float) (topRightColor >> 24 & 255) / 255.0F;
        float f5 = (float) (topRightColor >> 16 & 255) / 255.0F;
        float f6 = (float) (topRightColor >> 8 & 255) / 255.0F;
        float f7 = (float) (topRightColor & 255) / 255.0F;
        float f8 = (float) (bottomLeftColor >> 24 & 255) / 255.0F;
        float f9 = (float) (bottomLeftColor >> 16 & 255) / 255.0F;
        float f10 = (float) (bottomLeftColor >> 8 & 255) / 255.0F;
        float f11 = (float) (bottomLeftColor & 255) / 255.0F;
        float f12 = (float) (bottomRightColor >> 24 & 255) / 255.0F;
        float f13 = (float) (bottomRightColor >> 16 & 255) / 255.0F;
        float f14 = (float) (bottomRightColor >> 8 & 255) / 255.0F;
        float f15 = (float) (bottomRightColor & 255) / 255.0F;
        bufferBuilder.vertex(matrix4f, (float) x1, (float) y0, 0).color(f5, f6, f7, f4).endVertex();
        bufferBuilder.vertex(matrix4f, (float) x0, (float) y0, 0).color(f1, f2, f3, f).endVertex();
        bufferBuilder.vertex(matrix4f, (float) x0, (float) y1, 0).color(f9, f10, f11, f8).endVertex();
        bufferBuilder.vertex(matrix4f, (float) x1, (float) y1, 0).color(f13, f14, f15, f12).endVertex();

        // Cleanup
        tesselator.end();
        RenderSystem.disableBlend();
    }
}