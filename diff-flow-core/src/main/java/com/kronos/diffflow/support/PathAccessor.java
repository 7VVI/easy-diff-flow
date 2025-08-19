package com.kronos.diffflow.support;

/**
 * @author zhangyh
 * @Date 2025/8/19 9:37
 * @desc
 */
public interface PathAccessor {
    Object read(Object root, String path); // 支持 a.b.c、list[2]、map[key]
}