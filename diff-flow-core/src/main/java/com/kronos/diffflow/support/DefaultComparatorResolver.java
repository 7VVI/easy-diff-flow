package com.kronos.diffflow.support;

import com.kronos.diffflow.enums.CodegenColumnHtmlTypeEnum;
import com.kronos.diffflow.support.function.FieldComparator;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zhangyh
 * @Date 2025/8/19 11:08
 * @desc 增强版比较器解析器，支持基于CodegenColumnHtmlTypeEnum的策略选择
 */
public final class DefaultComparatorResolver implements ComparatorResolver {
    
    @Override 
    public FieldComparator resolve(DiffRule rule, Class<?> fieldType) {
        // 优先使用规则中指定的比较器
        if (rule.comparator != null) {
            return rule.comparator;
        }
        
        // 根据HTML类型选择对应的比较器策略
        if (rule.htmlType != null) {
            return resolveByHtmlType(rule.htmlType);
        }
        
        // 如果没有HTML类型，则根据字段类型选择默认比较器
        return resolveByFieldType(fieldType);
    }
    
    /**
     * 根据CodegenColumnHtmlTypeEnum选择比较器
     */
    private FieldComparator resolveByHtmlType(CodegenColumnHtmlTypeEnum htmlType) {
        return switch (htmlType) {
            case MONEY -> FieldComparator.doubleWithEpsilon(0.00000000000000000000001); // 金额比较，允许0.01误差
            case NUMBER -> FieldComparator.doubleWithEpsilon(0.001); // 数字比较，允许0.001误差
            case TEXT -> FieldComparator.ignoringBlank(); // 文本比较，忽略空白字符
            case ENUM -> FieldComparator.defaultComparator(); // 枚举严格比较
            case DATE -> FieldComparator.defaultComparator(); // 日期严格比较
            default -> FieldComparator.defaultComparator(); // 默认比较器
        };
    }
    
    /**
     * 根据字段类型选择默认比较器
     */
    private FieldComparator resolveByFieldType(Class<?> fieldType) {
        if (fieldType == null) {
            return FieldComparator.defaultComparator();
        }
        
        // 数字类型使用精度比较
        if (BigDecimal.class.isAssignableFrom(fieldType) || 
            Double.class.isAssignableFrom(fieldType) || 
            Float.class.isAssignableFrom(fieldType)) {
            return FieldComparator.doubleWithEpsilon(0.001);
        }
        
        // 字符串类型忽略空白
        if (String.class.isAssignableFrom(fieldType)) {
            return FieldComparator.ignoringBlank();
        }
        
        // 日期类型严格比较
        if (Date.class.isAssignableFrom(fieldType) || 
            java.time.LocalDate.class.isAssignableFrom(fieldType) ||
            java.time.LocalDateTime.class.isAssignableFrom(fieldType)) {
            return FieldComparator.defaultComparator();
        }
        
        // 其他类型使用默认比较器
        return FieldComparator.defaultComparator();
    }
}
