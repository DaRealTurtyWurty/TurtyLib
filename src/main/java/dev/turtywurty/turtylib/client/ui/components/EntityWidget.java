package dev.turtywurty.turtylib.client.ui.components;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.turtywurty.turtylib.client.util.GuiUtils;
import dev.turtywurty.turtylib.core.util.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EntityWidget extends AbstractWidget {
    private final Minecraft minecraft;
    private Entity entity;
    private float rotation = 135;
    private float rotationSpeed;
    private Vec3 defaultRotation, scale, offset;
    
    private EntityWidget(Builder builder) {
        super(builder.xPos, builder.yPos, builder.width, builder.height, Component.empty());
        
        this.minecraft = Minecraft.getInstance();
        this.entity = builder.entity;
        this.rotationSpeed = builder.rotationSpeed;
        this.defaultRotation = builder.defaultRotation;
        this.scale = builder.scale;
        this.offset = builder.offset;
    }
    
    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if(!this.visible)
            return;

        this.isHovered = MathUtils.isWithinArea(mouseX, mouseY, this.x, this.y, this.width, this.height);

        if (this.rotationSpeed != 0) {
            this.rotation += partialTicks * this.rotationSpeed;
        }
        
        this.entity.tick();
        
        GuiUtils.renderEntity(stack, this.entity,
            this.rotationSpeed != 0 ? new Vec3(this.defaultRotation.x(), this.rotation, this.defaultRotation.z())
                : this.defaultRotation,
            this.scale, this.offset, this.x, this.y, partialTicks);
    }
    
    @Override
    public void updateNarration(NarrationElementOutput narration) {
        defaultButtonNarrationText(narration);
    }
    
    public static class Builder {
        private float rotationSpeed;
        private Vec3 scale = new Vec3(20f, 20f, 20f), defaultRotation = new Vec3(15, 135, 0),
            offset = new Vec3(-1.25f, -1.75f, 0);
        private Entity entity;
        private int xPos, yPos, width, height;
        
        public Builder(Entity entity, int xPos, int yPos, int width, int height) {
            this.entity = entity;
            this.xPos = xPos;
            this.yPos = yPos;
            this.width = width;
            this.height = height;
        }
        
        public EntityWidget build() {
            return new EntityWidget(this);
        }
        
        public Builder defaultRotation(float x, float y, float z) {
            this.defaultRotation = new Vec3(x, y, z);
            return this;
        }
        
        public Builder offset(float x, float y, float z) {
            this.offset = new Vec3(-x, y, z);
            return this;
        }
        
        public Builder rotationSpeed(float rotationSpeed) {
            this.rotationSpeed = rotationSpeed;
            return this;
        }
        
        public Builder scale(float x, float y, float z) {
            this.scale = new Vec3(x, y, z);
            return this;
        }
    }
}
