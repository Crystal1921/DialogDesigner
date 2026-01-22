package moe.gensoukyo.automata.actions.image;

import moe.gensoukyo.automata.actions.Action;
import moe.gensoukyo.automata.actions.EventType;

/**
 * 显示图片事件
 */
public class ImageShowAction extends Action {
    public final String imageName;
    public final float x, y;
    public final String width, height;

    public ImageShowAction(int timeKey, String imageName, float x, float y, String width, String height) {
        super(EventType.IMAGE_SHOW, timeKey);
        this.imageName = imageName;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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
        return String.format("显示图片: %s (%.1f, %.1f) [%sx%s]",
            imageName, x, y, width.isEmpty() ? "默认" : width, height.isEmpty() ? "默认" : height);
    }
}
