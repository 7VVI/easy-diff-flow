package com.kronos.diffflow.support;

import com.kronos.diffflow.model.DiffItem;
import com.kronos.diffflow.support.function.FieldComparator;
import com.kronos.diffflow.support.function.FieldFormatter;

public final class DiffEngine {

    private static final PathAccessor ACCESSOR = new ReflectivePathAccessor();
    private static final ComparatorResolver COMPARATOR_RESOLVER = new DefaultComparatorResolver();
    private static final FormatterResolver FORMATTER_RESOLVER = new DefaultFormatterResolver();

    public static java.util.List<DiffItem> diff(Object left, Object right, java.util.List<DiffRule> rules) {
        java.util.List<DiffItem> out = new java.util.ArrayList<>();
        if (rules == null || rules.isEmpty()) return out;

        for (DiffRule r : rules) {
            if (r == null) continue;
            String leftPath = r.getLeftPath();
            String rightPath = r.getRightPath();

            Object lv = ACCESSOR.read(left, leftPath);
            Object rv = ACCESSOR.read(right, rightPath);

            // 当左右都为null，且未显式指定比较器时，直接认为无差异，避免NPE
            if (lv == null && rv == null && r.getComparator() == null) {
                continue;
            }

            // 解析比较器与格式化器（带默认策略与htmlType支持）
            Class<?> fieldType = typeOf(left, leftPath);
            FieldComparator cmp = (r.getComparator() != null) ? r.getComparator() : COMPARATOR_RESOLVER.resolve(r, fieldType);
            if (cmp == null) cmp = FieldComparator.defaultComparator();

            if (cmp.isDiff(lv, rv)) {
                FieldFormatter fmt = (r.getFormatter() != null) ? r.getFormatter() : FORMATTER_RESOLVER.resolve(r, fieldType);
                if (fmt == null) fmt = FieldFormatter.noop();
                out.add(new DiffItem(leftPath, r.getDisplayName(), fmt.format(lv), fmt.format(rv)));
            }
        }
        return out;
    }

    private static Class<?> typeOf(Object root, String path) {
        Object v = ACCESSOR.read(root, path);
        return v == null ? Object.class : v.getClass();
    }
}
