package com.kronos.diffflow.support.function;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * @author zhangyh
 * @Date 2025/8/19 9:43
 * @desc
 */
@FunctionalInterface
public interface FieldFormatter {
    String format(Object v);
    static FieldFormatter noop() { return v -> v == null ? null : v.toString(); }

    static FieldFormatter money() { return v -> v == null ? null : new DecimalFormat("0.00").format(new BigDecimal(v.toString())); }
}
