package io.github.darealturtywurty.turtylib.book;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.mojang.blaze3d.vertex.PoseStack;

import io.github.darealturtywurty.turtylib.client.util.MathUtils;
import net.minecraft.network.chat.Component;

public abstract class BookWidget {
    public final Set<PagePosition> allowedPositions = new HashSet<>();
    private PagePosition position = PagePosition.TOP_LEFT;
    public final int width, height;
    private boolean visible;
    private final Page page;

    protected BookWidget(int width, int height) {
        this.width = width;
        this.height = height;
        this.page = new Page(Component.empty());
    }

    public int calculateRelativeX(AdvancedBookScreen screen) {
        if (screen.isCover())
            return (int) (screen.leftPos + this.position.x * screen.imageWidth);

        // left page
        int mappedX = 0;
        if (screen.pages.get(screen.currentPage).equals(this.page)) {
            mappedX = MathUtils.mapToInt(this.position.x, 0, 1, 0, screen.leftPageWidth);
        }
        
        // right page
        else {
            mappedX = MathUtils.mapToInt(this.position.x, 0, 1, screen.imageWidth - screen.rightPageWidth,
                screen.imageWidth);
        }
        
        return screen.leftPos + mappedX;
    }

    public int calculateRelativeY(AdvancedBookScreen screen) {
        final int mappedY = MathUtils.mapToInt(this.position.y, 0, 1, 0, screen.imageHeight);
        return screen.topPos + mappedY;
    }
    
    public PagePosition getPosition() {
        return this.position;
    }

    public boolean isMouseOver(int xPos, int yPos, int mouseX, int mouseY) {
        return isVisible() && isMouseOverArea(xPos, yPos, mouseX, mouseY);
    }
    
    public boolean isMouseOverArea(int xPos, int yPos, int mouseX, int mouseY) {
        return mouseX >= xPos && mouseY >= yPos && mouseX <= xPos + this.width && mouseY <= yPos + this.height;
    }
    
    public boolean isVisible() {
        return this.visible;
    }

    public abstract void render(PoseStack stack, int mouseX, int mouseY, float partialTicks);

    public final BookWidget setAllowedPositions(boolean replace, PagePosition... positions) {
        if (replace) {
            this.allowedPositions.clear();
        }

        Collections.addAll(this.allowedPositions, positions);
        return this;
    }
    
    public final BookWidget setAllowedPositions(PagePosition... positions) {
        return setAllowedPositions(true, positions);
    }

    public BookWidget setPosition(PagePosition position) {
        if (this.allowedPositions.contains(position)) {
            this.position = position;
        }
        
        return this;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
