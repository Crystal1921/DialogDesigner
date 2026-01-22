package moe.gensoukyo.automata.actions.scene;

import moe.gensoukyo.automata.actions.Action;
import moe.gensoukyo.automata.actions.EventType;

/**
 * 清除场景颜色事件
 */
public class SceneColorClearAction extends Action {
    public SceneColorClearAction(int timeKey) {
        super(EventType.SCENE_COLOR_CLEAR, timeKey);
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
        return "清除场景颜色";
    }
}
