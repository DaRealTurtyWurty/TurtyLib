package dev.turtywurty.turtylib.client.ui.components;

import com.mojang.blaze3d.vertex.*;
import dev.turtywurty.turtylib.client.util.ClientUtils;
import dev.turtywurty.turtylib.core.util.MathUtils;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class WorldPreviewWidget extends AbstractWidget {
    private final ResourceLocation textureLoc;
    private final DynamicTexture texture;
    private final int imageWidth, imageHeight;
    private final RenderType renderType;

    public WorldPreviewWidget(int x, int y, int width, int height, int blockWidth, int blockHeight) {
        super(x, y, width, height, Component.empty());
        this.imageWidth = blockWidth;
        this.imageHeight = blockHeight;
        this.texture = new DynamicTexture(this.imageWidth, this.imageHeight, true);
        this.textureLoc = ClientUtils.getMinecraft().getTextureManager().register("world_preview/1", this.texture);
        this.renderType = RenderType.text(this.textureLoc);
    }

    @Override
    public void renderWidget(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) return;

        this.isHovered = MathUtils.isWithinArea(mouseX, mouseY, getX(), getY(), getWidth(), getHeight());

        final Matrix4f matrix = stack.last().pose();
        final BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
        buffer.vertex(matrix, 0.0F, this.imageHeight, 0F).color(255, 255, 255, 255).uv(0.0F, 1.0F).uv2(15728880).endVertex();
        buffer.vertex(matrix, this.imageWidth, this.imageHeight, 0F).color(255, 255, 255, 255).uv(1.0F, 1.0F).uv2(15728880).endVertex();
        buffer.vertex(matrix, this.imageWidth, 0.0F, 0F).color(255, 255, 255, 255).uv(1.0F, 0.0F).uv2(15728880).endVertex();
        buffer.vertex(matrix, 0.0F, 0.0F, 0F).color(255, 255, 255, 255).uv(0.0F, 0.0F).uv2(15728880).endVertex();
        BufferUploader.drawWithShader(buffer.end());
    }

    public void setPixels(int x, int y, int rgba) {
        this.texture.getPixels().setPixelRGBA(x, y, rgba);
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput narration) {
        defaultButtonNarrationText(narration);
    }

    public void upload() {
        this.texture.upload();
    }
}
