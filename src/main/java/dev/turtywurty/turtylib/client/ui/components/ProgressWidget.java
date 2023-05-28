package dev.turtywurty.turtylib.client.ui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.turtywurty.turtylib.core.util.MathUtils;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.IntSupplier;

public class ProgressWidget extends AbstractWidget {
    private final int frames;
    private final ResourceLocation texture;
    private final int textureX, textureY;
    private final int fillX, fillY, fillWidth, fillHeight, fillColor;
    private final IntSupplier progressSupplier, maxProgressSupplier;

    private int frame;

    public ProgressWidget(int pX, int pY, int pWidth, int pHeight, int frames, ResourceLocation texture, int textureX, int textureY, int fillX, int fillY, int fillWidth, int fillHeight, int fillColor, IntSupplier progressSupplier, IntSupplier maxProgressSupplier) {
        super(pX, pY, pWidth, pHeight, Component.empty());
        this.frames = frames;
        this.texture = texture;
        this.textureX = textureX;
        this.textureY = textureY;

        this.fillX = fillX;
        this.fillY = fillY;
        this.fillWidth = fillWidth;
        this.fillHeight = fillHeight;
        this.fillColor = fillColor;

        this.progressSupplier = progressSupplier;
        this.maxProgressSupplier = maxProgressSupplier;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
        defaultButtonNarrationText(pNarrationElementOutput);
    }

    @Override
    public void renderWidget(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (!this.visible) return;

        this.isHovered = MathUtils.isWithinArea(pMouseX, pMouseY, getX(), getY(), getWidth(), getHeight());

        int progress = this.progressSupplier.getAsInt();
        int maxProgress = this.maxProgressSupplier.getAsInt();

        this.frame = (progress > 0) ? (this.frame + 1) % this.frames : 0;

        // Render the background
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, this.texture);
        blit(pPoseStack, getX(), getY(), this.textureX, this.textureY + (this.frame * getWidth()), getWidth(), getHeight());

        // Render progress bar
        int fillWidth = (int) ((float) progress / (float) maxProgress * this.fillWidth);
        fill(pPoseStack, this.fillX, this.fillY, this.fillX + fillWidth, this.fillY + this.fillHeight, this.fillColor);
    }
}
