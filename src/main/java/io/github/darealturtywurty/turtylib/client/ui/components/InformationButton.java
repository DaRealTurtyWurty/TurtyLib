package io.github.darealturtywurty.turtylib.client.ui.components;

import java.util.List;
import java.util.Optional;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import io.github.darealturtywurty.turtylib.client.util.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public class InformationButton extends AbstractWidget {
    
    private final List<Component> onHover;
    private final Optional<TooltipComponent> optionalComponent;
    protected final Minecraft minecraft;
    protected final Font font;
    protected final Screen screen;

    public InformationButton(Screen screen, int xPos, int yPos, int width, int height, Component... hoverText) {
        this(screen, xPos, yPos, width, height, Optional.empty(), hoverText);
    }
    
    public InformationButton(Screen screen, int xPos, int yPos, int width, int height,
            Optional<TooltipComponent> optionalComponent, Component... hoverText) {
        super(xPos, yPos, width, height, TextComponent.EMPTY);
        this.screen = screen;
        this.optionalComponent = optionalComponent;
        this.onHover = List.of(hoverText);
        this.minecraft = Minecraft.getInstance();
        this.font = this.minecraft.font;
    }

    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, Resources.INFORMATION);
            RenderSystem.disableDepthTest();
            blit(stack, this.x, this.y, 0, 0, this.width, this.height, 16, 16);
            RenderSystem.enableDepthTest();

            if (this.isHovered && this.active) {
                this.screen.renderTooltip(stack, this.onHover, this.optionalComponent, mouseX, mouseY);
            }
        }
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    @Override
    public void updateNarration(NarrationElementOutput narration) {
        narration.add(NarratedElementType.HINT, new TranslatableComponent("narration.turtychemistry.information_button",
                (Object[]) this.onHover.toArray(new Component[0])));
    }
}
