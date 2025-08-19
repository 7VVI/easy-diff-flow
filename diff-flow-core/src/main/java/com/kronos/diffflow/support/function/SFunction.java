package com.kronos.diffflow.support.function;

/**
 * @author zhangyh
 * @Date 2025/8/19 9:41
 * @desc 支持方法引用获取字段名的函数式接口（类似 MyBatis-Plus 的 SFunction
 */
@FunctionalInterface
public interface SFunction<T, R> extends java.util.function.Function<T, R>, java.io.Serializable {}
