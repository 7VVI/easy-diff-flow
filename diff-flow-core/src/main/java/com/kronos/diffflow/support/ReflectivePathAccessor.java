package com.kronos.diffflow.support;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhangyh
 * @Date 2025/8/19 11:07
 * @desc
 */
public class ReflectivePathAccessor implements PathAccessor{

    @Override public Object read(Object root, String path) {
        if (root == null || path == null || path.isEmpty()) return null;
        Object cur = root;
        for (String token : tokenize(path)) {
            if (cur == null) return null;
            cur = step(cur, token);
        }
        return cur;
    }
    private List<String> tokenize(String path){
        // 支持 items[0].name 与 map[theKey]
        List<String> out = new ArrayList<>();
        int i=0; StringBuilder sb=new StringBuilder();
        while(i<path.length()){
            char c=path.charAt(i);
            if(c=='.'){ out.add(sb.toString()); sb.setLength(0); i++; continue; }
            if(c=='['){
                // 收集直到 ']' 包含索引或key
                int j=path.indexOf(']', i); if(j<0) throw new IllegalArgumentException("Bad path: "+path);
                sb.append(path, i, j+1); i=j+1; continue;
            }
            sb.append(c); i++;
        }
        if(sb.length()>0) out.add(sb.toString());
        return out;
    }
    private Object step(Object cur, String token){
        // token 可能是 name、name[2]、name[key]
        String name = token; String bracket=null;
        int bi = token.indexOf('[');
        if (bi>=0){ name = token.substring(0, bi); bracket = token.substring(bi); }
        if (!name.isEmpty()) cur = readProp(cur, name);
        if (bracket!=null){
            if(!(bracket.startsWith("[") && bracket.endsWith("]"))) throw new IllegalArgumentException("Bad token: "+token);
            String inner = bracket.substring(1, bracket.length()-1);
            if (isInteger(inner)){
                int idx = Integer.parseInt(inner);
                if (cur instanceof List) return idx>=0 && idx<((List<?>) cur).size()? ((List<?>) cur).get(idx): null;
                if (cur.getClass().isArray()) return idx>=0 && idx<java.lang.reflect.Array.getLength(cur)? java.lang.reflect.Array.get(cur, idx): null;
                return null;
            } else {
                if (cur instanceof Map) return ((Map<?,?>) cur).get(inner);
                return null;
            }
        }
        return cur;
    }
    private Object readProp(Object bean, String prop){
        try {
            PropertyDescriptor pd = new PropertyDescriptor(prop, bean.getClass());
            Method getter = pd.getReadMethod();
            return getter.invoke(bean);
        } catch (Exception e) { return null; }
    }
    private boolean isInteger(String s){ try { Integer.parseInt(s); return true; } catch(Exception e){ return false; } }
}
