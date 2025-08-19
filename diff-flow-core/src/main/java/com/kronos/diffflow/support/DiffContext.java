package com.kronos.diffflow.support;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * @author zhangyh
 * @Date 2025/8/19 9:38
 * @desc
 */
@Data
@Builder
public class DiffContext {
    public final String  operator;
    public final String  requestId;
    public final Instant bizTime;

    public DiffContext(String operator, String requestId, Instant bizTime) {
        this.operator = operator;
        this.requestId = requestId;
        this.bizTime = bizTime;
    }
}
