package dev.turtywurty.turtylib.client.util;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ImageInfo {
    public final Resource resource;
    public final NativeImage nativeImage;
    public final int width, height;
    public final boolean hasMetadata;
    public final Optional<AnimationData> animationData;

    // TODO: Metadata is a bit scuffed, fix it
    public ImageInfo(ResourceLocation textureLoc) {
        try {
            this.resource = ClientUtils.getResourceManager().getResource(textureLoc).get();
            this.nativeImage = NativeImage.read(this.resource.open());
            this.width = this.nativeImage.getWidth();
            this.height = this.nativeImage.getHeight();
            this.hasMetadata = this.resource.metadata() != null;
            if (this.hasMetadata) {
                this.animationData = Optional.of(
                    new AnimationData(this.resource.metadata().getSection(AnimationMetadataSection.SERIALIZER).get(),
                        this.width, this.height));
            } else {
                this.animationData = Optional.empty();
            }
        } catch (final IOException exception) {
            throw new IllegalStateException(
                "There was an issue reading the file or it's metadata at location: " + textureLoc.toString(),
                exception);
        }
    }
    
    public static final class AnimationData {
        public final AnimationMetadataSection metadata;
        public final int frameWidth, frameHeight, frameCount, frameTime;
        public final boolean isInterpolated;
        public final Map<Integer, Integer> frameRates;

        private AnimationData(AnimationMetadataSection metadata, int imgWidth, int imgHeight) {
            this.metadata = metadata;
            final FrameSize widthHeight = metadata.calculateFrameSize(imgWidth, imgHeight);
            this.frameWidth = widthHeight.width();
            this.frameHeight = widthHeight.height();
            this.frameCount = imgHeight / this.frameHeight;
            this.isInterpolated = metadata.isInterpolatedFrames();
            final Map<Integer, Integer> frameMap = new HashMap<>();
            metadata.forEachFrame(frameMap::put);
            this.frameRates = ImmutableMap.copyOf(frameMap);
            this.frameTime = metadata.getDefaultFrameTime();
        }
    }
}
