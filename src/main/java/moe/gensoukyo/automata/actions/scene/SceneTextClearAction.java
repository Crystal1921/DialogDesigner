package moe.gensoukyo.automata.actions.scene;

import moe.gensoukyo.automata.actions.Action;
import moe.gensoukyo.automata.actions.EventType;

/**
 * 清除场景文字事件
 */
public class SceneTextClearAction extends Action {
    public SceneTextClearAction(int timeKey) {
        super(EventType.SCENE_TEXT_CLEAR, timeKey);
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
        return "清除场景文字";
    }

    @Override
    public String toScriptString() {
        return "{action=scene_text_clear}";
    }
}
