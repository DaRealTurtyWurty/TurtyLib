package io.github.darealturtywurty.turtylib.client.ui.components;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import io.github.darealturtywurty.turtylib.client.util.ImageInfo;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class AnimatableTexture extends AbstractWidget {
    private final int imageWidth, imageHeight, frameCount;
    private final ResourceLocation textureLocation;
    private int currentFrame;
    private double lastMillis = 0, countTimeForFrames = 0;
    private final Map<Integer, Integer> frameLengths = new HashMap<>();
    private final int defaultFrameTime;

    public AnimatableTexture(int xPos, int yPos, ResourceLocation location) {
        super(xPos, yPos, 16, 16, TextComponent.EMPTY);
        this.textureLocation = location;

        final var imgInfo = new ImageInfo(location);
        this.imageWidth = imgInfo.width;
        this.imageHeight = imgInfo.height;
        this.frameCount = imgInfo.hasMetadata ? imgInfo.animationData.get().frameCount : 1;
        this.defaultFrameTime = imgInfo.hasMetadata ? imgInfo.animationData.get().frameTime : 1;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            final double currentMillis = System.currentTimeMillis();
            final double timeOfLastFrame = currentMillis - this.lastMillis;
            this.lastMillis = currentMillis;
            this.countTimeForFrames += timeOfLastFrame / 1000f;

            final int frametime = this.frameLengths.get(this.currentFrame) == null ? this.defaultFrameTime
                    : this.frameLengths.get(this.currentFrame);
            // System.out.println(frametime);
            if (this.countTimeForFrames >= 1f / frametime) {
                this.countTimeForFrames = 0;

                if (++this.currentFrame >= this.frameCount) {
                    this.currentFrame = 0;
                }
            }

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, this.textureLocation);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            blit(stack, this.x, this.y, 0, this.width * this.currentFrame, this.width, this.height, this.imageWidth,
                    this.imageHeight);
        }
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    @Override
    public void updateNarration(NarrationElementOutput narration) {
        defaultButtonNarrationText(narration);
    }
    
    public static double getTimeInSeconds() {
        return (double) System.nanoTime() / (double) 1000000000L;
    }
}