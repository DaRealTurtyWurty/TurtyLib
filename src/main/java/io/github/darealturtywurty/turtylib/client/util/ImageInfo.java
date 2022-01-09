package io.github.darealturtywurty.turtylib.client.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.PngInfo;
import com.mojang.datafixers.util.Pair;

import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

public class ImageInfo {
    public final Resource resource;
    public final PngInfo pngInfo;
    public final int width, height;
    public final boolean hasMetadata;
    public final Optional<AnimationData> animationData;
    
    public ImageInfo(ResourceLocation textureLoc) {
        try {
            this.resource = ClientUtils.getResourceManager().getResource(textureLoc);
            this.pngInfo = new PngInfo(this.resource.toString(), this.resource.getInputStream());
            this.width = this.pngInfo.width;
            this.height = this.pngInfo.height;
            this.hasMetadata = this.resource.hasMetadata();
            if (this.hasMetadata) {
                this.animationData = Optional.of(new AnimationData(
                        this.resource.getMetadata(AnimationMetadataSection.SERIALIZER), this.width, this.height));
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
            final Pair<Integer, Integer> widthHeight = metadata.getFrameSize(imgWidth, imgHeight);
            this.frameWidth = widthHeight.getFirst();
            this.frameHeight = widthHeight.getSecond();
            this.frameCount = imgHeight / this.frameHeight;
            this.isInterpolated = metadata.isInterpolatedFrames();
            final Map<Integer, Integer> frameMap = new HashMap<>();
            metadata.forEachFrame(frameMap::put);
            this.frameRates = ImmutableMap.copyOf(frameMap);
            this.frameTime = metadata.getDefaultFrameTime();
        }
    }
}
