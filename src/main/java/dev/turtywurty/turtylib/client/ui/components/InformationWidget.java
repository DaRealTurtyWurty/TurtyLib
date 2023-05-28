package dev.turtywurty.turtylib.client.ui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.turtywurty.turtylib.client.util.Resources;
import dev.turtywurty.turtylib.core.util.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.List;
import java.util.Optional;

public class InformationWidget extends AbstractWidget {
    private final List<Component> onHover;
    private final Optional<TooltipComponent> optionalComponent;
    protected final Minecraft minecraft;
    protected final Font font;
    protected final Screen screen;

    public InformationWidget(Screen screen, int xPos, int yPos, int width, int height, Component... hoverText) {
        this(screen, xPos, yPos, width, height, Optional.empty(), hoverText);
    }

    public InformationWidget(Screen screen, int xPos, int yPos, int width, int height, Optional<TooltipComponent> optionalComponent, Component... hoverText) {
        super(xPos, yPos, width, height, Component.empty());
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
    public void renderWidget(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) return;

        this.isHovered = MathUtils.isWithinArea(mouseX, mouseY, getX(), getY(), getWidth(), getHeight());

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, Resources.INFORMATION);
        RenderSystem.disableDepthTest();
        blit(stack, getX(), getY(), 0, 0, getWidth(), getHeight(), 16, 16);
        RenderSystem.enableDepthTest();
    }

    @Deprecated(since = "1.19.3")
    public void renderToolTip(PoseStack stack, int mouseX, int mouseY) {
        if (this.visible && this.isHovered) {
            this.screen.renderTooltip(stack, this.onHover, this.optionalComponent, mouseX, mouseY);
        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput narration) {
        narration.add(NarratedElementType.HINT, Component.translatable("narration.turtylib.information_button", this.onHover.toArray(new Object[0])));
    }
}
