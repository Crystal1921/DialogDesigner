package moe.gensoukyo.automata.actions.model;

import moe.gensoukyo.automata.actions.Action;
import moe.gensoukyo.automata.actions.EventType;

/**
 * 模型跟随事件
 */
public class ModelFollowAction extends Action {
    public final String modelId;
    public final boolean follow;

    public ModelFollowAction(int timeKey, String modelId, boolean follow) {
        super(EventType.MODEL_FOLLOW, timeKey);
        this.modelId = modelId;
        this.follow = follow;
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
        return String.format("模型跟随: %s (%s)", modelId, follow ? "启用" : "禁用");
    }

    @Override
    public String toScriptString() {
        return String.format("{action=model_follow %s %b}",
            modelId, follow);
    }
}
