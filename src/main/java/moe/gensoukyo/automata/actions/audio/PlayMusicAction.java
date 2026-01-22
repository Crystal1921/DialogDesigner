package moe.gensoukyo.automata.actions.audio;

import moe.gensoukyo.automata.actions.Action;
import moe.gensoukyo.automata.actions.EventType;

/**
 * 播放音乐事件
 */
public class PlayMusicAction extends Action {
    public final String musicUrl;

    public PlayMusicAction(int timeKey, String musicUrl) {
        super(EventType.PLAY_MUSIC, timeKey);
        this.musicUrl = musicUrl;
    }

    @Override
    public boolean isValid() {
        return musicUrl != null && !musicUrl.trim().isEmpty();
    }

    @Override
    public String getValidationError() {
        if (musicUrl == null || musicUrl.trim().isEmpty()) return "音乐URL不能为空";
        return "";
    }

    @Override
    public String getDisplayName() {
        return String.format("播放音乐: %s", musicUrl);
    }

    @Override
    public String toScriptString() {
        return String.format("{action=play_music %s}", musicUrl);
    }
}
