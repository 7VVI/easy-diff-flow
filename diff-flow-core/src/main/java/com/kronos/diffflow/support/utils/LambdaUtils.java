package com.kronos.diffflow.support.utils;

import com.kronos.diffflow.support.function.SFunction;

/**
 * @author zhangyh
 * @Date 2025/8/19 9:41
 * @desc
 */
public final class LambdaUtils {
    public static <T> String resolveFieldName(SFunction<T, ?> fn) {
        try {
            java.lang.invoke.SerializedLambda lambda = extract(fn);
            String impl = lambda.getImplMethodName(); // "getInvoiceStatus"
            if (impl.startsWith("get") && impl.length() > 3) {
                String prop = impl.substring(3);
                return Character.toLowerCase(prop.charAt(0)) + prop.substring(1);
            }
            if (impl.startsWith("is") && impl.length() > 2) {
                String prop = impl.substring(2);
                return Character.toLowerCase(prop.charAt(0)) + prop.substring(1);
            }
            return impl;
        } catch (Exception e) {
            throw new IllegalStateException("Cannot resolve field name from lambda", e);
        }
    }
    private static java.lang.invoke.SerializedLambda extract(Object fn) throws Exception {
        java.lang.reflect.Method writeReplace = fn.getClass().getDeclaredMethod("writeReplace");
        writeReplace.setAccessible(true);
        return (java.lang.invoke.SerializedLambda) writeReplace.invoke(fn);
    }
}