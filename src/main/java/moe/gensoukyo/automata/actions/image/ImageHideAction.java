package moe.gensoukyo.automata.actions.image;

import moe.gensoukyo.automata.actions.Action;
import moe.gensoukyo.automata.actions.EventType;

/**
 * 隐藏图片事件
 */
public class ImageHideAction extends Action {
    public final String imageName;

    public ImageHideAction(int timeKey, String imageName) {
        super(EventType.IMAGE_HIDE, timeKey);
        this.imageName = imageName;
    }

    @Override
    public boolean isValid() {
        return imageName != null && !imageName.trim().isEmpty();
    }

    @Override
    public String getValidationError() {
        if (imageName == null || imageName.trim().isEmpty()) return "图片名称不能为空";
        return "";
    }

    @Override
    public String getDisplayName() {
        return String.format("隐藏图片: %s", imageName);
    }
}
