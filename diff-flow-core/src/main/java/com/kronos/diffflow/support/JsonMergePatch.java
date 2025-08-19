package com.kronos.diffflow.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;

/**
 * @author zhangyh
 * @Date 2025/8/19 11:10
 * @desc
 */
public final class JsonMergePatch implements Patch {
    private final ObjectMapper om = new ObjectMapper();
    private final ObjectNode   patch;
    JsonMergePatch(ObjectNode patch){ this.patch = patch; }

    @Override
    public <T> T applyTo(T current, Class<T> type) {
        // 将 current → ObjectNode，做深度合并（RFC7396）
        ObjectNode base = om.valueToTree(current);
        ObjectNode merged = deepMerge(base, patch);
        try { return om.treeToValue(merged, type); } catch (Exception e){ throw new IllegalStateException(e); }
    }

    @Override public Patch merge(Patch other) {
        if (!(other instanceof JsonMergePatch)) return this;
        ObjectNode copy = patch.deepCopy();
        ObjectNode right = ((JsonMergePatch) other).patch;
        return new JsonMergePatch(deepMerge(copy, right));
    }

    @Override public String asJson() { return patch.toString(); }

    private ObjectNode deepMerge(ObjectNode left, ObjectNode right){
        Iterator<String> it = right.fieldNames();
        while(it.hasNext()){
            String f = it.next();
            JsonNode rv = right.get(f);
            if (rv.isNull()) { left.set(f, rv); continue; }
            JsonNode lv = left.get(f);
            if (lv!=null && lv.isObject() && rv.isObject()) {
                left.set(f, deepMerge((ObjectNode) lv, (ObjectNode) rv));
            } else {
                left.set(f, rv);
            }
        }
        return left;
    }
}
