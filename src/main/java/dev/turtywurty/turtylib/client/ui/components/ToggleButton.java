package dev.turtywurty.turtylib.client.ui.components;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.turtywurty.turtylib.client.util.Resources;
import dev.turtywurty.turtylib.core.util.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class ToggleButton extends AbstractWidget {
    protected boolean isStateTriggered;
    protected int xTexStart = 0;
    protected int yTexStart = 0;
    protected int xDiffTex = 0;
    protected int yDiffTex = 16;
    @Nonnull
    protected ResourceLocation textureLocation = Resources.TOGGLE_SWITCH;
    @Nonnull
    protected Component label = Component.empty();
    private final Minecraft minecraft = Minecraft.getInstance();
    private final Font font = this.minecraft.font;
    private final int baseXPos;
    private Pressable onPress = Pressable.NONE;

    public ToggleButton(int xPos, int yPos, int width, int height) {
        this(xPos, yPos, width, height, false);
    }
    
    public ToggleButton(int xPos, int yPos, int width, int height, boolean triggered) {
        super(xPos, yPos, width, height, Component.empty());
        this.isStateTriggered = triggered;
        this.baseXPos = xPos;
    }
    
    public Component getLabel() {
        return this.label;
    }
    
    public void initTextureValues(int xTexStart, int yTexStart, int xDiffTex, int yDiffTex) {
        initTextureValues(xTexStart, yTexStart, xDiffTex, yDiffTex, Resources.TOGGLE_SWITCH);
    }

    public void initTextureValues(int xTexStart, int yTexStart, int xDiffTex, int yDiffTex,
        ResourceLocation textureLoc) {
        this.xTexStart = xTexStart;
        this.yTexStart = yTexStart;
        this.xDiffTex = xDiffTex;
        this.yDiffTex = yDiffTex;
        this.textureLocation = textureLoc;
    }
    
    public boolean isStateTriggered() {
        return this.isStateTriggered;
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int key) {
        if (isMouseOver(mouseX, mouseY)) {
            setStateTriggered(!this.isStateTriggered);
            this.onPress.onPress(mouseX, mouseY, key);
        }
        return super.mouseClicked(mouseX, mouseY, key);
    }
    
    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) return;

        this.isHovered = MathUtils.isWithinArea(mouseX, mouseY, this.x, this.y, this.width, this.height);

        if (!this.label.getString().isBlank()) {
            this.font.draw(stack, this.label, this.baseXPos, this.y + this.height / 4f, 0x404040);
            this.x = this.baseXPos + this.font.width(this.label);
        }

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.textureLocation);
        RenderSystem.disableDepthTest();
        int xPos = this.xTexStart;
        int yPos = this.yTexStart;
        if (this.isStateTriggered) {
            xPos += this.xDiffTex;
            yPos += this.yDiffTex;
        }

        blit(stack, this.x, this.y, xPos, yPos, this.width, this.height, 32, 32);
        RenderSystem.enableDepthTest();
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public void setLabel(MutableComponent label) {
        final String toAppend = ":  ";
        final String raw = this.label.getString().replace(toAppend, "");
        final MutableComponent copy = label.copy();

        if (copy.getString().isBlank() || copy.getString().equalsIgnoreCase(raw)) {
            this.label = copy;
        } else {
            this.label = copy.append(toAppend);
        }
    }
    
    public void setOnPressed(Pressable onPress) {
        if (onPress != null) {
            this.onPress = onPress;
        }
    }
    
    public void setStateTriggered(boolean isStateTriggered) {
        this.isStateTriggered = isStateTriggered;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    @Override
    public void updateNarration(NarrationElementOutput narration) {
        defaultButtonNarrationText(narration);
    }
    
    @FunctionalInterface
    public interface Pressable {
        Pressable NONE = (mouseX, mouseY, key) -> {
        };

        void onPress(double mouseX, double mouseY, int key);
    }
}
