package moe.gensoukyo.automata.actions.model;

import moe.gensoukyo.automata.actions.Action;
import moe.gensoukyo.automata.actions.EventType;

/**
 * 执行动作事件
 */
public class ModelMotionAction extends Action {
    public final String modelId;
    public final String motionName;
    public final boolean loop;

    public ModelMotionAction(int timeKey, String modelId, String motionName, boolean loop) {
        super(EventType.MODEL_MOTION, timeKey);
        this.modelId = modelId;
        this.motionName = motionName;
        this.loop = loop;
    }

    @Override
    public boolean isValid() {
        return modelId != null && !modelId.trim().isEmpty()
            && motionName != null && !motionName.trim().isEmpty();
    }

    @Override
    public String getValidationError() {
        if (modelId == null || modelId.trim().isEmpty()) return "模型标识符不能为空";
        if (motionName == null || motionName.trim().isEmpty()) return "动作名称不能为空";
        return "";
    }

    @Override
    public String getDisplayName() {
        return String.format("执行动作: %s - %s (%s)",
            modelId, motionName, loop ? "循环" : "单次");
    }
}
