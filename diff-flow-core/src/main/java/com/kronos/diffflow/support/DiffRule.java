package com.kronos.diffflow.support;

import com.kronos.diffflow.enums.CodegenColumnHtmlTypeEnum;
import com.kronos.diffflow.support.function.FieldComparator;
import com.kronos.diffflow.support.function.FieldFormatter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author zhangyh
 * @Date 2025/8/19 9:39
 * @desc 规则对象
 */
@Data
@Builder
@AllArgsConstructor
public class DiffRule {
    final String                    leftPath;
    final String                    rightPath;
    final String                    displayName;
    final CodegenColumnHtmlTypeEnum htmlType;
    final String                    whenSpel;                  // 条件表达式，可为空
    final FieldComparator           comparator;       // 可为空 → 使用 resolver
    final FieldFormatter            formatter;         // 可为空 → 使用 resolver
    final String                    collectionKeyPath;         // 集合键路径，非空则走集合对比


    private DiffRule(Builder b) {
        this.leftPath = b.leftPath;
        this.rightPath = b.rightPath;
        this.displayName = b.displayName;
        this.htmlType = b.htmlType;
        this.whenSpel = b.whenSpel;
        this.comparator = b.comparator;
        this.formatter = b.formatter;
        this.collectionKeyPath = b.collectionKeyPath;
    }

    public static class Builder {
        String leftPath, rightPath, displayName, whenSpel, collectionKeyPath;
        public CodegenColumnHtmlTypeEnum htmlType = CodegenColumnHtmlTypeEnum.TEXT;
        public FieldComparator           comparator;
        FieldFormatter formatter;

        public Builder leftPath(String p) {
            this.leftPath = p;
            return this;
        }

        public Builder rightPath(String p) {
            this.rightPath = p;
            return this;
        }

        public Builder displayName(String s) {
            this.displayName = s;
            return this;
        }

        public Builder htmlType(CodegenColumnHtmlTypeEnum t) {
            this.htmlType = t;
            return this;
        }

        public Builder whenSpel(String spel) {
            this.whenSpel = spel;
            return this;
        }

        public Builder comparator(FieldComparator c) {
            this.comparator = c;
            return this;
        }

        public Builder formatter(FieldFormatter f) {
            this.formatter = f;
            return this;
        }

        public Builder collectionKeyPath(String p) {
            this.collectionKeyPath = p;
            return this;
        }

        public DiffRule build() {
            return new DiffRule(this);
        }
    }
}