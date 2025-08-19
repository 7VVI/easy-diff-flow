package com.kronos.diffflow.support.annotations;

/**
 * @author zhangyh
 * @Date 2025/8/19 11:14
 * @desc
 */
public @interface Diff {
    String ruleSet();
    String left();   // SpEL，如 #p0
    String right();  // SpEL，如 #p1
    boolean computeBeforeProceed() default true; // true：以入参为准；false：以 proceed 后值为准
}
