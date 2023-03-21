package dev.turtywurty.turtylib.client.ui.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import dev.turtywurty.turtylib.client.util.FourVec2;
import dev.turtywurty.turtylib.client.util.GuiUtils;
import dev.turtywurty.turtylib.client.ui.components.LineGraphWidget.Axis;
import dev.turtywurty.turtylib.core.util.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class BarChartWidget extends AbstractWidget {
    private static final int DISTANCE = 16;

    private final Minecraft minecraft;
    private final VerticalAxis verticalAxis;
    private final HorizontalAxis horizontalAxis;

    private final Map<Component, Number> data;

    public BarChartWidget(int xPos, int yPos, int width, int height, VerticalAxis verticalAxis,
        HorizontalAxis horizontalAxis, Map<Component, Number> data) {
        super(xPos, yPos, width, height, Component.empty());

        this.minecraft = Minecraft.getInstance();

        this.verticalAxis = verticalAxis;
        this.horizontalAxis = horizontalAxis;
        this.data = data;
    }

    public void addData(Component name, Number value) {
        this.data.put(name, value);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if(!this.visible)
            return;

        this.isHovered = MathUtils.isWithinArea(mouseX, mouseY, this.x, this.y, this.width, this.height);

        drawAxis(stack);
        drawBars(stack);
        
        final var counter = new AtomicInteger(0);
        this.data.forEach((component, number) -> {
            final Number maxNumb = this.data.entrySet().stream().map(Entry::getValue)
                .sorted(Comparator.comparingDouble(Number::doubleValue).reversed()).findFirst().get();
            final float delta = maxNumb.floatValue() - this.verticalAxis.minValue.floatValue();
            final int left = this.x + 1 + counter.getAndIncrement() * 35;
            final int top = this.y + this.height - (int) (number.floatValue() / delta * this.height);
            final int right = 10;
            final int bottom = this.y + this.height - 1 - top;
            if (MathUtils.isWithinArea(mouseX, mouseY, left, top, right, bottom)) {
                this.minecraft.screen.renderTooltip(stack, Component.literal(number.toString()), mouseX, mouseY);
            }
        });
    }

    @Override
    public void updateNarration(NarrationElementOutput narration) {
        defaultButtonNarrationText(narration);
    }

    private void drawAxis(PoseStack stack) {
        final FourVec2 vertical = MathUtils.getFourVec(stack, this.x, this.y + this.height, this.x + this.width,
            this.y + this.height, 1);
        final FourVec2 horizontal = MathUtils.getFourVec(stack, this.x, this.y, this.x, this.y + this.height, 1);
        GuiUtils.drawLine(stack, vertical.first(), vertical.second(), vertical.third(), vertical.fourth(), 0xFF404040);
        GuiUtils.drawLine(stack, horizontal.first(), horizontal.second(), horizontal.third(), horizontal.fourth(),
            0xFF404040);

        drawValues(stack);

        final var horizontalComponent = Component.translatable(this.horizontalAxis.name());
        GuiUtils.drawCenteredString(stack, horizontalComponent, this.x + this.width / 2, this.y + this.height + 20,
            0x404040);

        final var verticalComponent = Component.translatable(this.verticalAxis.name());

        stack.pushPose();
        stack.translate(this.x - 30, this.y + this.height / 2, 0);
        stack.mulPose(Vector3f.ZP.rotationDegrees(-90f));
        GuiUtils.drawCenteredString(stack, verticalComponent, 0, 0, 0x404040);
        stack.popPose();
    }

    private void drawBars(PoseStack stack) {
        final var counter = new AtomicInteger(0);
        this.data.forEach((component, number) -> {
            final Number maxNumb = this.data.entrySet().stream().map(Entry::getValue)
                .sorted(Comparator.comparingDouble(Number::doubleValue).reversed()).findFirst().get();
            final float delta = maxNumb.floatValue() - this.verticalAxis.minValue.floatValue();
            final int top = this.y + this.height - (int) (number.floatValue() / delta * this.height);
            GuiUtils.drawQuad(stack, this.x + 1 + counter.get() * 35, top,
                this.x + 1 + 10 + counter.getAndIncrement() * 35, this.y + this.height - 1, 0xFFFF0000);
        });
    }

    private void drawValues(PoseStack stack) {
        this.minecraft.font.draw(stack, Component.literal(this.verticalAxis.minValue + ""), this.x - 7,
            this.y + this.height - this.minecraft.font.lineHeight / 2, 0x404040);
        final Number maxNumb = this.data.entrySet().stream().map(Entry::getValue)
            .sorted(Comparator.comparingDouble(Number::doubleValue).reversed()).findFirst().get();

        final float amount = this.height / DISTANCE;
        final float increment = (maxNumb.floatValue() - this.verticalAxis.minValue().floatValue()) / amount;

        for (float d = 1; d <= amount; d++) {
            final String toDraw = MathUtils.withSuffix((int) Math.ceil(d * increment));
            this.minecraft.font.draw(stack, toDraw, this.x - this.minecraft.font.width(toDraw) - 2,
                this.y + this.height - DISTANCE * d - 5, 0x404040);
        }

        final var counter = new AtomicInteger(0);
        this.data.forEach((component, number) -> GuiUtils.drawCenteredString(stack, component,
            this.x + 10 + counter.getAndIncrement() * 35, this.y + this.height + 5, 0x404040));
    }

    public static class HorizontalAxis implements Axis {
        private final String name;

        private List<Component> classes = new ArrayList<>();

        public HorizontalAxis(String name) {
            this.name = name;
        }

        public HorizontalAxis(String name, Component... classes) {
            this(name);
            Collections.addAll(this.classes, classes);
        }

        public void addClass(Component clazz) {
            this.classes.add(clazz);
        }

        public List<Component> getClasses() {
            return List.copyOf(this.classes);
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public Orientation orientation() {
            return Orientation.HORIZONTAL;
        }

        public void removeClass(Component clazz) {
            this.classes.remove(clazz);
        }

        public void removeClass(int index) {
            this.classes.remove(index);
        }
    }

    public record VerticalAxis(String name, Number minValue, Number interval) implements Axis {
        @Override
        public Orientation orientation() {
            return Orientation.VERTICAL;
        }
    }
}
