package moe.gensoukyo.automata.actions.text;

import moe.gensoukyo.automata.actions.Action;
import moe.gensoukyo.automata.actions.EventType;

/**
 * 文本框背景事件
 */
public class TextColorAction extends Action {
    public final float[] color;

    public TextColorAction(int timeKey, float[] color) {
        super(EventType.TEXT_COLOR, timeKey);
        this.color = color;
    }

    @Override
    public boolean isValid() {
        return color != null;
    }

    @Override
    public String getValidationError() {
        return "";
    }

    @Override
    public String getDisplayName() {
        return String.format("文本颜色: #%02X%02X%02X%02X",
                (int)(color[0] * 255), // Red
                (int)(color[1] * 255), // Green
                (int)(color[2] * 255), // Blue
                (int)(color[3] * 255)); // Alpha
    }

    @Override
    public String toScriptString() {
        // 转换为16进制ARGB格式
        String colorHex = String.format("%02X%02X%02X%02X",
                (int)(color[3] * 255), // Alpha
                (int)(color[0] * 255), // Red
                (int)(color[1] * 255), // Green
                (int)(color[2] * 255)); // Blue
        return String.format("{action=text_color %s}", colorHex);
    }
}
