package dev.turtywurty.turtylib.client.ui.components;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.turtywurty.turtylib.client.util.ClientUtils;
import dev.turtywurty.turtylib.client.util.GuiUtils;
import dev.turtywurty.turtylib.core.util.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

public class FluidWidget extends AbstractWidget {
    private final Minecraft minecraft;

    private int maximum;
    private Orientation orientation;
    private boolean drawBorder;
    private int borderColour = 0xFF000000;
    private FluidInfo info;


    public FluidWidget(FluidStack fluid, int xPos, int yPos, int width, int height, int loopX, int loopY) {
        this(fluid, xPos, yPos, width, height, loopX, loopY, false);
    }
    
    public FluidWidget(FluidStack fluid, int xPos, int yPos, int width, int height, int loopX, int loopY,
        boolean flowing) {
        this(fluid, Orientation.BOTTOM_TOP, xPos, yPos, width, height, loopX, loopY, 1000, flowing);
    }
    
    public FluidWidget(FluidStack fluid, Orientation orientation, int xPos, int yPos, int width, int height, int loopX,
        int loopY, int max) {
        this(fluid, orientation, xPos, yPos, width, height, loopX, loopY, max, false);
    }
    
    public FluidWidget(FluidStack fluid, Orientation orientation, int xPos, int yPos, int width, int height, int loopX,
        int loopY, int max, boolean flowing) {
        super(xPos, yPos, width, height, Component.empty());
        this.info = new FluidInfo();
        this.info.loopX = loopX;
        this.info.loopY = loopY;
        this.maximum = max;
        this.orientation = orientation;
        this.info.flowing = flowing;
        
        this.minecraft = Minecraft.getInstance();

        setFluid(fluid);
    }
    
    public void drawTooltip(PoseStack stack, int mouseX, int mouseY) {
        final boolean withinHorizontal = this.orientation == Orientation.LEFT_RIGHT
            && MathUtils.isWithinArea(mouseX, mouseY, getX(), getY(), this.info.scaledWidth, getHeight())
            || this.orientation == Orientation.RIGHT_LEFT && MathUtils.isWithinArea(mouseX, mouseY,
                getX() + getWidth() - this.info.scaledWidth, getY(), this.info.scaledWidth, getHeight());
        final boolean withinVertical = this.orientation == Orientation.TOP_BOTTOM
            && MathUtils.isWithinArea(mouseX, mouseY, getX(), getY(), getWidth(), this.info.scaledHeight)
            || this.orientation == Orientation.BOTTOM_TOP && MathUtils.isWithinArea(mouseX, mouseY, getX(),
                getY() + getHeight() - this.info.scaledHeight, getWidth(), this.info.scaledHeight);
        if (withinHorizontal || withinVertical) {
            this.minecraft.screen.renderComponentTooltip(stack, getTooltip(), mouseX, mouseY);
        }
    }
    
    public FluidInfo getInfo() {
        return this.info;
    }

    @Override
    public void renderWidget(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if(!this.visible)
            return;

        int mX0 = 0, mY0 = 0, mX1 = 0, mY1 = 0;
        int x0 = 0, y0 = 0, x1 = 0, y1 = 0;
        switch (this.orientation) {
            case LEFT_RIGHT -> {
                x0 = getX();
                y0 = getY();
                x1 = getX() + this.info.scaledWidth;
                y1 = getY() + getHeight();
                mX0 = getX();
                mY0 = getY();
                mX1 = getX() + getWidth();
                mY1 = getY() + getHeight();
            }
            case TOP_BOTTOM -> {
                x0 = getX();
                y0 = getY();
                x1 = getX() + getWidth();
                y1 = getY() + this.info.scaledHeight;
                mX0 = getX();
                mY0 = getY();
                mX1 = getX() + getWidth();
                mY1 = getY() + getHeight();
            }
            case RIGHT_LEFT -> {
                x0 = getX() + getWidth() - this.info.scaledWidth;
                y0 = getY();
                x1 = getX() + getWidth();
                y1 = getY() + getHeight();
                mX0 = getX() + getWidth();
                mY0 = getY();
                mX1 = getX();
                mY1 = getY() + getHeight();
            }
            case BOTTOM_TOP -> {
                x0 = getX();
                y0 = getY() + getHeight() - this.info.scaledHeight;
                x1 = getX() + getWidth();
                y1 = getY() + getHeight();
                mX0 = getX();
                mY0 = getY() + getHeight();
                mX1 = getX() + getWidth();
                mY1 = getY();
            }
        }

        this.isHovered = MathUtils.isWithinArea(mouseX, mouseY, x0, y0, x1 - x0, y1 - y0);

        if(this.info.getTexture() != null) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
            RenderSystem.setShaderColor(this.info.color.x(), this.info.color.y(), this.info.color.z(), this.info.color.w());
            RenderSystem.enableBlend();
            GuiUtils.renderLoopSprite(this.info.getTexture(), stack, x0, y0, x1, y1, this.info.loopX, this.info.loopY);
            RenderSystem.disableBlend();
        }

