package moe.gensoukyo.automata.actions.image;

import moe.gensoukyo.automata.actions.Action;

/**
 * 图片操作的抽象基类
 * 所有涉及图片名称的Action都应该继承此类
 */
public abstract class ImageAction extends Action {
    public ImageAction(moe.gensoukyo.automata.actions.EventType type, int timeKey) {
        super(type, timeKey);
    }

    /**
     * 获取此操作涉及的图片名称
     * @return 图片名称，如果不涉及特定图片则返回null
     */
    public abstract String getImageName();
}
