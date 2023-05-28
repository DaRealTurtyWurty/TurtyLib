package dev.turtywurty.turtylib.client.ui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.turtywurty.turtylib.core.util.MathUtils;
import it.unimi.dsi.fastutil.objects.Object2FloatFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TexturedButton extends Button {
    protected final Minecraft minecraft;
    protected final ResourceLocation texture;
    protected final Object2FloatFunction<TexturedButton> alpha;
    protected final int texX, texY, texWidth, texHeight;

    public TexturedButton(ResourceLocation texture, int xPos, int yPos, int width, int height, int texX, int texY, int texWidth, int texHeight, float alpha, OnPress pressable, CreateNarration narration) {
        this(texture, xPos, yPos, width, height, texX, texY, texWidth, texHeight, btn -> alpha, pressable, narration);
    }

    public TexturedButton(ResourceLocation texture, int xPos, int yPos, int width, int height, int texX, int texY, int texWidth, int texHeight, Object2FloatFunction<TexturedButton> alpha, OnPress pressable, CreateNarration narration) {
        super(xPos, yPos, width, height, Component.empty(), pressable, narration);
        this.minecraft = Minecraft.getInstance();
        this.texture = texture;
        this.alpha = alpha;
        this.texX = texX;
        this.texY = texY;
        this.texWidth = texWidth;
        this.texHeight = texHeight;
    }

    @Override
    public void renderWidget(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) return;

        this.isHovered = MathUtils.isWithinArea(mouseX, mouseY, getX(), getY(), getWidth(), getHeight());

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.texture);
        final float alpha = this.alpha.getFloat(this);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha >= 0 && this.alpha.getFloat(this) <= 1 ? alpha : 1.0f);
        final int yTexOffset = this.isHovered ? getHeight() : 0;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        blit(stack, getX(), getY(), this.texX, this.texY + yTexOffset, getWidth(), getHeight(), this.texWidth,
                this.texHeight);
    }

    @FunctionalInterface
    public interface OnPress<TYPE extends TexturedButton> extends Button.OnPress {
        @SuppressWarnings("unchecked")
        @Override
        default void onPress(Button btn) {
            this.onPress((TYPE) btn);
        }

        void onPress(TYPE button);
    }
}
