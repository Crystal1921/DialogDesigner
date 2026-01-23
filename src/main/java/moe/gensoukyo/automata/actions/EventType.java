package moe.gensoukyo.automata.actions;

import moe.gensoukyo.automata.actions.parameter.ParameterDefinition;
import moe.gensoukyo.automata.actions.parameter.ParameterType;

/**
 * 事件类型枚举
 * 定义所有支持的事件类型及其参数
 */
public enum EventType {
    // === 场景 ===
    SCENE_COLOR("场景颜色", "scene_color",
            new ParameterDefinition("颜色值", ParameterType.COLOR, "00000000", "16进制ARGB颜色"),
            new ParameterDefinition("淡入时间(tick)", ParameterType.INT, "0", "淡入时间")
    ),
    SCENE_COLOR_CLEAR("场景颜色清除", "scene_color_clear"),
    SCENE_TEXT("场景文字", "scene_text",
            new ParameterDefinition("文本内容", ParameterType.STRING, "", "要显示的文字"),
            new ParameterDefinition("持续时间(tick)", ParameterType.INT, "60", ""),
            new ParameterDefinition("X坐标", ParameterType.FLOAT, "0", ""),
            new ParameterDefinition("Y坐标", ParameterType.FLOAT, "0", ""),
            new ParameterDefinition("颜色", ParameterType.COLOR, "FFFFFF", "可选: 16进制RGB颜色"),
            new ParameterDefinition("淡入时间", ParameterType.INT, "0", "可选: 淡入时间(tick)"),
            new ParameterDefinition("淡出时间", ParameterType.INT, "0", "可选: 淡出时间(tick)")
    ),
    SCENE_TEXT_CLEAR("场景文字清除", "scene_text_clear"),
    // === 模型操作 ===
    MODEL_SHOW("显示模型", "model_show",
        new ParameterDefinition("模型标识符", ParameterType.STRING, "", "模型的唯一标识符"),
        new ParameterDefinition("模型ID", ParameterType.STRING, "", "模型的资源ID"),
        new ParameterDefinition("坐标/方位", ParameterType.COORDINATES, "", "X,Y,Z坐标")
    ),
    MODEL_HIDE("隐藏模型", "model_hide",
        new ParameterDefinition("模型标识符", ParameterType.STRING, "", "要隐藏的模型标识符")
    ),
    MODEL_POS("模型坐标", "model_pos",
        new ParameterDefinition("模型标识符", ParameterType.STRING, "", "模型的唯一标识符"),
        new ParameterDefinition("坐标/方位", ParameterType.COORDINATES, "", "X,Y,Z坐标")
    ),
    MODEL_MOTION("执行动作", "model_motion",
        new ParameterDefinition("模型标识符", ParameterType.STRING, "", "模型的唯一标识符"),
        new ParameterDefinition("动作名称", ParameterType.STRING, "", "要执行的动作"),
        new ParameterDefinition("循环播放", ParameterType.BOOLEAN, "false", "true=循环, false=单次")
    ),
    MODEL_SCALE("模型缩放", "model_scale",
        new ParameterDefinition("模型标识符", ParameterType.STRING, "", "模型的唯一标识符"),
        new ParameterDefinition("缩放比例", ParameterType.FLOAT, "1.0", "默认为1.0")
    ),
    MODEL_LIGHT("模型光照", "model_light",
        new ParameterDefinition("模型标识符", ParameterType.STRING, "", "模型的唯一标识符"),
        new ParameterDefinition("光照等级", ParameterType.INT, "15", "MC原版光照等级 0-15")
    ),
    MODEL_FOLLOW("模型跟随", "model_follow",
        new ParameterDefinition("模型标识符", ParameterType.STRING, "", "模型的唯一标识符"),
        new ParameterDefinition("启用跟随", ParameterType.BOOLEAN, "false", "是否跟随玩家")
    ),
    MODEL_COLOR("模型颜色", "model_color",
        new ParameterDefinition("模型标识符", ParameterType.STRING, "", "模型的唯一标识符"),
        new ParameterDefinition("颜色值", ParameterType.COLOR, "FFFFFFFF", "16进制ARGB颜色")
    ),
    MODEL_PAT("模型拍拍", "model_pat",
        new ParameterDefinition("模型标识符", ParameterType.STRING, "", "要拍拍的模型标识符")
    ),

    // === 图片操作 ===
    LOCAL_IMAGE("预定义本地图片", "local_image",
        new ParameterDefinition("图片名称", ParameterType.STRING, "", "图片的唯一标识符"),
        new ParameterDefinition("图片路径", ParameterType.STRING, "", "ResourceLocation路径")
    ),
    IMAGE_SHOW("显示图片", "image_show",
        new ParameterDefinition("图片名称", ParameterType.STRING, "", "图片标识符"),
        new ParameterDefinition("X坐标", ParameterType.FLOAT, "0", ""),
        new ParameterDefinition("Y坐标", ParameterType.FLOAT, "0", ""),
        new ParameterDefinition("宽度", ParameterType.STRING, "", "留空=默认256, 'full'=全屏, 或指定数值"),
        new ParameterDefinition("高度", ParameterType.STRING, "", "留空=默认256, 或指定数值")
    ),
    IMAGE_HIDE("隐藏图片", "image_hide",
        new ParameterDefinition("图片名称", ParameterType.STRING, "", "要隐藏的图片标识符")
    ),
    IMAGE_MOVE("移动图片", "image_move",
        new ParameterDefinition("图片名称", ParameterType.STRING, "", "图片标识符"),
        new ParameterDefinition("X坐标", ParameterType.FLOAT, "0", ""),
        new ParameterDefinition("Y坐标", ParameterType.FLOAT, "0", "")
    ),
    IMAGE_CLEAR("清除所有图", "image_clear"),

    // === 音乐/音效 ===
    PLAY_SOUND("播放音效", "play_sound",
        new ParameterDefinition("音效ID", ParameterType.STRING, "", "例如: minecraft:block.note_block.pling")
    ),
    PLAY_MUSIC("播放音乐", "play_music",
        new ParameterDefinition("音乐URL", ParameterType.STRING, "", "音乐的URL地址")
    ),
    STOP_MUSIC("停止音乐", "stop_music"),

    // === 文字 ===
    TEXT_SPEED("文字速度", "text_speed",
        new ParameterDefinition("速度", ParameterType.INT, "1", "每多少tick播放一个字符")
    ),
    TEXT_POS("文本框位置", "text_pos",
        new ParameterDefinition("X坐标", ParameterType.FLOAT, "0", ""),
        new ParameterDefinition("Y坐标", ParameterType.FLOAT, "0", "")
    ),
    TEXT_COLOR("文本框背景", "text_color",
        new ParameterDefinition("颜色", ParameterType.COLOR, "", "16进制RGB/ARGB颜色")
    ),

    // === 震屏 ===
    SHAKE("震动", "shake",
        new ParameterDefinition("初始强度", ParameterType.FLOAT, "1.0", ""),
        new ParameterDefinition("衰减速度", ParameterType.FLOAT, "0.1", "每tick衰减的值")
    );

    private final String displayName;
    private final String command;
    private final java.util.List<ParameterDefinition> parameters;

    EventType(String displayName, String command, ParameterDefinition... parameters) {
        this.displayName = displayName;
        this.command = command;
        this.parameters = java.util.List.of(parameters);
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCommand() {
        return command;
    }

    public java.util.List<ParameterDefinition> getParameters() {
        return parameters;
    }
}
