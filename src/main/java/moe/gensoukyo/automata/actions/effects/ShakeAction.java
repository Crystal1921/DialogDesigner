package moe.gensoukyo.automata.actions.effects;

import moe.gensoukyo.automata.actions.Action;
import moe.gensoukyo.automata.actions.EventType;

/**
 * 震屏事件
 */
public class ShakeAction extends Action {
    public final float initialStrength;
    public final float decayRate;

    public ShakeAction(int timeKey, float initialStrength, float decayRate) {
        super(EventType.SHAKE, timeKey);
        this.initialStrength = initialStrength;
        this.decayRate = decayRate;
    }

    @Override
    public boolean isValid() {
        return initialStrength >= 0 && decayRate >= 0;
    }

    @Override
    public String getValidationError() {
        if (initialStrength < 0) return "初始强度不能为负数";
        if (decayRate < 0) return "衰减速度不能为负数";
        return "";
    }

    @Override
    public String getDisplayName() {
        return String.format("震屏: 强度%.2f 衰减%.2f", initialStrength, decayRate);
    }
}
