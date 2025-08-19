package com.kronos.diffflow.support;

import com.kronos.diffflow.support.function.FieldComparator;

/**
 * @author zhangyh
 * @Date 2025/8/19 9:37
 * @desc 比较器解析器：按字段类型/规则选用具体比较器
 */
public interface ComparatorResolver {
    FieldComparator resolve(DiffRule rule, Class<?> fieldType);
}
