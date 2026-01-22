package moe.gensoukyo.automata.actions.model;

import moe.gensoukyo.automata.actions.Action;
import moe.gensoukyo.automata.actions.EventType;

/**
 * 模型光照事件
 */
public class ModelLightAction extends Action {
    public final String modelId;
    public final int lightLevel;

    public ModelLightAction(int timeKey, String modelId, int lightLevel) {
        super(EventType.MODEL_LIGHT, timeKey);
        this.modelId = modelId;
        this.lightLevel = lightLevel;
    }

    @Override
    public boolean isValid() {
        return modelId != null && !modelId.trim().isEmpty()
            && lightLevel >= 0 && lightLevel <= 15;
    }

    @Override
    public String getValidationError() {
        if (modelId == null || modelId.trim().isEmpty()) return "模型标识符不能为空";
        if (lightLevel < 0 || lightLevel > 15) return "光照等级必须在0-15之间";
        return "";
    }

    @Override
    public String getDisplayName() {
        return String.format("模型光照: %s (%d)", modelId, lightLevel);
    }
}
