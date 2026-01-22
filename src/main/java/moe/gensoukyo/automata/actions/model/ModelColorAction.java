package moe.gensoukyo.automata.actions.model;

import moe.gensoukyo.automata.actions.Action;
import moe.gensoukyo.automata.actions.EventType;

/**
 * 模型颜色事件
 */
public class ModelColorAction extends Action {
    public final String modelId;
    public final float[] color;

    public ModelColorAction(int timeKey, String modelId, float[] color) {
        super(EventType.MODEL_COLOR, timeKey);
        this.modelId = modelId;
        this.color = color;
    }

    @Override
    public boolean isValid() {
        return modelId != null && !modelId.trim().isEmpty() && color != null;
    }

    @Override
    public String getValidationError() {
        if (modelId == null || modelId.trim().isEmpty()) return "模型标识符不能为空";
        return "";
    }

    @Override
    public String getDisplayName() {
        return String.format("模型颜色: %s (#%02X%02X%02X%02X)",
            modelId,
            (int)(color[0] * 255),
            (int)(color[1] * 255),
            (int)(color[2] * 255),
            (int)(color[3] * 255));
    }

    @Override
    public String toScriptString() {
        // 转换为16进制ARGB格式
        String colorHex = String.format("%02X%02X%02X%02X",
            (int)(color[3] * 255), // Alpha
            (int)(color[0] * 255), // Red
            (int)(color[1] * 255), // Green
            (int)(color[2] * 255)); // Blue
        return String.format("{action=model_color %s %s}",
            modelId, colorHex);
    }
}
