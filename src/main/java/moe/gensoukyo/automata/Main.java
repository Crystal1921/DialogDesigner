package moe.gensoukyo.automata;

import imgui.*;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.flag.ImGuiComboFlags;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import imgui.type.ImString;
import moe.gensoukyo.automata.actions.Action;
import moe.gensoukyo.automata.actions.ActionFactory;
import moe.gensoukyo.automata.actions.EventType;
import moe.gensoukyo.automata.actions.image.ImageDefinition;
import moe.gensoukyo.automata.actions.image.ImageType;
import moe.gensoukyo.automata.actions.image.ImageShowAction;
import moe.gensoukyo.automata.actions.image.ImageHideAction;
import moe.gensoukyo.automata.actions.image.ImageMoveAction;
import moe.gensoukyo.automata.actions.parameter.ParameterDefinition;
import moe.gensoukyo.automata.actions.parameter.ParameterType;
import moe.gensoukyo.automata.actions.parameter.ParameterValue;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
    // 事件数据存储: 时间(秒*100) -> 事件列表
    private final Map<Integer, List<Action>> eventData = new TreeMap<>();
    // 图片预定义存储
    private final List<ImageDefinition> imageDefinitions = new ArrayList<>();
    // 新增图片预定义时的临时值
    private final ImInt newImageType = new ImInt(0);
    private final ImString newImageName = new ImString(128);
    private final ImString newImagePath = new ImString(512);
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
        config.setTitle("Cinematic Action");
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

        // 导出按钮
        if (ImGui.button("导出脚本")) {
            exportScript();
        }
        ImGui.sameLine();
        if (eventData.isEmpty()) {
            ImGui.textDisabled("(暂无事件可导出)");
        } else {
            ImGui.textDisabled("(共 " + eventData.size() + " 个时间点, " +
                    eventData.values().stream().mapToInt(List::size).sum() + " 个事件)");
        }

        drawImageDefinitions();
        drawTimeline();
        drawRightClickPopup();
        drawEventList();
    }

    private void drawImageDefinitions() {
        ImGui.separator();

        if (ImGui.collapsingHeader("图片预定义管理")) {
            // 显示当前预定义数量
            ImGui.textDisabled("已预定义 " + imageDefinitions.size() + " 个图片");
            ImGui.sameLine();
            ImGui.textDisabled("(这些预定义将在脚本开头输出)");

            // 添加新预定义的区域
            ImGui.separator();
            ImGui.text("添加新预定义:");

            // 类型选择
            ImGui.text("类型:");
            ImGui.sameLine();
            String[] types = {"网络图片", "本地图片"};
            ImGui.combo("##image_type", newImageType, types);

            // 名称输入
            ImGui.text("名称(唯一标识):");
            ImGui.inputText("##image_name", newImageName);

            // 路径输入
            String pathLabel = newImageType.get() == 0 ? "URL:" : "ResourceLocation路径:";
            ImGui.text(pathLabel);
            ImGui.inputText("##image_path", newImagePath);

            // 添加按钮
            if (ImGui.button("添加预定义##add_image_def")) {
                String name = newImageName.get().trim();
                String path = newImagePath.get().trim();

                if (name.isEmpty()) {
                    ImGui.textColored(1.0f, 0.0f, 0.0f, 1.0f, "错误: 名称不能为空");
                } else if (path.isEmpty()) {
                    ImGui.textColored(1.0f, 0.0f, 0.0f, 1.0f, "错误: 路径不能为空");
                } else if (imageDefinitions.stream().anyMatch(def -> def.name.equals(name))) {
                    ImGui.textColored(1.0f, 0.0f, 0.0f, 1.0f, "错误: 名称 '" + name + "' 已存在");
                } else {
                    ImageType type = newImageType.get() == 0 ? ImageType.NETWORK : ImageType.LOCAL;
                    imageDefinitions.add(new ImageDefinition(type, name, path));

                    // 清空输入框
                    newImageName.set("");
                    newImagePath.set("");

                    System.out.println("添加图片预定义: " + type.getDisplayName() + " - " + name);
                }
            }

            ImGui.separator();

            // 显示已有的预定义列表
            if (!imageDefinitions.isEmpty()) {
                ImGui.text("已有预定义:");

                for (int i = 0; i < imageDefinitions.size(); i++) {
                    ImageDefinition def = imageDefinitions.get(i);
                    ImGui.pushID(i);

                    // 类型标识
                    String typeTag = def.type == ImageType.NETWORK ? "[网络]" : "[本地]";
                    ImGui.text(typeTag + " " + def.name);

                    // 显示路径（如果路径太长，截断显示）
                    String displayPath = def.path;
                    if (displayPath.length() > 50) {
                        displayPath = displayPath.substring(0, 47) + "...";
                    }
                    ImGui.sameLine();
                    ImGui.textDisabled("-> " + displayPath);

                    // 悬停显示完整路径
                    if (ImGui.isItemHovered()) {
                        ImGui.beginTooltip();
                        ImGui.text(def.path);
                        ImGui.endTooltip();
                    }

                    // 删除按钮
                    ImGui.sameLine();
                    String deleteLabel = "删除##del_img_" + i;
                    if (ImGui.button(deleteLabel)) {
                        imageDefinitions.remove(i);
                        i--;
                        ImGui.popID();
                        continue;
                    }

                    ImGui.popID();
                }
            } else {
                ImGui.textDisabled("暂无预定义");
            }

            ImGui.separator();
        }
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
            currentTime = Math.round(worldSeconds / MIN_TIME_STEP) * MIN_TIME_STEP;
        }

        // 右键点击检测
        if (ImGui.isItemHovered() && ImGui.isMouseClicked(1)) {
            float mouseX = ImGui.getIO().getMousePosX();
            float worldSeconds = (mouseX - timelineStartX - timelineOffset) / (PIXELS_PER_SECOND_BASE * timelineZoom);
            worldSeconds = Math.max(0f, worldSeconds);
            rightClickTime = Math.round(worldSeconds / MIN_TIME_STEP) * MIN_TIME_STEP;
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
            if (s % 5 == 0 || timelineZoom > 1 ) {
                drawList.addText(x + 4f, timelineStartY + tickHeight + 2f, 0xFFFFFFFF, s + "s");
            }
        }

        float playheadX = timelineStartX + timelineOffset + currentTime * pixelsPerSecond;
        drawList.addLine(playheadX, timelineStartY, playheadX, timelineStartY + timelineHeight, 0xFF66CCFF, 2f);

        // 绘制事件标记
        for (Map.Entry<Integer, List<Action>> entry : eventData.entrySet()) {
            int timeKey = entry.getKey();
            float eventTime = timeKey / 100f;
            List<Action> actions = entry.getValue();

            float eventX = timelineStartX + timelineOffset + eventTime * pixelsPerSecond;

            // 只绘制可见范围内的事件
            if (eventX < timelineStartX - CULLING_MARGIN || eventX > timelineStartX + availableWidth + CULLING_MARGIN) {
                continue;
            }

            // 根据事件数量选择颜色和大小
            int eventCount = actions.size();
            int color = eventCount > 1 ? 0xFFFFA500 : 0xFF00FF00; // 多个事件为橙色，单个为绿色
            float markerSize = Math.min(20f, 10f + eventCount * 2f);

            // 绘制事件标记（三角形）
            float triangleY = timelineStartY + timelineHeight - 5f;
            drawList.addTriangleFilled(
                    eventX, triangleY,
                    eventX - markerSize / 2, triangleY + markerSize,
                    eventX + markerSize / 2, triangleY + markerSize,
                    color
            );

            // 绘制事件数量文本
            if (eventCount > 1) {
                String countText = String.valueOf(eventCount);
                drawList.addText(eventX + 5f, triangleY, 0xFFFFFFFF, countText);
            }

            // 悬停时显示事件详情
            if (ImGui.isMouseHoveringRect(eventX - 5f, triangleY, eventX + 5f, triangleY + markerSize + 5f)) {
                ImGui.beginTooltip();
                ImGui.text(String.format("时间: %.2fs", eventTime));
                ImGui.text(String.format("事件数: %d", eventCount));
                ImGui.separator();
                for (Action action : actions) {
                    ImGui.text("- " + action.getDisplayName());
                }
                ImGui.endTooltip();
            }
        }

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
            if (ImGui.combo("##event_type", selectedEventTypeIndex, eventTypeNames, ImGuiComboFlags.HeightLargest)) {
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

            // 显示验证错误信息
            Action tempAction = ActionFactory.createActionFromParameters(currentEventType, (int) (rightClickTime * 100), parameterValues);
            if (!tempAction.isValid()) {
                ImGui.textColored(1.0f, 0.0f, 0.0f, 1.0f, "错误: " + tempAction.getValidationError());
            }

            if (ImGui.button("确定")) {
                Action action = ActionFactory.createActionFromParameters(currentEventType, (int) (rightClickTime * 100), parameterValues);

                // 验证参数
                if (!action.isValid()) {
                    System.err.println("参数验证失败: " + action.getValidationError());
                } else {
                    // 将事件添加到 eventData
                    int timeKey = (int) (rightClickTime * 100);
                    eventData.computeIfAbsent(timeKey, k -> new ArrayList<>()).add(action);

                    System.out.println("添加事件: " + action.getDisplayName());
                    System.out.println("时间: " + rightClickTime + "s");
                    for (int i = 0; i < parameterValues.size() && i < currentEventType.getParameters().size(); i++) {
                        ParameterDefinition param = currentEventType.getParameters().get(i);
                        ParameterValue value = parameterValues.get(i);
                        System.out.println("  " + param.name() + ": " + getValueAsString(value, param.type()));
                    }

                    ImGui.closeCurrentPopup();
                    showRightClickPopup = false;
                }
            }
            ImGui.sameLine();
            if (ImGui.button("取消")) {
                ImGui.closeCurrentPopup();
                showRightClickPopup = false;
            }

            ImGui.endPopup();
        }
    }

    private void drawEventList() {
        ImGui.separator();
        ImGui.text("事件列表");

        // 统计事件总数
        int totalEvents = eventData.values().stream().mapToInt(List::size).sum();
        ImGui.sameLine();
        ImGui.textDisabled("(共 " + totalEvents + " 个事件)");

        // 删除所有事件按钮
        if (ImGui.button("清空所有事件")) {
            eventData.clear();
        }

        // 按时间排序显示事件
        if (!eventData.isEmpty()) {
            ImGui.beginChild("EventListChild", 0, 300, true);

            for (Map.Entry<Integer, List<Action>> entry : eventData.entrySet()) {
                int timeKey = entry.getKey();
                float time = timeKey / 100f;
                List<Action> actions = entry.getValue();

                // 显示时间标题
                ImGui.pushID(timeKey);
                ImGui.text(String.format("[%.2fs] - %d 个事件", time, actions.size()));

                // 显示每个事件
                for (int i = 0; i < actions.size(); i++) {
                    Action action = actions.get(i);
                    ImGui.pushID(i);

                    // 事件描述
                    ImGui.text("  " + action.getDisplayName());

                    // 复制按钮
                    ImGui.sameLine();
                    String copyLabel = "复制##" + timeKey + "_" + i;
                    if (ImGui.button(copyLabel)) {
                        copyActionToClipboard(action);
                    }

                    // 删除按钮
                    ImGui.sameLine();
                    String deleteLabel = "删除##" + timeKey + "_" + i;
                    if (ImGui.button(deleteLabel)) {
                        actions.remove(i);
                        i--; // 调整索引
                        // 如果该时间点没有事件了，删除这个键
                        if (actions.isEmpty()) {
                            eventData.remove(timeKey);
                        }
                        ImGui.popID();
                        break; // 退出内层循环
                    }
                    ImGui.popID();
                }

                ImGui.popID();
                ImGui.separator();
            }

            ImGui.endChild();
        } else {
            ImGui.textDisabled("暂无事件，右键点击时间线添加事件");
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
                ImGui.text("X:");
                ImGui.sameLine();
                if (ImGui.inputFloat("##coord_x", tempX)) value.coordX = tempX.get();
                ImGui.sameLine();
                ImGui.text("Y:");
                ImGui.sameLine();
                if (ImGui.inputFloat("##coord_y", tempY)) value.coordY = tempY.get();
                ImGui.sameLine();
                ImGui.text("Z:");
                ImGui.sameLine();
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
                    (int) (value.colorValue[0] * 255),
                    (int) (value.colorValue[1] * 255),
                    (int) (value.colorValue[2] * 255),
                    (int) (value.colorValue[3] * 255));
            case COORDINATES -> String.format("(%.2f, %.2f, %.2f)", value.coordX, value.coordY, value.coordZ);
        };
    }

    /**
     * 复制单个事件的action文本到剪贴板
     */
    private void copyActionToClipboard(Action action) {
        try {
            // 生成该事件的脚本字符串
            String scriptText = action.toScriptString();

            // 获取系统剪贴板
            Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
            java.awt.datatransfer.Clipboard clipboard = defaultToolkit.getSystemClipboard();

            // 创建StringSelection并设置到剪贴板
            StringSelection stringSelection = new StringSelection(scriptText);
            clipboard.setContents(stringSelection, null);

            System.out.println("已复制到剪贴板: " + scriptText);
        } catch (Exception e) {
            System.err.println("复制到剪贴板失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 导出脚本到文件
     */
    private void exportScript() {
        if (eventData.isEmpty()) {
            System.err.println("没有事件可导出");
            return;
        }

        try {
            // 首先检查所有使用的图片是否已预定义
            java.util.Set<String> usedImageNames = new java.util.HashSet<>();
            java.util.Set<String> definedImageNames = imageDefinitions.stream()
                    .map(def -> def.name)
                    .collect(java.util.stream.Collectors.toSet());

            // 收集所有使用到的图片名称
            for (List<Action> actions : eventData.values()) {
                for (Action action : actions) {
                    if (action instanceof ImageShowAction imgShow) {
                        usedImageNames.add(imgShow.imageName);
                    } else if (action instanceof ImageHideAction imgHide) {
                        usedImageNames.add(imgHide.imageName);
                    } else if (action instanceof ImageMoveAction imgMove) {
                        usedImageNames.add(imgMove.imageName);
                    }
                }
            }

            // 检查是否有未定义的图片
            java.util.Set<String> undefinedImages = new java.util.HashSet<>(usedImageNames);
            undefinedImages.removeAll(definedImageNames);

            if (!undefinedImages.isEmpty()) {
                System.err.println("错误: 以下图片未被预定义:");
                for (String name : undefinedImages) {
                    System.err.println("  - " + name);
                }
                // 使用系统原生消息框
                showErrorMessage("导出错误",
                        "以下图片未被预定义，请先在\"图片预定义管理\"中添加:\n\n" +
                        String.join("\n", undefinedImages));
                return;
            }

            // 生成脚本内容
            StringBuilder script = new StringBuilder();
            script.append("@Cinematic\n");

            // 首先输出图片预定义
            if (!imageDefinitions.isEmpty()) {
                script.append("# 图片预定义\n");
                for (ImageDefinition def : imageDefinitions) {
                    script.append(def.toScriptString()).append("\n");
                }
                script.append("\n");
            }

            // 遍历所有时间点
            for (Map.Entry<Integer, List<Action>> entry : eventData.entrySet()) {
                int timeKey = entry.getKey();
                List<Action> actions = entry.getValue();

                // 时间转换为游戏刻 (1刻 = 0.05秒 = 5 timeKey单位)
                int ticks = timeKey / 5;

                // 生成时间行
                script.append(String.format("%d:", ticks));

                // 添加所有动作
                for (Action action : actions) {
                    script.append(" ").append(action.toScriptString());
                }

                script.append("\n");
            }

            // 创建原生文件保存对话框
            Frame frame = new Frame(); // 创建一个隐藏的父窗口
            FileDialog fileDialog = new FileDialog(frame, "保存脚本文件", FileDialog.SAVE);
            fileDialog.setFile("cinematic.txt"); // 设置默认文件名
            fileDialog.setVisible(true);

            // 获取用户选择的文件
            String selectedFile = fileDialog.getFile();
            String selectedDir = fileDialog.getDirectory();

            if (selectedFile != null && selectedDir != null) {
                java.io.File fileToSave = new java.io.File(selectedDir, selectedFile);

                // 确保文件以.txt结尾
                if (!fileToSave.getName().toLowerCase().endsWith(".txt")) {
                    fileToSave = new java.io.File(fileToSave.getParentFile(), fileToSave.getName() + ".txt");
                }

                // 写入文件
                try (FileWriter writer = new FileWriter(fileToSave)) {
                    writer.write(script.toString());
                }

                System.out.println("脚本已导出到: " + fileToSave.getAbsolutePath());
                System.out.println("共 " + imageDefinitions.size() + " 个图片预定义, " +
                        eventData.size() + " 个时间点");

                // 显示成功提示
                showInfoMessage("导出成功",
                        "脚本已导出到:\n" + fileToSave.getAbsolutePath() +
                        "\n\n共 " + imageDefinitions.size() + " 个图片预定义, " +
                        eventData.size() + " 个时间点");

                // 同时输出到控制台以便预览
                System.out.println("\n========== 导出的脚本 ==========");
                System.out.print(script);
                System.out.println("========== 脚本结束 ==========\n");
            }

        } catch (Exception e) {
            System.err.println("导出脚本失败: " + e.getMessage());
            e.printStackTrace();
            showErrorMessage("导出失败", "导出脚本时发生错误:\n" + e.getMessage());
        }
    }

    /**
     * 显示错误消息（使用系统原生对话框）
     */
    private void showErrorMessage(String title, String message) {
        // 在Windows上使用原生对话框
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            Frame frame = new Frame();
            frame.setVisible(false);
            JOptionPane.showMessageDialog(frame, message, title, JOptionPane.ERROR_MESSAGE);
            frame.dispose();
        } else {
            // 其他平台使用标准对话框
            JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 显示信息消息（使用系统原生对话框）
     */
    private void showInfoMessage(String title, String message) {
        // 在Windows上使用原生对话框
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            Frame frame = new Frame();
            frame.setVisible(false);
            JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
        } else {
            // 其他平台使用标准对话框
            JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
