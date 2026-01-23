package moe.gensoukyo.automata.actions.image;

/**
 * 图片预定义类
 */
public class ImageDefinition {
    public ImageType type;
    public String name;
    public String path; // URL或ResourceLocation路径

    public ImageDefinition(ImageType type, String name, String path) {
        this.type = type;
        this.name = name;
        this.path = path;
    }

    /**
     * 生成脚本字符串
     */
    public String toScriptString() {
        String command = type == ImageType.NETWORK ? "network_image" : "local_image";
        return String.format("%s %s %s", command, name, path);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s -> %s",
            type.getDisplayName(), name, path);
    }
}
