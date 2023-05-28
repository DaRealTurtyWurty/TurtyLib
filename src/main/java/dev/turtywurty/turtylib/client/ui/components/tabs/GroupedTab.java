package dev.turtywurty.turtylib.client.ui.components.tabs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.turtywurty.turtylib.client.util.GuiUtils;
import dev.turtywurty.turtylib.core.util.MathUtils;

public class GroupedTab extends Tab {
    public static final int WHITESPACE = 2;
    private final List<Tab> subTabs = new ArrayList<>();

    public GroupedTab(TabHolder<?> screen, TabPage page, Orientation orientation, String name, int xPos, int yPos,
            Tab... subTabs) {
        super(screen, page, orientation, name, xPos, yPos);
        Collections.addAll(this.subTabs, subTabs);
        
        recalculateTabPositions();
    }
    
    public int getOffset() {
        return this.subTabs.size() * ((this.orientation.vertical ? getHeight() : getWidth()) + WHITESPACE);
    }

    @Override
    public void renderWidget(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        boolean tabsHovered = false;
        final int offset = getOffset();
        final int height = offset + 20 + WHITESPACE * this.subTabs.size();
        if (MathUtils.isWithinArea(mouseX, mouseY, getX() + getWidth(), this.subTabs.get(0).getY(), getWidth(), offset)) {
            tabsHovered = true;
        }
        
        if (tabsHovered || this.isHovered && this.active) {
            GuiUtils.drawQuadSplitTexture(stack, getX() + getWidth() / 2, getY() + getHeight() / 2 - height / 2,
                    getWidth() * 2, height, 256, 256);
        }
        
        super.render(stack, mouseX, mouseY, partialTicks);
        
        if (this.isHovered && this.active || tabsHovered) {
            for (final var tab : this.subTabs) {
                tab.render(stack, mouseX, mouseY, partialTicks);
                
                if (tab.isHoveredOrFocused()) {
                    this.tabHolder.getScreen().renderTooltip(stack, tab.getLabel(), mouseX, mouseY);
                }
            }
            
            if (!tabsHovered) {
                this.tabHolder.getScreen().renderTooltip(stack, getLabel(), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void onHovered(PoseStack stack, int mouseX, int mouseY) {

    }

    private void recalculateTabPositions() {
        final int offset = getOffset();
        int tabIndex = 0;
        for (final Tab tab : this.subTabs) {
            if (tab == null) {
                continue;
            }

            int x = getX(), y = getY();
            switch (this.orientation) {
                case BOTTOM_TOP -> {
                    y += getHeight();
                    x -= offset;
                }
                case LEFT_RIGHT -> {
                    x -= getWidth();
                    y -= offset;
                }
                case RIGHT_LEFT -> {
                    x += getWidth();
                    y -= offset;
                }
                case TOP_BOTTOM -> {
                    y -= getHeight();
                    x -= offset;
                }
            }

            if (this.orientation.vertical) {
                tab.setY(tabIndex++ * (getHeight() + WHITESPACE) + getY() - offset / 2 + getHeight() / 2 + 1);
                tab.setX(x);
            } else {
                tab.setX(tab.getX() + (tabIndex++ * (getWidth() + WHITESPACE) + getX() - offset / 2 + getWidth() / 2 + 1));
                tab.setY(y);
            }
        }
    }
}
