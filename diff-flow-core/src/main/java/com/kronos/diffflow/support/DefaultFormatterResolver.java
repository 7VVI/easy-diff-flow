package com.kronos.diffflow.support;

import com.kronos.diffflow.enums.CodegenColumnHtmlTypeEnum;
import com.kronos.diffflow.support.function.FieldFormatter;

/**
 * @author zhangyh
 * @Date 2025/8/19 11:09
 * @desc
 */
public class DefaultFormatterResolver implements FormatterResolver {
    @Override public FieldFormatter resolve(DiffRule rule, Class<?> fieldType) {
        if (rule.formatter != null) return rule.formatter;
        if (rule.htmlType == CodegenColumnHtmlTypeEnum.MONEY) {
            return FieldFormatter.money();
        }
        return FieldFormatter.noop();
    }
}