package moe.gensoukyo.automata.actions.text;

import moe.gensoukyo.automata.actions.Action;
import moe.gensoukyo.automata.actions.EventType;

/**
 * 文本框背景事件
 */
public class TextColorAction extends Action {
    public final String color;

    public TextColorAction(int timeKey, String color) {
        super(EventType.TEXT_COLOR, timeKey);
        this.color = color;
    }

    @Override
    public boolean isValid() {
        return color != null && !color.trim().isEmpty();
    }

    @Override
    public String getValidationError() {
        if (color == null || color.trim().isEmpty()) return "颜色值不能为空";
        return "";
    }

    @Override
    public String getDisplayName() {
        return String.format("文本框背景: %s", color);
    }

    @Override
    public String toScriptString() {
        return String.format("{action=text_color %s}", color);
    }
}
