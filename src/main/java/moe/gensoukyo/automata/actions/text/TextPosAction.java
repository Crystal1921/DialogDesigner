package moe.gensoukyo.automata.actions.text;

import moe.gensoukyo.automata.actions.Action;
import moe.gensoukyo.automata.actions.EventType;

/**
 * 文本框位置事件
 */
public class TextPosAction extends Action {
    public final float x, y;

    public TextPosAction(int timeKey, float x, float y) {
        super(EventType.TEXT_POS, timeKey);
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public String getValidationError() {
        return "";
    }

    @Override
    public String getDisplayName() {
        return String.format("文本框位置: (%.1f, %.1f)", x, y);
    }
}
