package moe.gensoukyo.automata.actions.parameter;

import imgui.type.ImInt;
import imgui.type.ImString;
import imgui.type.ImFloat;
import imgui.type.ImBoolean;

/**
 * 参数值存储类
 */
public class ParameterValue {
    public ImString stringValue = new ImString(256);
    public ImInt intValue = new ImInt(0);
    public ImFloat floatValue = new ImFloat(0f);
    public ImBoolean booleanValue = new ImBoolean(false);
    public float[] colorValue = new float[]{1f, 1f, 1f, 1f};
    public float coordX = 0f, coordY = 0f, coordZ = 0f;

    public static ParameterValue create(ParameterType type, String defaultValue) {
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
