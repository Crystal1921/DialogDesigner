package moe.gensoukyo.automata.actions.model;

import moe.gensoukyo.automata.actions.Action;
import moe.gensoukyo.automata.actions.EventType;

/**
 * 模型坐标事件
 */
public class ModelPosAction extends Action {
    public final String modelId;
    public final float x, y, z;

    public ModelPosAction(int timeKey, String modelId, float x, float y, float z) {
        super(EventType.MODEL_POS, timeKey);
        this.modelId = modelId;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean isValid() {
        return modelId != null && !modelId.trim().isEmpty();
    }

    @Override
    public String getValidationError() {
        if (modelId == null || modelId.trim().isEmpty()) return "模型标识符不能为空";
        return "";
    }

    @Override
    public String getDisplayName() {
        return String.format("模型坐标: %s (%.2f, %.2f, %.2f)", modelId, x, y, z);
    }

    @Override
    public String toScriptString() {
        return String.format("{action=model_pos %s %.2f %.2f %.2f}",
            modelId, x, y, z);
    }
}
