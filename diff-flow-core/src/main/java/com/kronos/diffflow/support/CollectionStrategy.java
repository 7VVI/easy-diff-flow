package com.kronos.diffflow.support;

import com.kronos.diffflow.model.DiffItem;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * @author zhangyh
 * @Date 2025/8/19 9:38
 * @desc
 */
public interface CollectionStrategy {
    // 按某个key抽取（如 id）对集合进行对比，输出元素级Diff
    List<DiffItem> diffCollections(String display, Collection<?> left, Collection<?> right,
                                   Function<Object, Object> keyExtractor, Function<Object, String> toString);
}
