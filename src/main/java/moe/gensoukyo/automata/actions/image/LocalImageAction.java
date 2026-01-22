package moe.gensoukyo.automata.actions.image;

import moe.gensoukyo.automata.actions.Action;
import moe.gensoukyo.automata.actions.EventType;

/**
 * 预定义本地图片事件
 */
public class LocalImageAction extends Action {
    public final String imageName;
    public final String imagePath;

    public LocalImageAction(int timeKey, String imageName, String imagePath) {
        super(EventType.LOCAL_IMAGE, timeKey);
        this.imageName = imageName;
        this.imagePath = imagePath;
    }

    @Override
    public boolean isValid() {
        return imageName != null && !imageName.trim().isEmpty()
            && imagePath != null && !imagePath.trim().isEmpty();
    }

    @Override
    public String getValidationError() {
        if (imageName == null || imageName.trim().isEmpty()) return "图片名称不能为空";
        if (imagePath == null || imagePath.trim().isEmpty()) return "图片路径不能为空";
        return "";
    }

    @Override
    public String getDisplayName() {
        return String.format("本地图片: %s -> %s", imageName, imagePath);
    }

    @Override
    public String toScriptString() {
        return String.format("{action=local_image %s %s}",
            imageName, imagePath);
    }
}
