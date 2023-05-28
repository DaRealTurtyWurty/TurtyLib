package dev.turtywurty.turtylib.client.ui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.turtywurty.turtylib.client.ui.components.LineGraphWidget.Axis;
import dev.turtywurty.turtylib.client.util.FourVec2;
import dev.turtywurty.turtylib.client.util.GuiUtils;
import dev.turtywurty.turtylib.core.util.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
    public void renderWidget(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if(!this.visible)
            return;

        this.isHovered = MathUtils.isWithinArea(mouseX, mouseY, getX(), getY(), getWidth(), getHeight());

        drawAxis(stack);
        drawBars(stack);
        
        final var counter = new AtomicInteger(0);
        this.data.forEach((component, number) -> {
            final Number maxNumb = this.data.values().stream().max(Comparator.comparingDouble(Number::doubleValue)).get();
            final float delta = maxNumb.floatValue() - this.verticalAxis.minValue.floatValue();
            final int left = getX() + 1 + counter.getAndIncrement() * 35;
            final int top = getY() + getHeight() - (int) (number.floatValue() / delta * getHeight());
            final int right = 10;
            final int bottom = getY() + getHeight() - 1 - top;
            if (MathUtils.isWithinArea(mouseX, mouseY, left, top, right, bottom)) {
                if (this.minecraft.screen != null) {
                    this.minecraft.screen.renderTooltip(stack, Component.literal(number.toString()), mouseX, mouseY);
                }
            }
        });
    }

    @Override
    public void updateWidgetNarration(@NotNull NarrationElementOutput narration) {
        defaultButtonNarrationText(narration);
    }

    private void drawAxis(PoseStack stack) {
        final FourVec2 vertical = MathUtils.getFourVec(stack, getX(), getY() + getHeight(), getX() + getWidth(),
            getY() + getHeight(), 1);
        final FourVec2 horizontal = MathUtils.getFourVec(stack, getX(), getY(), getX(), getY() + getHeight(), 1);
        GuiUtils.drawLine(stack, vertical.first(), vertical.second(), vertical.third(), vertical.fourth(), 0xFF404040);
        GuiUtils.drawLine(stack, horizontal.first(), horizontal.second(), horizontal.third(), horizontal.fourth(),
            0xFF404040);

        drawValues(stack);

        final var horizontalComponent = Component.translatable(this.horizontalAxis.name());
        GuiUtils.drawCenteredString(stack, horizontalComponent, getX() + getWidth() / 2, getY() + getHeight() + 20,
            0x404040);

        final var verticalComponent = Component.translatable(this.verticalAxis.name());

        stack.pushPose();
        stack.translate(getX() - 30, getY() + getHeight() / 2f, 0);
        stack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-90f));
        GuiUtils.drawCenteredString(stack, verticalComponent, 0, 0, 0x404040);
        stack.popPose();
    }

    private void drawBars(PoseStack stack) {
        final var counter = new AtomicInteger(0);
        this.data.forEach((component, number) -> {
            final Number maxNumb = this.data.values().stream().max(Comparator.comparingDouble(Number::doubleValue)).get();
            final float delta = maxNumb.floatValue() - this.verticalAxis.minValue.floatValue();
            final int top = getY() + getHeight() - (int) (number.floatValue() / delta * getHeight());
            GuiUtils.drawQuad(stack, getX() + 1 + counter.get() * 35, top,
                getX() + 1 + 10 + counter.getAndIncrement() * 35, getY() + getHeight() - 1, 0xFFFF0000);
        });
    }

    private void drawValues(PoseStack stack) {
        this.minecraft.font.draw(stack, Component.literal(String.valueOf(this.verticalAxis.minValue)), getX() - 7,
            getY() + getHeight() - this.minecraft.font.lineHeight / 2f, 0x404040);
        final Number maxNumb = this.data.values().stream().max(Comparator.comparingDouble(Number::doubleValue)).get();

        final float amount = (float) getHeight() / DISTANCE;
        final float increment = (maxNumb.floatValue() - this.verticalAxis.minValue().floatValue()) / amount;

        for (float d = 1; d <= amount; d++) {
            final String toDraw = MathUtils.withSuffix((int) Math.ceil(d * increment));
            this.minecraft.font.draw(stack, toDraw, getX() - this.minecraft.font.width(toDraw) - 2,
                getY() + getHeight() - DISTANCE * d - 5, 0x404040);
        }

        final var counter = new AtomicInteger(0);
        this.data.forEach((component, number) -> GuiUtils.drawCenteredString(stack, component,
            getX() + 10 + counter.getAndIncrement() * 35, getY() + getHeight() + 5, 0x404040));
    }

    public static class HorizontalAxis implements Axis {
        private final String name;

        private final List<Component> classes = new ArrayList<>();

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
