package me.bymartrixx.vtd.gui.widget;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.button.ButtonWidget;
import net.minecraft.text.Text;

public class ReloadButtonWidget extends ButtonWidget {
    private static final Text ICON = Text.literal("\u21BB"); // Clockwise arrow ↻

    public static final int BUTTON_SIZE = 20;

    public ReloadButtonWidget(int x, int y, Text message, PressAction onPress) {
        super(x, y, BUTTON_SIZE, BUTTON_SIZE, message, onPress, ButtonWidget.DEFAULT_NARRATION);
    }

    protected Text getIconText() {
        return ICON;
    }

    protected void drawScrollingText(GuiGraphics graphics, TextRenderer textRenderer, int xOffset, int color) {
        int scale = 2;
        graphics.getMatrices().pushMatrix();
        graphics.getMatrices().scale(scale, scale);
        int cx = (this.getX() + xOffset + this.getWidth() / 2) / scale;
        int cy = (this.getY() + (this.getHeight() - textRenderer.fontHeight) / 2) / scale;
        graphics.drawCenteredShadowedText(textRenderer, this.getIconText(), cx, cy, color);
        graphics.getMatrices().popMatrix();
    }

    @Override
    public void method_75752(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        // Bridge for mappings that use obfuscated method names for rendering
        drawScrollingText(graphics, MinecraftClient.getInstance().textRenderer, 0, 0xFFFFFFFF);
    }
}
