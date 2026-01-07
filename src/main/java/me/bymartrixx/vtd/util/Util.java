package me.bymartrixx.vtd.util;

import me.bymartrixx.vtd.VTDMod;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ArgbHelper;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.lang.reflect.Method;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.render.RenderPipelines;
import net.minecraft.util.Identifier;

public class Util {
    public static final int VTD_BUTTON_WIDTH = 120;
    public static final int VTD_BUTTON_CENTER_X = VTD_BUTTON_WIDTH / 2;
    public static final int VTD_BUTTON_HEIGHT = 20;
    public static final int VTD_BUTTON_BOTTOM_MARGIN = 24;

    /**
     * Parse an 0xAARRGGBB color from `rgba(red, green, blue, alpha)`
     */
    public static int parseColor(String color) {
        String format = color.substring(0, color.indexOf("("));
        if (color.endsWith(")")) {
            List<String> components = Arrays.stream(color.substring(color.indexOf("(") + 1, color.length() - 1)
                    .split(",")).map(String::trim).toList();

            if (format.equals("rgba")) {
                if (components.size() == 4) {
                    int red = Integer.parseInt(components.get(0));
                    int green = Integer.parseInt(components.get(1));
                    int blue = Integer.parseInt(components.get(2));
                    float alpha = Float.parseFloat(components.get(3));

                    return ArgbHelper.pack((int) (alpha * 255), red, green, blue);
                }
            }
        }

        VTDMod.LOGGER.warn("Unknown color format: {}", color);
        return 0x00000000;
    }

    public static String removeHtmlTags(String text) {
        // Remove html tags
        return StringUtils.normalizeSpace(text.replaceAll("(?!<br>)<[^>]*>", " "))
                .replaceAll("<br>", "\n"); // Replace <br> after normalizing to keep new lines
    }

    public static Text urlText(String url) {
        MutableText t = Text.literal(url)
                .formatted(Formatting.UNDERLINE, Formatting.ITALIC, Formatting.BLUE);
        try {
            URI uri = net.minecraft.util.Util.createUri(url);
            t.styled(s -> s.withClickEvent(new ClickEvent.OpenUrl(uri)));
        } catch (URISyntaxException ignored) {
        }

        return t;
    }

    @Nullable
    public static Style getStyleAt(TextRenderer textRenderer, int centerX, double mouseX, Text text) {
        int width = textRenderer.getWidth(text);
        int startX = centerX - width / 2;
        int endX = startX + width;
 
        return mouseX >= startX && mouseX < endX ?
            invokeGetStyle(textRenderer, text, (int) mouseX - startX) : null;
    }

    @Nullable
    public static Style getStyleAt(TextRenderer textRenderer, int centerX, double mouseX, OrderedText text) {
        int width = textRenderer.getWidth(text);
        int startX = centerX - width / 2;
        int endX = startX + width;
 
        return mouseX >= startX && mouseX < endX ?
                invokeGetStyle(textRenderer, text, (int) mouseX - startX) : null;
    }

    private static Style invokeGetStyle(TextRenderer textRenderer, Object textObj, int index) {
        Object handler = textRenderer.getTextHandler();
        try {
            // Try to find any getStyleAt method and invoke it reflectively
            for (Method m : handler.getClass().getMethods()) {
                if (!m.getName().equals("getStyleAt")) continue;
                Class<?>[] params = m.getParameterTypes();
                if (params.length == 2) {
                    try {
                        Object res = m.invoke(handler, textObj, index);
                        if (res instanceof Style) return (Style) res;
                    } catch (IllegalArgumentException ignored) {
                        // parameter types didn't match; try next
                    }
                }
            }
        } catch (Exception e) {
            // fallback to null
        }

        return null;
    }

