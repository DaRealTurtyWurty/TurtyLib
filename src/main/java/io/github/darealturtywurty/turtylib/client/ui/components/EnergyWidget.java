package io.github.darealturtywurty.turtylib.client.ui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import io.github.darealturtywurty.turtylib.client.util.Gradient;
import io.github.darealturtywurty.turtylib.core.util.Either3;
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

    private final boolean tooltip;
    private final EnergyStorage energyStorage;
    private final IntSupplier energySupplier, maxEnergySupplier;

    private final ResourceLocation texture;
    private final Int2IntFunction color;
    private final Gradient gradient;

    private EnergyWidget(Screen screen, int x, int y, int width, int height, boolean tooltip, Either3<ResourceLocation, Int2IntFunction, Gradient> textureOrColorOrGradient, Either<EnergyStorage, Pair<IntSupplier, IntSupplier>> energyStorage) {
        super(x, y, width, height, Component.empty());
        this.screen = screen;

        this.tooltip = tooltip;
        this.texture = textureOrColorOrGradient.map(texture -> texture, color -> null, gradient -> null);
        this.color = textureOrColorOrGradient.map(texture -> null, color -> color, gradient -> null);
        this.gradient = textureOrColorOrGradient.map(texture -> null, color -> null, gradient -> gradient);
        this.energyStorage = energyStorage.map(storage -> storage, pair -> null);
        this.energySupplier = energyStorage.map(storage -> null, Pair::getFirst);
        this.maxEnergySupplier = energyStorage.map(storage -> null, Pair::getSecond);
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
        defaultButtonNarrationText(pNarrationElementOutput);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        int energy = this.energyStorage != null ? this.energyStorage.getEnergyStored() : this.energySupplier.getAsInt();
        int maxEnergy = this.energyStorage != null ? this.energyStorage.getMaxEnergyStored() : this.maxEnergySupplier.getAsInt();
        int energyHeight = (int) ((float) energy / maxEnergy * this.height);

        if (texture != null) {
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, this.texture);

            blit(pPoseStack, this.x, this.y + this.height - energyHeight, 0, 0, this.width, energyHeight);
        } else if (gradient != null) {
            this.gradient.draw(pPoseStack, this.x, this.y + this.height - energyHeight, this.x + this.width,
                    this.y + this.height);
        } else {
            fill(pPoseStack, this.x, this.y + this.height - energyHeight, this.x + this.width, this.y + this.height,
                    this.color.applyAsInt(energy));
        }

        if (this.tooltip) {
            renderToolTip(pPoseStack, pMouseX, pMouseY);
        }
    }

    @Override
    public void renderToolTip(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        if (!isMouseOver(pMouseX, pMouseY)) return;

        int energy = this.energyStorage != null ? this.energyStorage.getEnergyStored() : this.energySupplier.getAsInt();
        int maxEnergy = this.energyStorage != null ? this.energyStorage.getMaxEnergyStored() : this.maxEnergySupplier.getAsInt();
        this.screen.renderTooltip(pPoseStack, Component.nullToEmpty(energy + " / " + maxEnergy + " FE"), pMouseX,
                pMouseY);
    }

    public static class Builder {
        private final int x, y, width, height;

        private boolean tooltip = false;
        private Either<EnergyStorage, Pair<IntSupplier, IntSupplier>> energyStorage = Either.right(
                Pair.of(() -> 0, () -> 0));
        private Either3<ResourceLocation, Int2IntFunction, Gradient> textureOrColorOrGradient = Either3.middle(
                i -> 0xFFFF0000);

        public Builder(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
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

        public Builder tooltip() {
            this.tooltip = true;
            return this;
        }

        public EnergyWidget build(Screen screen) {
            return new EnergyWidget(screen, this.x, this.y, this.width, this.height, this.tooltip,
                    this.textureOrColorOrGradient, this.energyStorage);
        }
    }
}
