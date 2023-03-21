package dev.turtywurty.turtylib.client.ui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import dev.turtywurty.turtylib.client.util.FourVec2;
import dev.turtywurty.turtylib.client.util.GuiUtils;
import dev.turtywurty.turtylib.core.util.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.*;

public class LineGraphWidget extends AbstractWidget {
    private static final int DISTANCE = 16;
    private final HorizontalAxis horizontalAxis;
    private final VerticalAxis verticalAxis;

    private final List<Node> nodes = new ArrayList<>();

    private final Minecraft minecraft;

    public LineGraphWidget(int xPos, int yPos, int width, int height, VerticalAxis vertical, HorizontalAxis horizontal, List<Node> defaultNodes, List<Component> classes) {
        super(xPos, yPos, width, height, Component.empty());
        this.horizontalAxis = horizontal;
        this.verticalAxis = vertical;
        this.nodes.addAll(defaultNodes);
        for (final Component component : classes) {
            this.horizontalAxis.addClass(component);
        }

        this.minecraft = Minecraft.getInstance();
    }

    public void addNode(Node node) {
        this.nodes.add(node);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) return;

        this.isHovered = MathUtils.isWithinArea(mouseX, mouseY, this.x, this.y, this.width, this.height);

        drawAxis(stack);
        drawNodes(stack);

        Node previousNode = null;
        for (final Node node : this.nodes) {
            if (previousNode != null) {
                connectNode(previousNode, node, stack);
            }

            previousNode = node;
        }
    }

    @Override
    public void renderToolTip(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        if (this.visible && this.isHovered) {
            int index = 0;
            for (final Node node : this.nodes) {
                final Number maxNumb = this.nodes.stream().map(Node::getValue)
                        .max(Comparator.comparingDouble(Number::doubleValue)).orElse(0);
                final float delta = maxNumb.floatValue() - this.verticalAxis.minValue.floatValue();
                final int yPos = this.y + this.height - (int) (node.value.floatValue() / delta * this.height);
                final int xPos = this.x + index++ * 30;
                if (MathUtils.isWithinArea(pMouseX, pMouseY, xPos, yPos, 5, 5)) {
                    this.minecraft.screen.renderTooltip(pPoseStack, Component.literal(node.getValue().toString()),
                            pMouseX, pMouseY);
                }
            }
        }
    }

    @Override
    public void updateNarration(NarrationElementOutput narration) {
        defaultButtonNarrationText(narration);
    }

    private void connectNode(Node node0, Node node1, PoseStack stack) {
        final int index0 = this.nodes.indexOf(node0);
        final int index1 = this.nodes.indexOf(node1);

        final Number maxNumb = this.nodes.stream().map(Node::getValue)
                .max(Comparator.comparingDouble(Number::doubleValue)).orElse(0);
        final float delta = maxNumb.floatValue() - this.verticalAxis.minValue.floatValue();
        final int pos0 = this.y + this.height - (int) (node0.value.floatValue() / delta * this.height) + 2;
        final int pos1 = this.y + this.height - (int) (node1.value.floatValue() / delta * this.height) + 2;
        final FourVec2 fourVec = MathUtils.getFourVec(stack, this.x + 2 + index0 * 30, pos0, this.x + 2 + index1 * 30,
                pos1, 1);
        GuiUtils.drawLine(stack, fourVec.first(), fourVec.second(), fourVec.third(), fourVec.fourth(), 0xFF00AA00);
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
        stack.translate(this.x - 30, this.y + this.height / 2f, 0);
        stack.mulPose(Vector3f.ZP.rotationDegrees(-90f));
        GuiUtils.drawCenteredString(stack, verticalComponent, 0, 0, 0x404040);
        stack.popPose();
    }

    private void drawNodes(PoseStack stack) {
        int index = 0;
        for (final Node node : this.nodes) {
            renderNode(node, stack, this.x + index++ * 30, this.y);
        }
    }

    private void drawValues(PoseStack stack) {
        this.minecraft.font.draw(stack, Component.literal(this.verticalAxis.minValue + ""), this.x - 7,
                this.y + this.height - this.minecraft.font.lineHeight / 2f, 0x404040);
        final Number maxNumb = this.nodes.stream().map(Node::getValue)
                .max(Comparator.comparingDouble(Number::doubleValue)).orElse(0);

        final float amount = this.height / (float) DISTANCE;
        final float increment = (maxNumb.floatValue() - this.verticalAxis.minValue().floatValue()) / amount;

        for (float d = 1; d <= amount; d++) {
            final String toDraw = MathUtils.withSuffix((int) Math.ceil(d * increment));
            this.minecraft.font.draw(stack, toDraw, this.x - this.minecraft.font.width(toDraw) - 2,
                    this.y + this.height - DISTANCE * d - 5, 0x404040);
        }

        for (int index = 0; index < this.horizontalAxis.classes.size(); index++) {
            GuiUtils.drawCenteredString(stack, this.horizontalAxis.getClasses().get(index), this.x + index * 30,
                    this.y + this.height + 5, 0x404040);
        }
    }

    private void renderNode(Node node, PoseStack stack, int x, int y) {
        final Number maxNumb = this.nodes.stream().map(Node::getValue)
                .max(Comparator.comparingDouble(Number::doubleValue)).orElse(0);
        final float delta = maxNumb.floatValue() - this.verticalAxis.minValue.floatValue();
        final int pos = this.y + this.height - (int) (node.value.floatValue() / delta * this.height);
        GuiUtils.drawQuad(stack, x, pos, x + 5, pos + 5, 0xFFFF0000);
    }

    public interface Axis {
        String name();

        Orientation orientation();

        public enum Orientation {
            VERTICAL, HORIZONTAL;
        }
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

    public static class Node {
        private Optional<Component> name = Optional.empty();
        private final int classIndex;
        private Number value;

        public Node(Component name, int classIndex, Number value) {
            this(classIndex, value);
            this.name = Optional.of(name);
        }

        public Node(int classIndex, Number value) {
            this.classIndex = classIndex;
            setValue(value);
        }

        public int getClassIndex() {
            return this.classIndex;
        }

        public Optional<Component> getName() {
            return this.name;
        }

        public Number getValue() {
            return this.value;
        }

        public void setName(Component name) {
            this.name = Optional.of(name);
        }

        public void setValue(Number value) {
            this.value = value;
        }
    }

    public record VerticalAxis(String name, Number minValue, Number interval) implements Axis {
        @Override
        public Orientation orientation() {
            return Orientation.VERTICAL;
        }
    }
}
