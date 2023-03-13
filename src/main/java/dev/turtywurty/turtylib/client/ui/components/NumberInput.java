package dev.turtywurty.turtylib.client.ui.components;

import java.util.function.IntConsumer;

import dev.turtywurty.turtylib.client.util.ClientUtils;
import dev.turtywurty.turtylib.client.util.GuiUtils;
import org.apache.commons.lang3.StringUtils;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class NumberInput extends EditBox {
    public String units = "";
    
    public NumberInput(int xPos, int yPos, int width, int height, Component name) {
        this(xPos, yPos, width, height, 3, 0, 100, 100, name);
    }
    
    public NumberInput(int xPos, int yPos, int width, int height, int max, Component name) {
        this(xPos, yPos, width, height, 3, 0, max, max, name);
    }
    
    public NumberInput(int xPos, int yPos, int width, int height, int min, int max, Component name) {
        this(xPos, yPos, width, height, 3, min, max, max, name);
    }
    
    public NumberInput(int xPos, int yPos, int width, int height, int didgetCount, int min, int max, int defaultVal,
            Component name) {
        super(ClientUtils.getFont(), xPos, yPos, width, height, name);
        min = min < max ? min : max;
        max = max > min ? max : min;
        if (min == max) {
            min = max - 1;
        }
        defaultVal = defaultVal >= max ? max : defaultVal <= min ? min : defaultVal;
        
        setCanLoseFocus(true);
        setMaxLength(didgetCount);
        final int minimum = min, maximum = max;
        setFilter(str -> StringUtils.isNumeric(str) && Integer.parseInt(str) >= minimum
                && Integer.parseInt(str) <= maximum);
        setBordered(false);
        setTextColor(-1);
        setTextColorUneditable(-1);
        setValue(defaultVal + "");
        setVisible(false);
    }
    
    public String getUnits() {
        return this.units;
    }
    
    @Override
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.renderButton(stack, mouseX, mouseY, partialTicks);
        if (this.units != null && !this.units.isBlank()) {
            ClientUtils.getFont().drawShadow(stack, this.units, this.x + ClientUtils.getFont().width(getValue()) + 1,
                    this.y, 0xFFFFFF);
        }
        
        GuiUtils.drawLine(this.x, this.y + 10,
                this.x + ClientUtils.getFont().width(getValue()) + ClientUtils.getFont().width(getUnits()) + 5,
                this.y + 10, 64, 64, 64, 255, 3);
    }
    
    public void setOnChanged(IntConsumer onChanged) {
        setResponder(str -> onChanged.accept(Integer.parseInt(str)));
    }
    
    public void setUnits(String units) {
        if (units != null) {
            this.units = units;
        }
    }
}
