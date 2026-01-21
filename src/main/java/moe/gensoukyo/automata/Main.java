package moe.gensoukyo.automata;

import imgui.*;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.type.ImInt;
import imgui.type.ImString;

import java.io.IOException;

public class Main extends Application {
    private static final float PIXELS_PER_SECOND_BASE = 100f;
    private static final float ZOOM_SENSITIVITY = 0.1f;
    private static final float CULLING_MARGIN = 50f;
    private static final float MIN_TIME_STEP = 0.05f;
    private final float[] sceneColor = new float[]{1f, 1f, 1f, 1f};
    private final float[] eventColor = new float[]{1f, 1f, 1f, 1f};
    // 事件类型选择（默认为null，表示未选择）
    private final ImInt selectedEventTypeIndex = new ImInt(0);
    private final ImString[] eventInputs1 = new ImString[]{new ImString("")};
    private final ImString[] eventInputs2 = new ImString[]{new ImString(""), new ImString("")};
    private final ImString[] eventInputs3 = new ImString[]{new ImString(""), new ImString(""), new ImString("")};
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
                // 切换事件类型时清空输入框
                if (previousIndex != selectedEventTypeIndex.get()) {
                    clearEventInputs();
                }
            }

            ImGui.separator();

            // 根据选择的事件类型显示相应数量的输入框
            EventType currentEventType = eventTypes[selectedEventTypeIndex.get()];
            int inputCount = currentEventType.getInputCount();

            ImGui.text("事件参数:");
            ImString[] currentInputs = getEventInputsArray(inputCount);

            for (int i = 0; i < inputCount; i++) {
                ImGui.inputText("参数 " + (i + 1) + "##input_" + i, currentInputs[i]);
            }

            ImGui.separator();
            ImGui.text("事件颜色:");
            ImGui.colorEdit4("##event_color", eventColor);

            ImGui.separator();
            if (ImGui.button("确定")) {
                // TODO: 根据选择的事件类型和输入参数添加事件
                EventType selectedType = eventTypes[selectedEventTypeIndex.get()];
                ImString[] inputs = getEventInputsArray(selectedType.getInputCount());

                System.out.println("添加事件: " + selectedType.getDisplayName());
                System.out.println("时间: " + rightClickTime + "s");
                for (int i = 0; i < inputs.length; i++) {
                    System.out.println("参数 " + (i + 1) + ": " + inputs[i].get());
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

    private void clearEventInputs() {
        for (ImString imString : eventInputs1) imString.set("");
        for (ImString imString : eventInputs2) imString.set("");
        for (ImString imString : eventInputs3) imString.set("");
    }

    private ImString[] getEventInputsArray(int inputCount) {
        return switch (inputCount) {
            case 1 -> eventInputs1;
            case 2 -> eventInputs2;
            case 3 -> eventInputs3;
            default -> eventInputs2;
        };
    }

    // 事件类型定义
    private enum EventType {
        TWO_INPUTS("双参数事件", 2),
        THREE_INPUTS("三参数事件", 3);

        private final String displayName;
        private final int inputCount;

        EventType(String displayName, int inputCount) {
            this.displayName = displayName;
            this.inputCount = inputCount;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getInputCount() {
            return inputCount;
        }
    }
}
