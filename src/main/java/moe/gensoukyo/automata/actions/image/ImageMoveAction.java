package moe.gensoukyo.automata.actions.image;

import moe.gensoukyo.automata.actions.EventType;

/**
 * 移动图片事件
 */
public class ImageMoveAction extends ImageAction {
    public final String imageName;
    public final float x, y;

    public ImageMoveAction(int timeKey, String imageName, float x, float y) {
        super(EventType.IMAGE_MOVE, timeKey);
        this.imageName = imageName;
        this.x = x;
        this.y = y;
    }

    @Override
    public String getImageName() {
        return imageName;
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
        return String.format("移动图片: %s (%.1f, %.1f)", imageName, x, y);
    }

    @Override
    public String toScriptString() {
        return String.format("{action=image_move %s %.1f %.1f}",
            imageName, x, y);
    }
}
