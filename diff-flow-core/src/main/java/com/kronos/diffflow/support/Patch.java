package com.kronos.diffflow.support;

/**
 * @author zhangyh
 * @Date 2025/8/19 9:36
 * @desc
 */
public interface Patch {
    // 将补丁应用到当前对象（通常先转 JSON，再应用到 Map/POJO）
    <T> T applyTo(T current, Class<T> type);
    // 与另一个补丁合并为“最终补丁”（后者覆盖前者）
    Patch merge(Patch other);
    String asJson();
}