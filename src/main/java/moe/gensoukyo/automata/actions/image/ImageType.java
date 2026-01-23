package moe.gensoukyo.automata.actions.image;

/**
 * 图片预定义类型
 */
public enum ImageType {
    NETWORK("网络图片"),
    LOCAL("本地图片");

    private final String displayName;

    ImageType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
