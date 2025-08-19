package com.kronos.diffflow.support;

import com.kronos.diffflow.enums.CodegenColumnHtmlTypeEnum;
import com.kronos.diffflow.support.function.SFunction;
import com.kronos.diffflow.support.utils.LambdaUtils;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author zhangyh
 * @Date 2025/8/19 9:43
 * @desc 规则集注册表
 */
public final class DiffRegistry {
    private static final Map<String, List<DiffRule>> RULESETS = new LinkedHashMap<>();

    public static <L, R> RuleSetBuilder<L, R> of(String name, Class<L> left, Class<R> right) {
        return new RuleSetBuilder<>(name, left, right);
    }

    public static List<DiffRule> get(String name) {
        return RULESETS.getOrDefault(name, Collections.emptyList());
    }

    public static final class RuleSetBuilder<L, R> {
        private final String name;
        private final Class<L> left;
        private final Class<R> right;
        private final List<DiffRule> list = new ArrayList<>();
        private DiffRule last;

        RuleSetBuilder(String name, Class<L> left, Class<R> right) {
            this.name = name;
            this.left = left;
            this.right = right;
        }

        public RuleSetBuilder<L, R> map(SFunction<L, ?> lfn, SFunction<R, ?> rfn, String display) {
            return map(lfn, rfn, display, CodegenColumnHtmlTypeEnum.TEXT, null);
        }

        public RuleSetBuilder<L, R> map(SFunction<L, ?> lfn, SFunction<R, ?> rfn,
                                        String display, CodegenColumnHtmlTypeEnum type) {
            return map(lfn, rfn, display, type, null);
        }

        public RuleSetBuilder<L, R> map(SFunction<L, ?> lfn, SFunction<R, ?> rfn,
                                        String display, CodegenColumnHtmlTypeEnum type,
                                        Consumer<DiffRule.Builder> custom) {
            DiffRule.Builder b = new DiffRule.Builder()
                    .leftPath(LambdaUtils.resolveFieldName(lfn))
                    .rightPath(LambdaUtils.resolveFieldName(rfn))
                    .displayName(display)
                    .htmlType(type);
            if (custom != null) custom.accept(b);
            last = b.build();
            list.add(last);
            return this;
        }

        public RuleSetBuilder<L, R> lastRule(Consumer<DiffRule.Builder> custom) {
            if (last == null) return this;
            DiffRule.Builder b = new DiffRule.Builder()
                    .leftPath(last.leftPath).rightPath(last.rightPath)
                    .displayName(last.displayName).htmlType(last.htmlType)
                    .whenSpel(last.whenSpel).comparator(last.comparator)
                    .formatter(last.formatter).collectionKeyPath(last.collectionKeyPath);
            custom.accept(b);
            list.set(list.size() - 1, last = b.build());
            return this;
        }

        public void register() {
            RULESETS.put(name, List.copyOf(list));
        }
    }
}
