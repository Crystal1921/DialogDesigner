package moe.gensoukyo.automata;

import imgui.*;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.type.ImInt;
import imgui.type.ImString;
import imgui.type.ImFloat;
import imgui.type.ImBoolean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    private static final float PIXELS_PER_SECOND_BASE = 100f;
    private static final float ZOOM_SENSITIVITY = 0.1f;
    private static final float CULLING_MARGIN = 50f;
    private static final float MIN_TIME_STEP = 0.05f;
    private final float[] sceneColor = new float[]{1f, 1f, 1f, 1f};
    // 事件类型选择
    private final ImInt selectedEventTypeIndex = new ImInt(0);
    // 参数值存储
    private final List<ParameterValue> parameterValues = new ArrayList<>();
    private float timelineOffset = 0f;
    private float timelineZoom = 1f;
    private float currentTime = 0f;
    // 右键弹窗相关状态
    private boolean showRightClickPopup = false;
    private float rightClickTime = 0f;

    public static void main(String[] args) {
        launch(new Main());
    }

    @Override
    protected void configure(Configuration config) {
        config.setTitle("Dear ImGui is Awesome!");
    }

    @Override
    protected void init(Configuration config) {
        super.init(config);

        ImGuiIO io = ImGui.getIO();
        ImFontAtlas fonts = io.getFonts();

        // 获取简体中文字符集
        short[] chineseRange = fonts.getGlyphRangesChineseSimplifiedCommon();

        // 创建字体配置
        ImFontConfig fontConfig = new ImFontConfig();
        fontConfig.setPixelSnapH(true);

        // 从资源加载中文字体
        try {
            byte[] fontBytes = getClass().getResourceAsStream("/font/NotoSansSC-Medium.ttf").readAllBytes();
            ImFont font = fonts.addFontFromMemoryTTF(fontBytes, 18, fontConfig, chineseRange);
            io.setFontDefault(font);
        } catch (IOException e) {
            System.err.println("Failed to load font: " + e.getMessage());
            e.printStackTrace();
        }

        fontConfig.destroy();
    }

    @Override
    public void process() {

        ImGui.text("拖动滑动并可缩放的时间条演示");
        ImGui.colorEdit4("scene_color", sceneColor);
        ImGui.text(String.format("当前时间: %.2fs (最小刻度 %.2fs)", currentTime, MIN_TIME_STEP));
        drawTimeline();
        drawRightClickPopup();
    }

    private void drawTimeline() {
        float availableWidth = ImGui.getContentRegionAvailX();
        float timelineHeight = 80f;

        ImGui.text(String.format("缩放倍数: %.2fx  偏移: %.1f px", timelineZoom, timelineOffset));

        ImGui.invisibleButton("timeline_area", availableWidth, timelineHeight);
        float timelineStartX = ImGui.getItemRectMinX();
        float timelineStartY = ImGui.getItemRectMinY();

        if (ImGui.isItemHovered()) {
            float wheel = ImGui.getIO().getMouseWheel();
            if (wheel != 0f) {
                float mouseX = ImGui.getIO().getMousePosX();
                float world = (mouseX - timelineStartX - timelineOffset) / (PIXELS_PER_SECOND_BASE * timelineZoom);
                float newZoom = timelineZoom * (1f + wheel * ZOOM_SENSITIVITY);
                timelineZoom = Math.max(0.2f, Math.min(5f, newZoom));
                timelineOffset = mouseX - timelineStartX - world * PIXELS_PER_SECOND_BASE * timelineZoom;
                // 限制 offset，确保时间线最小为0
                timelineOffset = Math.min(0, timelineOffset);
            }
        }

        if (ImGui.isItemActive() && ImGui.isMouseDragging(0)) {
            timelineOffset += ImGui.getIO().getMouseDeltaX();
            // 限制 offset，确保时间线最小为0
            timelineOffset = Math.min(0, timelineOffset);
        }

        if (ImGui.isItemHovered() && ImGui.isMouseClicked(0)) {
            float mouseX = ImGui.getIO().getMousePosX();
            float worldSeconds = (mouseX - timelineStartX - timelineOffset) / (PIXELS_PER_SECOND_BASE * timelineZoom);
            worldSeconds = Math.max(0f, worldSeconds);
            float snapped = Math.round(worldSeconds / MIN_TIME_STEP) * MIN_TIME_STEP;
            currentTime = snapped;
        }

        // 右键点击检测
        if (ImGui.isItemHovered() && ImGui.isMouseClicked(1)) {
            float mouseX = ImGui.getIO().getMousePosX();
            float worldSeconds = (mouseX - timelineStartX - timelineOffset) / (PIXELS_PER_SECOND_BASE * timelineZoom);
            worldSeconds = Math.max(0f, worldSeconds);
            float snapped = Math.round(worldSeconds / MIN_TIME_STEP) * MIN_TIME_STEP;
            rightClickTime = snapped;
            showRightClickPopup = true;
            ImGui.openPopup("Event Popup");
        }

        var drawList = ImGui.getWindowDrawList();
        float pixelsPerSecond = PIXELS_PER_SECOND_BASE * timelineZoom;
        float visibleSeconds = (availableWidth / pixelsPerSecond) + 2f;
        int startSecond = (int) Math.floor((-timelineOffset) / pixelsPerSecond) - 1;
        int endSecond = startSecond + (int) visibleSeconds + 3;

        for (int s = startSecond; s <= endSecond; s++) {
            float x = timelineStartX + timelineOffset + s * pixelsPerSecond;
            if (x < timelineStartX - CULLING_MARGIN || x > timelineStartX + availableWidth + CULLING_MARGIN) {
                continue;
            }
            float tickHeight = (s % 5 == 0) ? 30f : 18f;
            drawList.addLine(x, timelineStartY, x, timelineStartY + tickHeight, 0xFFAAAAAA);
            if (s % 5 == 0) {
                drawList.addText(x + 4f, timelineStartY + tickHeight + 2f, 0xFFFFFFFF, s + "s");
            }
        }

        float playheadX = timelineStartX + timelineOffset + currentTime * pixelsPerSecond;
        drawList.addLine(playheadX, timelineStartY, playheadX, timelineStartY + timelineHeight, 0xFF66CCFF, 2f);

        drawList.addRect(timelineStartX, timelineStartY, timelineStartX + availableWidth, timelineStartY + timelineHeight, 0x80FFFFFF);
    }

    private void drawRightClickPopup() {
        if (ImGui.beginPopup("Event Popup")) {
            ImGui.text("添加事件");
            ImGui.separator();
            ImGui.text(String.format("时间: %.2fs", rightClickTime));

            // 事件类型选择
            ImGui.text("事件类型:");
            EventType[] eventTypes = EventType.values();
            String[] eventTypeNames = new String[eventTypes.length];
            for (int i = 0; i < eventTypes.length; i++) {
                eventTypeNames[i] = eventTypes[i].getDisplayName();
            }

            int previousIndex = selectedEventTypeIndex.get();
            if (ImGui.combo("##event_type", selectedEventTypeIndex, eventTypeNames)) {
                // 切换事件类型时重新初始化参数
                if (previousIndex != selectedEventTypeIndex.get()) {
                    initParameterValues(eventTypes[selectedEventTypeIndex.get()]);
                }
            }

            ImGui.separator();

            // 根据选择的事件类型显示相应的参数输入控件
            EventType currentEventType = eventTypes[selectedEventTypeIndex.get()];
            List<ParameterDefinition> params = currentEventType.getParameters();

            ImGui.text("事件参数:");
            for (int i = 0; i < params.size(); i++) {
                ParameterDefinition param = params.get(i);
                // 确保有足够的ParameterValue
                ensureParameterCapacity(i);
                ParameterValue paramValue = parameterValues.get(i);
                renderParameterInput(param, paramValue, i);
            }

            ImGui.separator();
            if (ImGui.button("确定")) {
                EventType selectedType = eventTypes[selectedEventTypeIndex.get()];
                System.out.println("添加事件: " + selectedType.getCommand());
                System.out.println("时间: " + rightClickTime + "s");
                for (int i = 0; i < parameterValues.size() && i < selectedType.getParameters().size(); i++) {
                    ParameterDefinition param = selectedType.getParameters().get(i);
                    ParameterValue value = parameterValues.get(i);
                    System.out.println("  " + param.name() + ": " + getValueAsString(value, param.type()));
                }

                ImGui.closeCurrentPopup();
                showRightClickPopup = false;
            }
            ImGui.sameLine();
            if (ImGui.button("取消")) {
                ImGui.closeCurrentPopup();
                showRightClickPopup = false;
            }

            ImGui.endPopup();
        }
    }

    private void initParameterValues(EventType eventType) {
        parameterValues.clear();
        for (ParameterDefinition param : eventType.getParameters()) {
            parameterValues.add(ParameterValue.create(param.type(), param.defaultValue()));
        }
    }

    private void ensureParameterCapacity(int index) {
        while (parameterValues.size() <= index) {
            parameterValues.add(new ParameterValue());
        }
    }

    private void renderParameterInput(ParameterDefinition param, ParameterValue value, int index) {
        String label = param.name() + "##param_" + index;
        switch (param.type()) {
            case STRING -> ImGui.inputText(label, value.stringValue);
            case INT -> ImGui.inputInt(label, value.intValue);
            case FLOAT -> ImGui.inputFloat(label, value.floatValue);
            case BOOLEAN -> ImGui.checkbox(label, value.booleanValue);
            case COLOR -> ImGui.colorEdit4(label, value.colorValue);
            case COORDINATES -> {
                ImGui.text(param.name());
                ImGui.pushID(index);
                ImFloat tempX = new ImFloat(value.coordX);
                ImFloat tempY = new ImFloat(value.coordY);
                ImFloat tempZ = new ImFloat(value.coordZ);
                ImGui.text("X:"); ImGui.sameLine();
                if (ImGui.inputFloat("##coord_x", tempX)) value.coordX = tempX.get();
                ImGui.sameLine();
                ImGui.text("Y:"); ImGui.sameLine();
                if (ImGui.inputFloat("##coord_y", tempY)) value.coordY = tempY.get();
                ImGui.sameLine();
                ImGui.text("Z:"); ImGui.sameLine();
                if (ImGui.inputFloat("##coord_z", tempZ)) value.coordZ = tempZ.get();
                ImGui.popID();
            }
        }
        if (param.description() != null && !param.description().isEmpty()) {
            ImGui.sameLine();
            ImGui.textDisabled("(?)");
            if (ImGui.isItemHovered()) {
                ImGui.setTooltip(param.description());
            }
        }
    }

    private String getValueAsString(ParameterValue value, ParameterType type) {
        return switch (type) {
            case STRING -> value.stringValue.get();
            case INT -> String.valueOf(value.intValue.get());
            case FLOAT -> String.valueOf(value.floatValue.get());
            case BOOLEAN -> String.valueOf(value.booleanValue.get());
            case COLOR -> String.format("#%02X%02X%02X%02X",
                (int)(value.colorValue[0] * 255),
                (int)(value.colorValue[1] * 255),
                (int)(value.colorValue[2] * 255),
                (int)(value.colorValue[3] * 255));
            case COORDINATES -> String.format("(%.2f, %.2f, %.2f)", value.coordX, value.coordY, value.coordZ);
        };
    }

    // 参数类型枚举
    private enum ParameterType {
        STRING, INT, FLOAT, BOOLEAN, COLOR, COORDINATES
    }

    // 参数定义记录类
    private record ParameterDefinition(
        String name,
        ParameterType type,
        String defaultValue,
        String description
    ) {}

    // 参数值存储类
    private static class ParameterValue {
        ImString stringValue = new ImString(256);
        ImInt intValue = new ImInt(0);
        ImFloat floatValue = new ImFloat(0f);
        ImBoolean booleanValue = new ImBoolean(false);
        float[] colorValue = new float[]{1f, 1f, 1f, 1f};
        float coordX = 0f, coordY = 0f, coordZ = 0f;

        static ParameterValue create(ParameterType type, String defaultValue) {
            ParameterValue v = new ParameterValue();
            if (defaultValue != null && !defaultValue.isEmpty()) {
                switch (type) {
                    case STRING -> v.stringValue.set(defaultValue);
                    case INT -> v.intValue.set(Integer.parseInt(defaultValue));
                    case FLOAT -> v.floatValue.set(Float.parseFloat(defaultValue));
                    case BOOLEAN -> v.booleanValue.set(Boolean.parseBoolean(defaultValue));
                }
            }
            return v;
        }
    }

    // 事件类型定义
    private enum EventType {
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

        // === 场景 ===
        SCENE_COLOR("场景颜色", "scene_color",
            new ParameterDefinition("颜色值", ParameterType.COLOR, "00000000", "16进制ARGB颜色"),
            new ParameterDefinition("持续时间(tick)", ParameterType.INT, "0", "淡入时间")
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

        // === 文字 ===
        TEXT_SPEED("文字速度", "text_speed",
            new ParameterDefinition("速度", ParameterType.INT, "1", "每多少tick播放一个字符")
        ),
        TEXT_POS("文本框位置", "text_pos",
            new ParameterDefinition("X坐标", ParameterType.FLOAT, "0", ""),
            new ParameterDefinition("Y坐标", ParameterType.FLOAT, "0", "")
        ),
        TEXT_COLOR("文本框背景", "text_color",
            new ParameterDefinition("颜色值", ParameterType.STRING, "", "16进制RGB/ARGB颜色")
        ),

        // === 震屏 ===
        SHAKE("震屏", "shake",
            new ParameterDefinition("初始强度", ParameterType.FLOAT, "1.0", ""),
            new ParameterDefinition("衰减速度", ParameterType.FLOAT, "0.1", "每tick衰减的值")
        );

        private final String displayName;
        private final String command;
        private final List<ParameterDefinition> parameters;

        EventType(String displayName, String command, ParameterDefinition... parameters) {
            this.displayName = displayName;
            this.command = command;
            this.parameters = List.of(parameters);
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getCommand() {
            return command;
        }

        public List<ParameterDefinition> getParameters() {
            return parameters;
        }
    }
}
