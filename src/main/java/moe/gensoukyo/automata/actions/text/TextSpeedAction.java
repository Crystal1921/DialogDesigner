package moe.gensoukyo.automata.actions.text;

import moe.gensoukyo.automata.actions.Action;
import moe.gensoukyo.automata.actions.EventType;

/**
 * 文字速度事件
 */
public class TextSpeedAction extends Action {
    public final int speed;

    public TextSpeedAction(int timeKey, int speed) {
        super(EventType.TEXT_SPEED, timeKey);
        this.speed = speed;
    }

    @Override
    public boolean isValid() {
        return speed > 0;
    }

    @Override
    public String getValidationError() {
        if (speed <= 0) return "速度必须大于0";
        return "";
    }

    @Override
    public String getDisplayName() {
        return String.format("文字速度: %d tick/字符", speed);
    }
}
