package com.kronos.diffflow.support;

import com.kronos.diffflow.model.DiffItem;

import java.util.List;

/**
 * @author zhangyh
 * @Date 2025/8/19 11:15
 * @desc
 */
public class DiffHolder {
    private static final ThreadLocal<List<DiffItem>> TL = new ThreadLocal<>();

    public static void set(List<DiffItem> items) {
        TL.set(items);
    }

    public static List<DiffItem> getAndClear() {
        List<DiffItem> v = TL.get();
        TL.remove();
        return v;
    }
}
