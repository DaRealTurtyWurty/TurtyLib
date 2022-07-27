package io.github.darealturtywurty.turtylib.client.ui.components.tabs;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import io.github.darealturtywurty.turtylib.client.util.GuiUtils;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public abstract class TabPage {
    public final ResourceLocation baseTexture;
    private final int leftPos, topPos, width, height, textureWidth, textureHeight;
    private Component label = Component.empty();
    
    protected TabPage(ResourceLocation texture, int leftPos, int topPos, int width, int height, int textureWidth,
        int textureHeight) {
        this.baseTexture = texture;
        this.leftPos = leftPos;
        this.topPos = topPos;
        this.width = width;
        this.height = height;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }
    
    public Component getLabel() {
        return this.label;
    }
    
    public abstract void initWidgets();
    
    public void renderBackground(PoseStack stack, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, this.baseTexture);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        GuiComponent.blit(stack, this.leftPos, this.topPos, 0, 0, this.width, this.height, this.textureWidth,
            this.textureHeight);
    }
    
    public abstract void renderCenterground(PoseStack stack, int mouseX, int mouseY);

    public void renderForeground(PoseStack stack, int mouseX, int mouseY) {
        GuiUtils.drawCenteredString(stack, this.label, this.leftPos + this.width / 2, this.topPos + 35, 0x404040);
    }
    
    public void setLabel(Component label) {
        if (label != null) {
            this.label = label;
        }
    }
    
    public abstract void uninitWidgets();
}
