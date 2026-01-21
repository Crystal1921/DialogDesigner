import imgui.ImGui;
import imgui.app.Application;
import imgui.app.Configuration;

public class Main extends Application {
    private float timelineOffset = 0f;
    private float timelineZoom = 1f;

    @Override
    protected void configure(Configuration config) {
        config.setTitle("Dear ImGui is Awesome!");
    }

    @Override
    public void process() {
        ImGui.text("拖拽滑动并可缩放的时间条演示");
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
                float world = (mouseX - timelineStartX - timelineOffset) / (100f * timelineZoom);
                float newZoom = timelineZoom * (1f + wheel * 0.1f);
                timelineZoom = Math.max(0.2f, Math.min(5f, newZoom));
                timelineOffset = mouseX - timelineStartX - world * 100f * timelineZoom;
            }
        }

        if (ImGui.isItemActive() && ImGui.isMouseDragging(0)) {
            timelineOffset += ImGui.getIO().getMouseDeltaX();
        }

        var drawList = ImGui.getWindowDrawList();
        float pixelsPerSecond = 100f * timelineZoom;
        float visibleSeconds = (availableWidth / pixelsPerSecond) + 2f;
        int startSecond = (int) Math.floor((-timelineOffset) / pixelsPerSecond) - 1;
        int endSecond = startSecond + (int) visibleSeconds + 3;

        for (int s = startSecond; s <= endSecond; s++) {
            float x = timelineStartX + timelineOffset + s * pixelsPerSecond;
            if (x < timelineStartX - 50 || x > timelineStartX + availableWidth + 50) {
                continue;
            }
            float tickHeight = (s % 5 == 0) ? 30f : 18f;
            drawList.addLine(x, timelineStartY, x, timelineStartY + tickHeight, 0xFFAAAAAA);
            if (s % 5 == 0) {
                drawList.addText(x + 4f, timelineStartY + tickHeight + 2f, 0xFFFFFFFF, s + "s");
            }
        }

        drawList.addRect(timelineStartX, timelineStartY, timelineStartX + availableWidth, timelineStartY + timelineHeight, 0x80FFFFFF);
    }
}
