package dev.turtywurty.turtylib.client.ui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.turtywurty.turtylib.client.util.ClientUtils;
import dev.turtywurty.turtylib.client.util.GuiUtils;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;

import java.util.function.IntConsumer;

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

    public NumberInput(int xPos, int yPos, int width, int height, int didgetCount, int min, int max, int defaultVal, Component name) {
        super(ClientUtils.getFont(), xPos, yPos, width, height, name);
        min = Math.min(min, max);
        if (min == max) {
            min = max - 1;
        }

        defaultVal = defaultVal >= max ? max : Math.max(defaultVal, min);

        setCanLoseFocus(true);
        setMaxLength(didgetCount);
        final int minimum = min, maximum = max;
        setFilter(str -> StringUtils.isNumeric(str) && Integer.parseInt(str) >= minimum && Integer.parseInt(str) <= maximum);
        setBordered(false);
        setTextColor(-1);
        setTextColorUneditable(-1);
        setValue(String.valueOf(defaultVal));
        setVisible(false);
    }

    public String getUnits() {
        return this.units;
    }

    @Override
    public void renderWidget(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.renderWidget(stack, mouseX, mouseY, partialTicks);
        if (this.units != null && !this.units.isBlank()) {
            ClientUtils.getFont().drawShadow(stack, this.units, getX() + ClientUtils.getFont().width(getValue()) + 1, getY(), 0xFFFFFF);
        }

        GuiUtils.drawLine(getX(), getY() + 10, getX() + ClientUtils.getFont().width(getValue()) + ClientUtils.getFont().width(getUnits()) + 5, getY() + 10, 64, 64, 64, 255, 3);
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
