package moe.gensoukyo.automata.actions.audio;

import moe.gensoukyo.automata.actions.Action;
import moe.gensoukyo.automata.actions.EventType;

/**
 * 播放音效事件
 */
public class PlaySoundAction extends Action {
    public final String soundId;

    public PlaySoundAction(int timeKey, String soundId) {
        super(EventType.PLAY_SOUND, timeKey);
        this.soundId = soundId;
    }

    @Override
    public boolean isValid() {
        return soundId != null && !soundId.trim().isEmpty();
    }

    @Override
    public String getValidationError() {
        if (soundId == null || soundId.trim().isEmpty()) return "音效ID不能为空";
        return "";
    }

    @Override
    public String getDisplayName() {
        return String.format("播放音效: %s", soundId);
    }
}
