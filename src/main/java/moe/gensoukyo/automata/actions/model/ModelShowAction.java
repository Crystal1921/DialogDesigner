package moe.gensoukyo.automata.actions.model;

import moe.gensoukyo.automata.actions.Action;
import moe.gensoukyo.automata.actions.EventType;

/**
 * 显示模型事件
 */
public class ModelShowAction extends Action {
    public final String modelId;
    public final String resourceId;
    public final float x, y, z;

    public ModelShowAction(int timeKey, String modelId, String resourceId, float x, float y, float z) {
        super(EventType.MODEL_SHOW, timeKey);
        this.modelId = modelId;
        this.resourceId = resourceId;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean isValid() {
        return modelId != null && !modelId.trim().isEmpty()
            && resourceId != null && !resourceId.trim().isEmpty();
    }

    @Override
    public String getValidationError() {
        if (modelId == null || modelId.trim().isEmpty()) return "模型标识符不能为空";
        if (resourceId == null || resourceId.trim().isEmpty()) return "模型ID不能为空";
        return "";
    }

    @Override
    public String getDisplayName() {
        return String.format("显示模型: %s (%.2f, %.2f, %.2f)", modelId, x, y, z);
    }

    @Override
    public String toScriptString() {
        return String.format("{action=model_show %s %s %.2f %.2f %.2f}",
            modelId, resourceId, x, y, z);
    }
}
