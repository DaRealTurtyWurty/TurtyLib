package io.github.darealturtywurty.turtylib.book;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.PngInfo;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import io.github.darealturtywurty.turtylib.client.util.ClientUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public class AdvancedBookScreen extends Screen {
    protected final List<Page> pages = new ArrayList<>();
    protected int currentPage;
    protected int imageWidth, imageHeight, leftPageWidth, binderWidth, rightPageWidth;
    protected int leftPos, topPos;
    private final ResourceManager resourceManager;

    protected AdvancedBookScreen(Component title) {
        super(title);
        this.resourceManager = ClientUtils.getMinecraft().getResourceManager();
        this.imageWidth = 0;
        this.imageHeight = 0;
    }
    
    public final boolean isCover() {
        return this.currentPage == 0;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        renderPage(stack);
        renderContents(this.pages.get(this.currentPage), stack, mouseX, mouseY, partialTicks);
        if (!isCover()) {
            renderContents(this.pages.get(this.currentPage + 1), stack, mouseX, mouseY, partialTicks);
        }
    }

    public void renderBinder(PoseStack stack) {
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, this.pages.get(this.currentPage).binderTexture);
        blit(stack, this.leftPos + this.leftPageWidth, this.topPos, 0, 0, this.binderWidth, this.imageHeight);
    }
    
    public void renderContents(Page page, PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        for (final BookWidget widget : page.widgets) {
            widget.render(stack, mouseX, mouseY, partialTicks);
        }
    }

    public void renderPage(PoseStack stack) {
        renderPlainPage(stack, this.pages.get(this.currentPage));
        if (isCover())
            return;
        
        renderBinder(stack);
        renderPlainPage(stack, this.pages.get(this.currentPage + 1));
    }

    public void renderPlainPage(PoseStack stack, Page page) {
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, page.texture);
        if (this.currentPage % 2 == 1) {
            blit(stack, this.leftPos, this.topPos, 0, 0,
                isCover() ? this.imageWidth : this.imageWidth - this.leftPageWidth, this.imageHeight);
        } else {
            blit(stack, this.leftPos + this.leftPageWidth + this.binderWidth, this.topPos, this.leftPageWidth, 0,
                this.rightPageWidth, this.imageHeight);
        }
    }
    
    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        
        if (isCover()) {
            final ResourceLocation pageTexture = this.pages.get(this.currentPage).texture;
            try {
                final Resource resource = this.resourceManager.getResource(pageTexture).get();
                final var pngInfo = new PngInfo(resource::toString, resource.open());
                this.imageWidth = pngInfo.width;
                this.imageHeight = pngInfo.height;
            } catch (final IOException exception) {
                throw new IllegalStateException(
                    "There was an issue getting the information for resource: " + pageTexture, exception);
            }
            return;
        }

        final ResourceLocation leftPageTexture = this.pages.get(this.currentPage).texture;
        final ResourceLocation binderTexture = this.pages.get(this.currentPage).binderTexture;
        final ResourceLocation rightPageTexture = this.pages.get(this.currentPage + 1).texture;
        try {
            final Resource leftPageResource = this.resourceManager.getResource(leftPageTexture).get();
            final var leftPagePngInfo = new PngInfo(leftPageResource::toString, leftPageResource.open());
            this.leftPageWidth = leftPagePngInfo.width;

            final Resource binderResource = this.resourceManager.getResource(binderTexture).get();
            final var binderPngInfo = new PngInfo(binderResource::toString, binderResource.open());
            this.binderWidth = binderPngInfo.width;

            final Resource rightPageResource = this.resourceManager.getResource(rightPageTexture).get();
            final var rightPagePngInfo = new PngInfo(rightPageResource::toString, rightPageResource.open());
            this.rightPageWidth = rightPagePngInfo.width;
        } catch (final IOException exception) {
            throw new IllegalStateException("There was an issue getting the information for resource: "
                + leftPageTexture + " or " + binderTexture + " or " + rightPageTexture, exception);
        }
    }
}
