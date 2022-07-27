package io.github.darealturtywurty.turtylib.client.ui.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import io.github.darealturtywurty.turtylib.client.util.ClientUtils;
import io.github.darealturtywurty.turtylib.client.util.Resources;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class Popout extends Screen {
    private final RenderFunction renderFunc;
    protected int x, y, width, height, leftPos, topPos;
    protected ResourceLocation textureLoc = Resources.POPOUT;
    private boolean visible, active;
    @Nonnull
    private final Runnable onClose;
    private final List<AbstractWidget> tempWidgets = new ArrayList<>();
    
    public Popout(int xPos, int yPos, int width, int height, RenderFunction renderFunc, Runnable onClose) {
        super(Component.empty());
        this.minecraft = ClientUtils.getMinecraft();
        this.font = this.minecraft.font;
        this.x = xPos;
        this.y = yPos;
        this.width = width;
        this.height = height;
        this.renderFunc = renderFunc;
        this.visible = false;
        this.onClose = onClose;
    }

    public Popout(int xPos, int yPos, int width, int height, Runnable onClose) {
        this(xPos, yPos, width, height, RenderFunction.NONE, onClose);
    }

    public void addWidgets(AbstractWidget... widgets) {
        Collections.addAll(this.tempWidgets, widgets);
    }

    public int getLeftPos() {
        return this.leftPos;
    }

    public int getTopPos() {
        return this.topPos;
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public void onClose() {
        super.onClose();
        this.onClose.run();
    }
    
    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            this.renderBackground(stack);
            RenderSystem.setShaderTexture(0, this.textureLoc);
            blit(stack, this.x, this.y, 0, 0, this.width, this.height);

            super.render(stack, mouseX, mouseY, partialTicks);
            
            this.renderFunc.render(stack, mouseX, mouseY, partialTicks);
        }
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
        if (visible) {
            this.minecraft.pushGuiLayer(this);
        } else {
            this.minecraft.popGuiLayer();
        }
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
    
    @Override
    protected void init() {
        super.init();
        for (final var widget : this.tempWidgets) {
            addRenderableWidget(widget);
        }
    }

    @FunctionalInterface
    public interface RenderFunction {
        RenderFunction NONE = (stack, mouseX, mouseY, partialTicks) -> {
        };

        void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks);
    }
}
