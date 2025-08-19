package com.kronos.diffflow.support;

import com.kronos.diffflow.model.DiffItem;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhangyh
 * @Date 2025/8/19 11:07
 * @desc 集合对比策略（按 key 提取）
 */
public final class ByKeyCollectionStrategy implements CollectionStrategy {
    @Override
    public List<DiffItem> diffCollections(String display, Collection<?> left, Collection<?> right,
                                          Function<Object, Object> keyExtractor, Function<Object, String> toString) {
        Map<Object, Object> L = left == null ? Collections.emptyMap() : left.stream().collect(Collectors.toMap(keyExtractor, x -> x, (a, b) -> b, LinkedHashMap::new));
        Map<Object, Object> R = right == null ? Collections.emptyMap() : right.stream().collect(Collectors.toMap(keyExtractor, x -> x, (a, b) -> b, LinkedHashMap::new));
        List<DiffItem> out = new ArrayList<>();
        // 删除
        for (Object k : diffKeys(L.keySet(), R.keySet())) {
            out.add(new DiffItem(display + "[-" + k + "]", display, toString.apply(L.get(k)), null));
        }
        // 新增
        for (Object k : diffKeys(R.keySet(), L.keySet())) {
            out.add(new DiffItem(display + "[+" + k + "]", display, null, toString.apply(R.get(k))));
        }
        // 变更（这里只做整体 toString 级别的判等；如需更细可递归规则）
        for (Object k : inter(L.keySet(), R.keySet())) {
            Object lv = L.get(k), rv = R.get(k);
            if (!Objects.equals(lv, rv))
                out.add(new DiffItem(display + "[~" + k + "]", display, toString.apply(lv), toString.apply(rv)));
        }
        return out;
    }

    private <T> Collection<T> diffKeys(Collection<T> A, Collection<T> B) {
        List<T> o = new ArrayList<>(A);
        o.removeAll(B);
        return o;
    }

    private <T> Collection<T> inter(Collection<T> A, Collection<T> B) {
        List<T> o = new ArrayList<>(A);
        o.retainAll(B);
        return o;
    }
}
