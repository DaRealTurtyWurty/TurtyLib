package testing.client.screen;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import io.github.darealturtywurty.turtylib.client.ui.components.BarChartWidget;
import io.github.darealturtywurty.turtylib.client.ui.components.EntityWidget;
import io.github.darealturtywurty.turtylib.client.ui.components.FluidWidget;
import io.github.darealturtywurty.turtylib.client.ui.components.FluidWidget.Orientation;
import io.github.darealturtywurty.turtylib.client.ui.components.LineGraphWidget;
import io.github.darealturtywurty.turtylib.client.ui.components.LineGraphWidget.HorizontalAxis;
import io.github.darealturtywurty.turtylib.client.ui.components.LineGraphWidget.VerticalAxis;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import net.minecraftforge.fluids.FluidStack;
import testing.TestMod;
import testing.common.blockentity.TestBlockEntity;

public class TestScreen extends Screen {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(TestMod.MODID, "textures/gui/test.png");

    private static final int IMG_WIDTH = 176;
    private static final int IMG_HEIGHT = 166;

    private int xPos, yPos;
    private FluidWidget renderedFluid;
    private ExtendedButton testButton;
    private LineGraphWidget lineGraph;
    private BarChartWidget barChart;
    private EntityWidget entity;

    public TestScreen(TestBlockEntity be, Component title) {
        super(title);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        renderBg(stack, mouseX, mouseY, partialTicks);
        renderFg(stack, mouseX, mouseY, partialTicks);
    }

    @SuppressWarnings("resource")
    @Override
    protected void init() {
        this.xPos = (this.width - IMG_WIDTH) / 2;
        this.yPos = (this.height - IMG_HEIGHT) / 2;
        this.renderedFluid = addWidget(new FluidWidget(new FluidStack(Fluids.WATER, 900), Orientation.BOTTOM_TOP,
            this.xPos + 12, this.yPos + 20, 150, 50, 16, 16, 1000, true));
        this.testButton = addWidget(
            new ExtendedButton(this.xPos + 45, this.yPos + 80, 80, 20, Component.literal("Switch Fluid"), pressable -> {
                this.barChart.addData(Component.literal("Beans" + ThreadLocalRandom.current().nextInt(20)),
                    ThreadLocalRandom.current().nextInt(200));
            }));
        
        this.lineGraph = addWidget(new LineGraphWidget(this.xPos + 40, this.yPos + 25, 125, 100,
            new VerticalAxis("Bruh", 0, 16), new HorizontalAxis("Bruh 2"),
            List.of(new LineGraphWidget.Node(0, 2000000), new LineGraphWidget.Node(1, 150000),
                new LineGraphWidget.Node(2, 1440000), new LineGraphWidget.Node(3, 753000)),
            List.of(Component.literal("Test"), Component.literal("Testa"), Component.literal("Testb"),
                Component.literal("Testc"), Component.literal("Testd"))));
        
        this.barChart = addWidget(new BarChartWidget(this.xPos + 40, this.yPos + 25, 125, 100,
            new BarChartWidget.VerticalAxis("Bruh", 0, 16), new BarChartWidget.HorizontalAxis("Bruh 2"),
            new LinkedHashMap<>(
                Map.of(Component.literal("Testa"), 5, Component.literal("Testb"), 1, Component.literal("Testc"), 15))));

        this.entity = addWidget(
            new EntityWidget.Builder(new Blaze(EntityType.BLAZE, Minecraft.getInstance().level), this.xPos + 40,
                this.yPos + 25, 100, 100).rotationSpeed(0).offset(5f, -7.5f, 0).scale(10f, 10f, 10f).build());
    }

    private void renderBg(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(stack);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        blit(stack, this.xPos, this.yPos, 0, 0, IMG_WIDTH, IMG_HEIGHT, 256, 256);
    }

    private void renderFg(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.font.draw(stack, this.title, this.xPos + 5, this.yPos + 8, 0x404040);
        // this.renderedFluid.render(stack, mouseX, mouseY, partialTicks);
        // this.testButton.render(stack, mouseX, mouseY, partialTicks);
        // this.lineGraph.render(stack, mouseX, mouseY, partialTicks);
        // this.barChart.render(stack, mouseX, mouseY, partialTicks);
        this.entity.render(stack, mouseX, mouseY, partialTicks);
    }
}
