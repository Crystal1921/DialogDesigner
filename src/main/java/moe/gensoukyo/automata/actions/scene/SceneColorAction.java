package moe.gensoukyo.automata.actions.scene;

import moe.gensoukyo.automata.actions.Action;
import moe.gensoukyo.automata.actions.EventType;

/**
 * 场景颜色事件
 */
public class SceneColorAction extends Action {
    public final float[] color;
    public int fadeInTime;

    public SceneColorAction(int timeKey, float[] color, int fadeInTime) {
        super(EventType.SCENE_COLOR, timeKey);
        this.color = color;
        this.fadeInTime = fadeInTime;
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
            fadeInTime);
    }

    @Override
    public String toScriptString() {
        // 转换为16进制ARGB格式
        String colorHex = String.format("%02X%02X%02X%02X",
            (int)(color[3] * 255), // Alpha
            (int)(color[0] * 255), // Red
            (int)(color[1] * 255), // Green
            (int)(color[2] * 255)); // Blue
        return String.format("{action=scene_color %s}",
            colorHex);
    }
}
