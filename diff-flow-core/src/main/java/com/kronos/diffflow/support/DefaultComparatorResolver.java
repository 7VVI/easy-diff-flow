package com.kronos.diffflow.support;

import com.kronos.diffflow.support.function.FieldComparator;

/**
 * @author zhangyh
 * @Date 2025/8/19 11:08
 * @desc
 */
public final class DefaultComparatorResolver implements ComparatorResolver {
    @Override public FieldComparator resolve(DiffRule rule, Class<?> fieldType) {
        if (rule.comparator != null) {
            return rule.comparator;
        }
        // 可按类型分发，这里简单返回 equals 比较
        return FieldComparator.equalsComparator();
    }
}
