package com.kronos.diffflow.support.function;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author zhangyh
 * @Date 2025/8/19 9:42
 * @desc
 */
@FunctionalInterface
public interface FieldComparator {
    boolean isDiff(Object left, Object right);
    static FieldComparator defaultComparator() {
        return (l, r) -> !java.util.Objects.equals(l, r);
    }

    static FieldComparator equalsComparator() { return (l, r) -> !Objects.equals(l, r); }

    static FieldComparator doubleWithEpsilon(double eps) {
        return (l, r) -> {
            if (l == null && r == null) return false;
            if (l == null || r == null) return true;
            Double dl = toDouble(l), dr = toDouble(r);
            if (dl == null || dr == null) return !Objects.equals(l, r);
            return Math.abs(dl - dr) > eps;
        };
    }
    static FieldComparator ignoringBlank() {
        return (l, r) -> {
            String ls = normBlank(l), rs = normBlank(r);
            return !Objects.equals(ls, rs);
        };
    }
    private static Double toDouble(Object o) {
        if (o instanceof Number) return ((Number) o).doubleValue();
        if (o instanceof String) {
            try { return Double.parseDouble(((String) o).trim()); } catch (Exception ignored) {}
        }
        if (o instanceof BigDecimal) return ((BigDecimal) o).doubleValue();
        return null;
    }

    private static String normBlank(Object o) { return (o == null) ? null : ("".equals(o.toString().trim()) ? null : o.toString().trim()); }
}