package com.kronos.diffflow.support;

import com.kronos.diffflow.model.DiffItem;

/**
 * @author zhangyh
 * @Date 2025/8/19 9:45
 * @desc
 */

public final class DiffEngine {

    // 反射/路径读取，支持嵌套：a.b.c（也可替换为 BeanPath / Jackson JsonPointer）
    private static Object readPath(Object root, String path) {
        if (root == null || path == null || path.isEmpty()) return null;
        String[] parts = path.split("\\.");
        Object cur = root;
        for (String p : parts) {
            if (cur == null) return null;
            try {
                java.beans.PropertyDescriptor pd =
                        new java.beans.PropertyDescriptor(p, cur.getClass());
                cur = pd.getReadMethod().invoke(cur);
            } catch (Exception e) {
                throw new IllegalStateException("Read path failed: " + path, e);
            }
        }
        return cur;
    }

    public static java.util.List<DiffItem> diff(Object left, Object right, java.util.List<DiffRule> rules) {
        java.util.List<DiffItem> out = new java.util.ArrayList<>();
        for (DiffRule r : rules) {
            Object lv = readPath(left, r.getLeftPath());
            Object rv = readPath(right, r.getRightPath());
            if (r.getComparator().isDiff(lv, rv)) {
                out.add(new DiffItem(
                        r.getLeftPath(),
                        r.getDisplayName(),
                        r.getFormatter().format(lv),
                        r.getFormatter().format(rv)
                ));
            }
        }
        return out;
    }
}