        if(this.drawBorder) {
            GuiUtils.drawOutline(stack, mX0, mY0, mX1, mY1, this.borderColour, 1);
        }
    }

    public void setFlowing(boolean flowing) {
        this.info.flowing = flowing;
        this.info.texture = ClientUtils.getBlock(
            this.info.flowing ? this.info.renderProps.getFlowingTexture() : this.info.renderProps.getStillTexture());
    }

    public void setMaximum(int max) {
        if(max > 0) {
            this.maximum = max;
        }
    }

    public void setOrientation(@NotNull Orientation orientation) {
        this.orientation = orientation;
    }

    public void setShouldDrawBorder(boolean drawBorder) {
        this.drawBorder = drawBorder;
    }

    public void setBorderColour(int colour) {
        this.borderColour = colour;
    }
    
    public void setFluid(FluidStack fluid) {
        this.info.fluidStack = fluid;
        this.info.attribs = fluid.getFluid().getFluidType();
        this.info.renderProps = IClientFluidTypeExtensions.of(this.info.fluidStack.getFluid());
        this.info.color = ClientUtils.ARGBtoRGBA(this.info.renderProps.getTintColor(fluid));
        setFlowing(this.info.flowing);
        this.info.scaledWidth = (int) (getWidth() / (this.maximum / (float) fluid.getAmount()));
        this.info.scaledHeight = (int) (getHeight() / (this.maximum / (float) fluid.getAmount()));
        final var fluidName = Component.translatable(fluid.getTranslationKey());
        final var amount = Component.literal(fluid.getAmount() + "/" + this.maximum);
        this.info.tooltip = List.of(fluidName, amount);
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput narration) {
        defaultButtonNarrationText(narration);
    }

    protected List<Component> getTooltip() {
        return this.info.tooltip;
    }

    @Deprecated(since = "1.19.3")
    public void renderToolTip(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        if(this.visible && this.isHovered) {
            if(this.info.tooltip != null && !this.info.tooltip.isEmpty()) {
                drawTooltip(pPoseStack, pMouseX, pMouseY);
            }
        }
    }

    public static class FluidInfo {
        private FluidType attribs;
        private FluidStack fluidStack;
        private IClientFluidTypeExtensions renderProps;
        private int loopX, loopY;
        private Vector4f color;
        private TextureAtlasSprite texture;
        private int scaledWidth, scaledHeight;
        private List<Component> tooltip;
        private boolean flowing;

        public FluidType getAttribs() {
            return this.attribs;
        }

        public Vector4f getColor() {
            return this.color;
        }

        public FluidStack getFluidStack() {
            return this.fluidStack;
        }

        public int getLoopX() {
            return this.loopX;
        }

        public int getLoopY() {
            return this.loopY;
        }

        public IClientFluidTypeExtensions getRenderProps() {
            return this.renderProps;
        }

        public int getScaledHeight() {
            return this.scaledHeight;
        }

        public int getScaledWidth() {
            return this.scaledWidth;
        }

        public TextureAtlasSprite getTexture() {
            return this.texture;
        }

        public List<Component> getTooltip() {
            return this.tooltip;
        }

        public boolean isFlowing() {
            return this.flowing;
        }

        public void setAttribs(FluidType attribs) {
            this.attribs = attribs;
        }

        public void setColor(Vector4f color) {
            this.color = color;
        }

        public void setFlowing(boolean flowing) {
            this.flowing = flowing;
        }

        public void setFluidStack(FluidStack fluidStack) {
            this.fluidStack = fluidStack;
        }

        public void setLoopX(int loopX) {
            this.loopX = loopX;
        }

        public void setLoopY(int loopY) {
            this.loopY = loopY;
        }

        public void setRenderProps(IClientFluidTypeExtensions renderProps) {
            this.renderProps = renderProps;
        }

        public void setScaledHeight(int scaledHeight) {
            this.scaledHeight = scaledHeight;
        }

        public void setScaledWidth(int scaledWidth) {
            this.scaledWidth = scaledWidth;
        }

        public void setTexture(TextureAtlasSprite texture) {
            this.texture = texture;
        }

        public void setTooltip(List<Component> tooltip) {
            this.tooltip = tooltip;
        }
    }

    public enum Orientation {
        TOP_BOTTOM, LEFT_RIGHT, BOTTOM_TOP, RIGHT_LEFT;
    }
}
