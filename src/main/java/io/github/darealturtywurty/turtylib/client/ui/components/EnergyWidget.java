package io.github.darealturtywurty.turtylib.client.ui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.energy.EnergyStorage;

import java.util.function.IntSupplier;

public class EnergyWidget extends AbstractWidget {
    private final EnergyStorage energyStorage;
    private final IntSupplier energySupplier, maxEnergySupplier;

    private final ResourceLocation texture;
    private final Int2IntFunction color;


    private EnergyWidget(int x, int y, int width, int height, Either<ResourceLocation, Int2IntFunction> textureOrColor, Either<EnergyStorage, Pair<IntSupplier, IntSupplier>> energyStorage) {
        super(x, y, width, height, Component.empty());
        this.texture = textureOrColor.map(texture -> texture, color -> null);
        this.color = textureOrColor.map(texture -> null, color -> color);
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
            return;
        }

        fill(pPoseStack, this.x, this.y + this.height - energyHeight, this.x + this.width, this.y + this.height,
                this.color.applyAsInt(energy));
    }

    public static class Builder {
        private Either<EnergyStorage, Pair<IntSupplier, IntSupplier>> energyStorage = Either.right(
                Pair.of(() -> 0, () -> 0));
        private Either<ResourceLocation, Int2IntFunction> textureOrColor = Either.right(i -> 0xFFFF0000);
        private final int x, y, width, height;

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
            this.textureOrColor = Either.left(texture);
            return this;
        }

        public Builder color(Int2IntFunction color) {
            this.textureOrColor = Either.right(color);
            return this;
        }

        public EnergyWidget build() {
            return new EnergyWidget(this.x, this.y, this.width, this.height, this.textureOrColor, this.energyStorage);
        }
    }
}
