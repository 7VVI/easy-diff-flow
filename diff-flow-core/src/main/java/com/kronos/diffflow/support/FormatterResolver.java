package com.kronos.diffflow.support;

import com.kronos.diffflow.support.function.FieldFormatter;

/**
 * @author zhangyh
 * @Date 2025/8/19 9:37
 * @desc
 */
public interface FormatterResolver {
    FieldFormatter resolve(DiffRule rule, Class<?> fieldType);
}
