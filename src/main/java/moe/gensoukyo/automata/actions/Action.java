package moe.gensoukyo.automata.actions;

/**
 * Action基类
 * 所有事件数据类的基类
 */
public abstract class Action {
    protected final EventType type;
    protected final int timeKey; // 时间键值(秒*100)

    protected Action(EventType type, int timeKey) {
        this.type = type;
        this.timeKey = timeKey;
    }

    public EventType getType() {
        return type;
    }

    public int getTimeKey() {
        return timeKey;
    }

    public float getTime() {
        return timeKey / 100f;
    }

    /**
     * 验证参数是否有效
     */
    public abstract boolean isValid();

    /**
     * 获取验证错误信息
     */
    public abstract String getValidationError();

    /**
     * 获取显示名称
     */
    public abstract String getDisplayName();
}
