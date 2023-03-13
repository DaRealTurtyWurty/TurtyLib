package dev.turtywurty.turtylib.client.ui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.function.IntSupplier;

public class ProgressBarWidget extends AbstractWidget {
    private final int fillX, fillY, fillWidth, fillHeight, fillColor;
    private final IntSupplier progressSupplier, maxProgressSupplier;

    private int progress, maxProgress;

    public ProgressBarWidget(int pX, int pY, int pWidth, int pHeight, int fillX, int fillY, int fillWidth, int fillHeight, int fillColor, IntSupplier progressSupplier, IntSupplier maxProgressSupplier) {
        super(pX, pY, pWidth, pHeight, Component.empty());
        this.fillX = fillX;
        this.fillY = fillY;
        this.fillWidth = fillWidth;
        this.fillHeight = fillHeight;
        this.fillColor = fillColor;
        this.progressSupplier = progressSupplier;
        this.maxProgressSupplier = maxProgressSupplier;
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
        defaultButtonNarrationText(pNarrationElementOutput);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (!this.visible || !this.active) return;

        this.progress = this.progressSupplier.getAsInt();
        this.maxProgress = this.maxProgressSupplier.getAsInt();

        // Render progress bar
        int fillWidth = (int) ((float) this.progress / (float) this.maxProgress * this.fillWidth);
        fill(pPoseStack, this.fillX, this.fillY, this.fillX + fillWidth, this.fillY + this.fillHeight, this.fillColor);
    }
}
