package moe.gensoukyo.automata.actions.scene;

import moe.gensoukyo.automata.actions.Action;
import moe.gensoukyo.automata.actions.EventType;

/**
 * 场景颜色事件
 */
public class SceneColorAction extends Action {
    public final float[] color;
    public final int duration;

    public SceneColorAction(int timeKey, float[] color, int duration) {
        super(EventType.SCENE_COLOR, timeKey);
        this.color = color;
        this.duration = duration;
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
        return String.format("场景颜色: #%02X%02X%02X%02X (%dtick)",
            (int)(color[0] * 255),
            (int)(color[1] * 255),
            (int)(color[2] * 255),
            (int)(color[3] * 255),
            duration);
    }
}
