package moe.gensoukyo.automata.actions;

import moe.gensoukyo.automata.actions.audio.*;
import moe.gensoukyo.automata.actions.effects.ShakeAction;
import moe.gensoukyo.automata.actions.image.*;
import moe.gensoukyo.automata.actions.model.*;
import moe.gensoukyo.automata.actions.parameter.ParameterValue;
import moe.gensoukyo.automata.actions.scene.*;
import moe.gensoukyo.automata.actions.text.*;

/**
 * Action工厂类
 * 根据事件类型和参数值创建对应的Action实例
 */
public class ActionFactory {

    /**
     * 从参数值创建Action实例
     *
     * @param eventType 事件类型
     * @param timeKey 时间键值(秒*100)
     * @param values 参数值列表
     * @return 对应的Action实例
     */
    public static Action createActionFromParameters(EventType eventType, int timeKey, java.util.List<ParameterValue> values) {
        return switch (eventType) {
            case MODEL_SHOW -> new ModelShowAction(timeKey,
                values.get(0).stringValue.get(),
                values.get(1).stringValue.get(),
                values.get(2).coordX,
                values.get(2).coordY,
                values.get(2).coordZ);
            case MODEL_HIDE -> new ModelHideAction(timeKey, values.get(0).stringValue.get());
            case MODEL_POS -> new ModelPosAction(timeKey,
                values.get(0).stringValue.get(),
                values.get(1).coordX,
                values.get(1).coordY,
                values.get(1).coordZ);
            case MODEL_MOTION -> new ModelMotionAction(timeKey,
                values.get(0).stringValue.get(),
                values.get(1).stringValue.get(),
                values.get(2).booleanValue.get());
            case MODEL_SCALE -> new ModelScaleAction(timeKey,
                values.get(0).stringValue.get(),
                values.get(1).floatValue.get());
            case MODEL_LIGHT -> new ModelLightAction(timeKey,
                values.get(0).stringValue.get(),
                values.get(1).intValue.get());
            case MODEL_FOLLOW -> new ModelFollowAction(timeKey,
                values.get(0).stringValue.get(),
                values.get(1).booleanValue.get());
            case MODEL_COLOR -> new ModelColorAction(timeKey,
                values.get(0).stringValue.get(),
                values.get(1).colorValue.clone());
            case MODEL_PAT -> new ModelPatAction(timeKey, values.get(0).stringValue.get());
            case LOCAL_IMAGE -> new LocalImageAction(timeKey,
                values.get(0).stringValue.get(),
                values.get(1).stringValue.get());
            case IMAGE_SHOW -> new ImageShowAction(timeKey,
                values.get(0).stringValue.get(),
                values.get(1).floatValue.get(),
                values.get(2).floatValue.get(),
                values.get(3).stringValue.get(),
                values.get(4).stringValue.get());
            case IMAGE_HIDE -> new ImageHideAction(timeKey, values.get(0).stringValue.get());
            case IMAGE_MOVE -> new ImageMoveAction(timeKey,
                values.get(0).stringValue.get(),
                values.get(1).floatValue.get(),
                values.get(2).floatValue.get());
            case IMAGE_CLEAR -> new ImageClearAction(timeKey);
            case PLAY_SOUND -> new PlaySoundAction(timeKey, values.get(0).stringValue.get());
            case PLAY_MUSIC -> new PlayMusicAction(timeKey, values.get(0).stringValue.get());
            case STOP_MUSIC -> new StopMusicAction(timeKey);
            case SCENE_COLOR -> new SceneColorAction(timeKey,
                values.get(0).colorValue.clone());
            case SCENE_COLOR_CLEAR -> new SceneColorClearAction(timeKey);
            case SCENE_TEXT -> new SceneTextAction(timeKey,
                values.get(0).stringValue.get(),
                values.get(1).intValue.get(),
                values.get(2).floatValue.get(),
                values.get(3).floatValue.get(),
                values.get(4).colorValue.clone(),
                values.get(5).intValue.get(),
                values.get(6).intValue.get());
            case SCENE_TEXT_CLEAR -> new SceneTextClearAction(timeKey);
            case TEXT_SPEED -> new TextSpeedAction(timeKey, values.get(0).intValue.get());
            case TEXT_POS -> new TextPosAction(timeKey,
                values.get(1).floatValue.get(),
                values.get(2).floatValue.get());
            case TEXT_COLOR -> new TextColorAction(timeKey, values.get(0).stringValue.get());
            case SHAKE -> new ShakeAction(timeKey,
                values.get(0).floatValue.get(),
                values.get(1).floatValue.get());
        };
    }
}
