package moe.gensoukyo.automata.actions.scene;

import moe.gensoukyo.automata.actions.Action;
import moe.gensoukyo.automata.actions.EventType;

/**
 * 场景文字事件
 */
public class SceneTextAction extends Action {
    public final String text;
    public final int duration;
    public final float x, y;
    public final float[] color;
    public final int fadeIn, fadeOut;

    public SceneTextAction(int timeKey, String text, int duration, float x, float y,
                           float[] color, int fadeIn, int fadeOut) {
        super(EventType.SCENE_TEXT, timeKey);
        this.text = text;
        this.duration = duration;
        this.x = x;
        this.y = y;
        this.color = color;
        this.fadeIn = fadeIn;
        this.fadeOut = fadeOut;
    }

    @Override
    public boolean isValid() {
        return text != null && !text.trim().isEmpty();
    }

    @Override
    public String getValidationError() {
        if (text == null || text.trim().isEmpty()) return "文本内容不能为空";
        return "";
    }

    @Override
    public String getDisplayName() {
        return String.format("场景文字: %s (%.1f, %.1f)", text.length() > 20 ? text.substring(0, 20) + "..." : text, x, y);
    }

    @Override
    public String toScriptString() {
        // 转换颜色为16进制RGB格式（不带Alpha）
        String colorHex = String.format("%02X%02X%02X",
            (int)(color[0] * 255), // Red
            (int)(color[1] * 255), // Green
            (int)(color[2] * 255)); // Blue

        // 根据可选参数的值选择正确的格式
        if (fadeIn == 0 && fadeOut == 0) {
            // scene_text <text> <tick> <posX> <posY> [color]
            return String.format("{action=scene_text %s %d %.1f %.1f %s}",
                text, duration, x, y, colorHex);
        } else {
            // scene_text <text> <tick> <posX> <posY> [color] [fadeIn] [fadeOut]
            return String.format("{action=scene_text %s %d %.1f %.1f %s %d %d}",
                text, duration, x, y, colorHex, fadeIn, fadeOut);
        }
    }
}
