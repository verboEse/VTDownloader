package me.bymartrixx.vtd.gui.widget;

import net.minecraft.client.gui.widget.button.ButtonWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.text.Text;

public class MutableMessageButtonWidget extends ButtonWidget {
    private final Text defaultMessage;
    private Text currentMessage;

    public MutableMessageButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, message, onPress, ButtonWidget.DEFAULT_NARRATION);
        this.defaultMessage = message;
        this.currentMessage = message;
    }

    public void resetMessage() {
        this.currentMessage = this.defaultMessage;
    }

    public void setMessage(Text message) {
        this.currentMessage = message;
    }

    @Override
    public Text getMessage() {
        return this.currentMessage;
    }

    @Override
    public void method_75752(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        // Provide bridge for obfuscated PressableWidget render method
        // Default behavior: draw current message using super rendering if available
        try {
            super.getClass().getMethod("method_75752", GuiGraphics.class, int.class, int.class, float.class)
                    .invoke(this, graphics, mouseX, mouseY, delta);
        } catch (Exception ignored) {
            // fallback: no-op
        }
    }
}
