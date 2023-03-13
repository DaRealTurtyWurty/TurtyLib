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

    public GroupedTab(TabHolder screen, TabPage page, Orientation orientation, String name, int xPos, int yPos,
            Tab... subTabs) {
        super(screen, page, orientation, name, xPos, yPos);
        Collections.addAll(this.subTabs, subTabs);
        
        recalculateTabPositions();
    }
    
    public int getOffset() {
        return this.subTabs.size() * ((this.orientation.vertical ? this.height : this.width) + WHITESPACE);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        boolean tabsHovered = false;
        final int offset = getOffset();
        final int height = offset + 20 + WHITESPACE * this.subTabs.size();
        if (MathUtils.isWithinArea(mouseX, mouseY, this.x + this.width, this.subTabs.get(0).y, this.width, offset)) {
            tabsHovered = true;
        }
        
        if (tabsHovered || this.isHovered && this.active) {
            GuiUtils.drawQuadSplitTexture(stack, this.x + this.width / 2, this.y + this.height / 2 - height / 2,
                    this.width * 2, height, 256, 256);
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

            int x = this.x, y = this.y;
            switch (this.orientation) {
                case BOTTOM_TOP:
                    y += this.height;
                    x -= offset;
                    break;
                case LEFT_RIGHT:
                    x -= this.width;
                    y -= offset;
                    break;
                case RIGHT_LEFT:
                    x += this.width;
                    y -= offset;
                    break;
                case TOP_BOTTOM:
                    y -= this.height;
                    x -= offset;
                    break;
            }

            if (this.orientation.vertical) {
                tab.y = tabIndex++ * (this.height + WHITESPACE) + this.y - offset / 2 + this.height / 2 + 1;
                tab.x = x;
            } else {
                tab.x += tabIndex++ * (this.width + WHITESPACE) + this.x - offset / 2 + this.width / 2 + 1;
                tab.y = y;
            }
        }
    }
}
