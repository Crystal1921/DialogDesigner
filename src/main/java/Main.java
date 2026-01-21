import imgui.ImGui;
import imgui.app.Application;
import imgui.app.Configuration;

public class Main extends Application {
    private static final float PIXELS_PER_SECOND_BASE = 100f;
    private static final float ZOOM_SENSITIVITY = 0.1f;
    private static final float CULLING_MARGIN = 50f;
    private static final float MIN_TIME_STEP = 0.05f;

    private float timelineOffset = 0f;
    private float timelineZoom = 1f;
    private float currentTime = 0f;
    private final float[] sceneColor = new float[]{1f, 1f, 1f, 1f};

    @Override
    protected void configure(Configuration config) {
        config.setTitle("Dear ImGui is Awesome!");
    }

    @Override
    public void process() {
        ImGui.text("拖拽滑动并可缩放的时间条演示");
        ImGui.colorEdit4("scene_color", sceneColor);
        ImGui.text(String.format("当前时间: %.2fs (最小刻度 %.2fs)", currentTime, MIN_TIME_STEP));
        drawTimeline();
    }

    public static void main(String[] args) {
        launch(new Main());
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
            }
        }

        if (ImGui.isItemActive() && ImGui.isMouseDragging(0)) {
            timelineOffset += ImGui.getIO().getMouseDeltaX();
        }

        if (ImGui.isItemHovered() && ImGui.isMouseClicked(0)) {
            float mouseX = ImGui.getIO().getMousePosX();
            float worldSeconds = (mouseX - timelineStartX - timelineOffset) / (PIXELS_PER_SECOND_BASE * timelineZoom);
            worldSeconds = Math.max(0f, worldSeconds);
            float snapped = Math.round(worldSeconds / MIN_TIME_STEP) * MIN_TIME_STEP;
            currentTime = snapped;
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
}
