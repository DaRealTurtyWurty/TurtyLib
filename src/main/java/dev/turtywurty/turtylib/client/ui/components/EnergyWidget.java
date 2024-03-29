package dev.turtywurty.turtylib.client.ui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import dev.turtywurty.turtylib.client.util.Gradient;
import dev.turtywurty.turtylib.client.util.GuiUtils;
import dev.turtywurty.turtylib.core.util.Either3;
import dev.turtywurty.turtylib.core.util.MathUtils;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.energy.EnergyStorage;

import java.util.function.IntSupplier;

public class EnergyWidget extends AbstractWidget {
    private final Screen screen;

    private final boolean tooltip, drawBorder;
    private final EnergyStorage energyStorage;
    private final IntSupplier energySupplier, maxEnergySupplier;

    private final ResourceLocation texture;
    private final Int2IntFunction color;
    private final Gradient gradient;

    private EnergyWidget(Screen screen, int x, int y, int width, int height, boolean tooltip, boolean drawBorder, Either3<ResourceLocation, Int2IntFunction, Gradient> textureOrColorOrGradient, Either<EnergyStorage, Pair<IntSupplier, IntSupplier>> energyStorage) {
        super(x, y, width, height, Component.empty());
        this.screen = screen;

        this.tooltip = tooltip;
        this.drawBorder = drawBorder;
        this.texture = textureOrColorOrGradient.map(texture -> texture, color -> null, gradient -> null);
        this.color = textureOrColorOrGradient.map(texture -> null, color -> color, gradient -> null);
        this.gradient = textureOrColorOrGradient.map(texture -> null, color -> null, gradient -> gradient);
        this.energyStorage = energyStorage.map(storage -> storage, pair -> null);
        this.energySupplier = energyStorage.map(storage -> null, Pair::getFirst);
        this.maxEnergySupplier = energyStorage.map(storage -> null, Pair::getSecond);
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
        defaultButtonNarrationText(pNarrationElementOutput);
    }

    @Override
    public void renderWidget(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (!this.visible) return;

        this.isHovered = MathUtils.isWithinArea(pMouseX, pMouseY, getX(), getY(), getWidth(), getHeight());

        if (this.drawBorder) {
            GuiUtils.drawOutline(pPoseStack, getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0xFF000000, 1);
        }

        int energy = this.energyStorage != null ? this.energyStorage.getEnergyStored() : this.energySupplier.getAsInt();
        int maxEnergy = this.energyStorage != null ? this.energyStorage.getMaxEnergyStored() : this.maxEnergySupplier.getAsInt();
        int energyHeight = (int) ((float) energy / maxEnergy * getHeight());

        if (texture != null) {
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, this.texture);

            blit(pPoseStack, getX(), getY() + getHeight() - energyHeight, 0, 0, getWidth(), energyHeight);
        } else if (gradient != null) {
            this.gradient.draw(pPoseStack, getX(), getY() + getHeight() - energyHeight, getX() + getWidth(),
                    getY() + getHeight());
        } else {
            fill(pPoseStack, getX(), getY() + getHeight() - energyHeight, getX() + getWidth(), getY() + getHeight(),
                    this.color.applyAsInt(energy));
        }
    }

    @Deprecated(since = "1.19.3")
    public void renderToolTip(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        if (this.visible && this.isHovered && this.tooltip) return;

        int energy = this.energyStorage != null ? this.energyStorage.getEnergyStored() : this.energySupplier.getAsInt();
        int maxEnergy = this.energyStorage != null ? this.energyStorage.getMaxEnergyStored() : this.maxEnergySupplier.getAsInt();
        this.screen.renderTooltip(pPoseStack, Component.nullToEmpty(energy + " / " + maxEnergy + " FE"), pMouseX,
                pMouseY);
    }

    public static class Builder {
        private final int x, y, width, height;

        private boolean tooltip = true;
        private boolean drawBorder = true;

        private Either<EnergyStorage, Pair<IntSupplier, IntSupplier>> energyStorage = Either.right(
                Pair.of(() -> 0, () -> 0));
        private Either3<ResourceLocation, Int2IntFunction, Gradient> textureOrColorOrGradient = Either3.right(
                Gradient.of(0xFFFF0000, 0xFF660000));

        public Builder(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public Builder tooltip(boolean tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public Builder drawBorder(boolean drawBorder) {
            this.drawBorder = drawBorder;
            return this;
        }

        public Builder energyStorage(EnergyStorage energyStorage) {
            this.energyStorage = Either.left(energyStorage);
            return this;
        }

        public Builder energyStorage(IntSupplier energy, IntSupplier maxEnergy) {
            this.energyStorage = Either.right(Pair.of(energy, maxEnergy));
            return this;
        }

        public Builder texture(ResourceLocation texture) {
            this.textureOrColorOrGradient = Either3.left(texture);
            return this;
        }

        public Builder color(Int2IntFunction color) {
            this.textureOrColorOrGradient = Either3.middle(color);
            return this;
        }

        public Builder gradient(Gradient gradient) {
            this.textureOrColorOrGradient = Either3.right(gradient);
            return this;
        }

        public EnergyWidget build(Screen screen) {
            return new EnergyWidget(screen, this.x, this.y, this.width, this.height, this.tooltip, this.drawBorder,
                    this.textureOrColorOrGradient, this.energyStorage);
        }
    }
}
