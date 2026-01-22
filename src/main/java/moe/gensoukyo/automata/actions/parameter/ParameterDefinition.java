package moe.gensoukyo.automata.actions.parameter;

/**
 * 参数定义记录类
 */
public record ParameterDefinition(
    String name,
    ParameterType type,
    String defaultValue,
    String description
) {}
