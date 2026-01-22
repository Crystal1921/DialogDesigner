package moe.gensoukyo.automata.actions.model;

import moe.gensoukyo.automata.actions.Action;
import moe.gensoukyo.automata.actions.EventType;

/**
 * 隐藏模型事件
 */
public class ModelHideAction extends Action {
    public final String modelId;

    public ModelHideAction(int timeKey, String modelId) {
        super(EventType.MODEL_HIDE, timeKey);
        this.modelId = modelId;
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
        return String.format("隐藏模型: %s", modelId);
    }
}
