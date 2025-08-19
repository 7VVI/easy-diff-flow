package com.kronos.diffflow.support.function;

/**
 * @author zhangyh
 * @Date 2025/8/19 9:43
 * @desc
 */
@FunctionalInterface
public interface FieldFormatter {
    String format(Object v);
    static FieldFormatter noop() { return v -> v == null ? null : v.toString(); }
}
