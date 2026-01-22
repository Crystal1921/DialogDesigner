package moe.gensoukyo.automata.actions.model;

import moe.gensoukyo.automata.actions.Action;
import moe.gensoukyo.automata.actions.EventType;

/**
 * 模型缩放事件
 */
public class ModelScaleAction extends Action {
    public final String modelId;
    public final float scale;

    public ModelScaleAction(int timeKey, String modelId, float scale) {
        super(EventType.MODEL_SCALE, timeKey);
        this.modelId = modelId;
        this.scale = scale;
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
        return String.format("模型缩放: %s (%.2fx)", modelId, scale);
    }

    @Override
    public String toScriptString() {
        return String.format("{action=model_scale %s %.2f}",
            modelId, scale);
    }
}
