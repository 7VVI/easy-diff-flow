package com.kronos.diffflow.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

/**
 * @author zhangyh
 * @Date 2025/8/19 11:13
 * @desc
 */
public class JsonPatchCodec implements PatchCodec {
    private final ObjectMapper om = new ObjectMapper();

    public Patch fromJson(String json) {
        try {
            return new JsonMergePatch((ObjectNode) om.readTree(json));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public Patch empty() {
        return new JsonMergePatch(new ObjectMapper().createObjectNode());
    }

    Patch fold(List<String> patchesJson) {
        Patch acc = empty();
        for (String pj : patchesJson) acc = acc.merge(fromJson(pj));
        return acc;
    }
}
