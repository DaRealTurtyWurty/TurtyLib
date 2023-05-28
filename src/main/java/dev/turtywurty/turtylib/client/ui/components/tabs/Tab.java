package dev.turtywurty.turtylib.client.ui.components.tabs;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.turtywurty.turtylib.client.ui.components.AnimatableTexture;
import dev.turtywurty.turtylib.client.ui.components.TexturedButton;
import dev.turtywurty.turtylib.client.util.Resources;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class Tab extends TexturedButton {
    public static final ToIntFunction<Tab> DEFAULT_Y_OFFSET = tab -> tab.isHovered || tab.isSelected ? 32 : 0;

    protected final TabHolder<? extends Tab> tabHolder;
    public final TabPage page;
    protected final Orientation orientation;
    protected ResourceLocation icon = Resources.Icons.ERROR;
    public boolean isSelected;
    protected final ToIntFunction<Tab> changeYOffset;
    private AnimatableTexture animatableIcon;
    private final Component label;

    public Tab(TabHolder<? extends Tab> screen, TabPage page, Orientation orientation, String name, int xPos, int yPos) {
        this(screen, page, orientation, name, xPos, yPos, calculateTabWidth(orientation), calculateTabHeight(orientation), calculateTabX(orientation), DEFAULT_Y_OFFSET);
    }

    public Tab(TabHolder<? extends Tab> screen, TabPage page, Orientation orientation, String name, final int xPos, final int yPos, final int width, final int height, final int texX, ToIntFunction<Tab> changeYOffset) {
        super(Resources.TAB_LOC, xPos, yPos, width, height, texX, 0, 256, 256, 1.0f, btn -> ((Tab) btn).handle(), Supplier::get);
        this.tabHolder = screen;
        this.page = page;
        this.orientation = orientation;
        this.changeYOffset = changeYOffset;
        this.label = Component.translatable("tab." + name);
        this.page.setLabel(this.label);
    }

    public AnimatableTexture getAnimatableIcon() {
        return this.animatableIcon;
    }

    public Component getLabel() {
        return this.label;
    }

    public boolean hasAnimation() {
        return this.animatableIcon != null;
    }

    @Override
    public void renderWidget(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            this.isHovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + getWidth() && mouseY < getY() + getHeight();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, this.texture);
            final float alpha = this.alpha.getFloat(this);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha >= 0 && this.alpha.getFloat(this) <= 1 ? alpha : 1.0f);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            blit(stack, getX(), getY(), this.texX, this.active || this.isSelected ? this.changeYOffset.applyAsInt(this) : 0, getWidth(), getHeight(), this.texWidth, this.texHeight);
            if (!hasAnimation()) {
                RenderSystem.setShaderTexture(0, this.icon);
                blit(stack, getX() + getWidth() / 2 - 8, getY() + getHeight() / 2 - 8, 0, 0, 16, 16, 16, 16);
            } else {
                getAnimatableIcon().render(stack, mouseX, mouseY, partialTicks);
            }

            if (this.isHovered && isActive()) {
                onHovered(stack, mouseX, mouseY);
            }
        }
    }

    public void setIcon(AnimatableTexture icon) {
        setIcon(icon, Resources.Icons.ERROR);
    }

    public void setIcon(AnimatableTexture icon, ResourceLocation fallback) {
        this.animatableIcon = icon;
        this.animatableIcon.setX(getX() + getWidth() / 2 - 8);
        this.animatableIcon.setY(getY() + getHeight() / 2 - 8);
        this.animatableIcon.setActive(false);
        setIcon(fallback);
    }

    public void setIcon(ResourceLocation icon) {
        this.icon = icon;
    }

    protected void onHovered(PoseStack stack, int mouseX, int mouseY) {
        this.tabHolder.getScreen().renderTooltip(stack, this.label, mouseX, mouseY);
    }

    private void handle() {
        if (this.tabHolder.getSelectedTab() != this) {
            this.tabHolder.setSelectedTab(this);
        }
    }

    private void rotateAround(PoseStack stack, Vector3f pivot, Axis axis, float angle) {
        stack.translate(pivot.x(), pivot.y(), pivot.z());
        stack.mulPose(axis.rotationDegrees(angle));
        stack.translate(-pivot.x(), -pivot.y(), -pivot.z());
    }

    private static int calculateTabHeight(Orientation orientation) {
        return switch (orientation) {
            case TOP_BOTTOM, BOTTOM_TOP -> 30;
            case RIGHT_LEFT, LEFT_RIGHT -> 28;
            default -> throw new IllegalArgumentException("Unexpected value: " + orientation);
        };
    }

    private static int calculateTabWidth(Orientation orientation) {
        return switch (orientation) {
            case TOP_BOTTOM, BOTTOM_TOP -> 28;
            case RIGHT_LEFT, LEFT_RIGHT -> 30;
            default -> throw new IllegalArgumentException("Unexpected value: " + orientation);
        };
    }

    private static int calculateTabX(Orientation orientation) {
        return switch (orientation) {
            case TOP_BOTTOM -> 0;
            case BOTTOM_TOP -> 30;
            case LEFT_RIGHT -> 60;
            case RIGHT_LEFT -> 92;
            default -> throw new IllegalArgumentException("Unexpected value: " + orientation);
        };
    }

    public enum Orientation {
        TOP_BOTTOM(0f), RIGHT_LEFT(90f), BOTTOM_TOP(180f), LEFT_RIGHT(270f);

        public final float rotDegrees;
        public final boolean vertical;

        Orientation(float degrees) {
            this.rotDegrees = degrees;
            this.vertical = this.rotDegrees / 90f % 2 == 1;
        }
    }
}
