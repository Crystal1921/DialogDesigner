package moe.gensoukyo.automata.actions.audio;

import moe.gensoukyo.automata.actions.Action;
import moe.gensoukyo.automata.actions.EventType;

/**
 * 停止音乐事件
 */
public class StopMusicAction extends Action {
    public StopMusicAction(int timeKey) {
        super(EventType.STOP_MUSIC, timeKey);
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
        return "停止音乐";
    }
}