    public static void drawMultilineText(MultilineText text, GuiGraphics graphics, int centerX, int y, int lineHeight, boolean shadow, int color) {
        try {
            // Try to find method_73212 reflectively and invoke it. There are different signatures across mappings.
            Method[] methods = MultilineText.class.getMethods();
            for (Method m : methods) {
                if (!m.getName().equals("method_73212")) continue;
                Class<?>[] params = m.getParameterTypes();
                Object[] args;
                if (params.length == 7) {
                    // (GuiGraphics, Enum, int, int, int, boolean, int)
                    args = new Object[]{graphics, null, centerX, y, lineHeight, shadow, color};
                } else if (params.length == 6) {
                    // (GuiGraphics, int, int, int, boolean, int)
                    args = new Object[]{graphics, centerX, y, lineHeight, shadow, color};
                } else {
                    continue;
                }

                try {
                    m.invoke(text, args);
                    return;
                } catch (IllegalArgumentException ignored) {
                    // try next
                }
            }
        } catch (Exception ignored) {
        }
    }

    public static List<OrderedText> getMultilineTextLines(TextRenderer textRenderer, Text text, int maxLines, int width) {
        return textRenderer.wrapLines(text, width).stream()
                .limit(maxLines)
                .toList();
    }

    public static MultilineText createMultilineText(TextRenderer textRenderer, Text text, int maxLines, int width) {
        return MultilineText.create(textRenderer, width, maxLines, text);
    }

    public static MultilineText createMultilineText(TextRenderer textRenderer, List<Text> lines, int maxLines) {
        if (lines.size() > maxLines) {
            lines = lines.subList(0, maxLines);
        }

        return MultilineText.create(textRenderer, lines.toArray(new Text[0]));
    }

    public static List<Text> wrapText(TextRenderer textRenderer, String text, int maxWidth) {
        TextHandler textHandler = textRenderer.getTextHandler();
        List<StringVisitable> visitableLines = textHandler.wrapLines(text, maxWidth, Style.EMPTY);
        return visitableLines.stream().map(StringVisitable::getString).map(Text::of).toList();
    }

    public static void drawTexture(GuiGraphics graphics, Identifier id, int x, int y, int u, int v, int w, int h) {
        try {
            Method[] methods = graphics.getClass().getDeclaredMethods();
            for (Method m : methods) {
                if (!m.getName().equals("drawTexture")) continue;
                Class<?>[] params = m.getParameterTypes();
                Object[] args = new Object[params.length];

                int intIndex = 0;
                int[] intPool = new int[]{x, y, u, v, w, h, 0};
                int floatIndex = 0;
                float[] floatPool = new float[]{(float) u, (float) v, (float) w, (float) h};

                boolean ok = true;
                for (int i = 0; i < params.length; i++) {
                    Class<?> p = params[i];
                    String pname = p.getName();
                    if (pname.contains("RenderPipeline")) {
                        try {
                            Class<?> rpClass = Class.forName("net.minecraft.client.render.RenderPipelines");
                            try {
                                args[i] = rpClass.getField("GUI_TEXTURED").get(null);
                            } catch (NoSuchFieldException e) {
                                // try method
                                for (Method rm : rpClass.getMethods()) {
                                    if (rm.getParameterCount() == 0 && rm.getReturnType().getName().contains("RenderPipeline")) {
                                        args[i] = rm.invoke(null);
                                        break;
                                    }
                                }
                            }
                        } catch (Exception ignored) {
                            args[i] = null;
                        }
                    } else if (p == Identifier.class) {
                        args[i] = id;
                    } else if (p == int.class) {
                        if (intIndex < intPool.length) {
                            args[i] = intPool[intIndex++];
                        } else {
                            args[i] = 0;
                        }
                    } else if (p == float.class) {
                        if (floatIndex < floatPool.length) {
                            args[i] = floatPool[floatIndex++];
                        } else {
                            args[i] = 0.0f;
                        }
                    } else {
                        args[i] = null;
                    }
                }

                try {
                    m.setAccessible(true);
                    m.invoke(graphics, args);
                    return;
                } catch (Throwable ignored) {
                    // try next
                }
            }
        } catch (Exception ignored) {
        }
    }
}
