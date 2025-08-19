package com.kronos.diffflow.support;

/**
 * @author zhangyh
 * @Date 2025/8/19 9:36
 * @desc
 */
public interface PatchCodec {
    Patch fromJson(String json);
    Patch empty();
}