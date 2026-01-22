package moe.gensoukyo.automata.actions.image;

import moe.gensoukyo.automata.actions.Action;
import moe.gensoukyo.automata.actions.EventType;

/**
 * 清除所有图片事件
 */
public class ImageClearAction extends Action {
    public ImageClearAction(int timeKey) {
        super(EventType.IMAGE_CLEAR, timeKey);
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
        return "清除所有图片";
    }

    @Override
    public String toScriptString() {
        return "{action=image_clear}";
    }
}
