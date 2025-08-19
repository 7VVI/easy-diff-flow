package com.kronos.diffflow.support;

import com.kronos.diffflow.model.DiffItem;
import com.kronos.diffflow.support.function.FieldComparator;
import com.kronos.diffflow.support.function.FieldFormatter;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * @author zhangyh
 * @Date 2025/8/19 11:10
 * @desc
 */
public class DiffRuntime {
    private final PathAccessor       accessor           = new ReflectivePathAccessor();
    private final ComparatorResolver comparatorResolver = new DefaultComparatorResolver();
    private final FormatterResolver  formatterResolver  = new DefaultFormatterResolver();
    private final CollectionStrategy collectionStrategy = new ByKeyCollectionStrategy();
    private final ExpressionParser   spel               = new SpelExpressionParser();

    public List<DiffItem> diff(Object left, Object right, List<DiffRule> rules, DiffContext ctx){
        List<DiffItem> out = new ArrayList<>();
        for (DiffRule r: rules){
            if (r.whenSpel != null && !r.whenSpel.isEmpty()){
                if (!evalWhen(r.whenSpel, left, right, ctx)) continue;
            }
            Object lv = accessor.read(left, r.leftPath);
            Object rv = accessor.read(right, r.rightPath);

            // 集合字段
            if (r.collectionKeyPath != null && !r.collectionKeyPath.isEmpty() && (lv instanceof Collection || rv instanceof Collection)){
                String keyPath = extractKeyPath(r.collectionKeyPath); // e.g. "items[].id" → "id"
                Function<Object,Object> keyFn = (o) -> o==null? null : new ReflectivePathAccessor().read(o, keyPath);
                out.addAll(collectionStrategy.diffCollections(r.displayName, (Collection<?>) lv, (Collection<?>) rv, keyFn, x-> String.valueOf(x)));
                continue;
            }

            FieldComparator cmp = comparatorResolver.resolve(r, typeOf(left, r.leftPath));
            if (cmp.isDiff(lv, rv)){
                FieldFormatter fmt = formatterResolver.resolve(r, typeOf(left, r.leftPath));
                out.add(new DiffItem(r.leftPath, r.displayName, fmt.format(lv), fmt.format(rv)));
            }
        }
        return out;
    }

    private boolean evalWhen(String spelExpr, Object left, Object right, DiffContext ctx){
        EvaluationContext c = new StandardEvaluationContext();
        c.setVariable("left", left); c.setVariable("right", right); c.setVariable("ctx", ctx);
        Boolean ok = spel.parseExpression(spelExpr).getValue(c, Boolean.class);
        return Boolean.TRUE.equals(ok);
    }

    private Class<?> typeOf(Object root, String path){
        Object v = new ReflectivePathAccessor().read(root, path);
        return v==null? Object.class : v.getClass();
    }

    private String extractKeyPath(String collectionKeyPath){
        // 形如 items[].id 或 details[].sku.code → 提取 [] 后面的部分
        int p = collectionKeyPath.indexOf("[]");
        if (p<0) return collectionKeyPath;
        String tail = collectionKeyPath.substring(p+3); // skip [].
        return tail;
    }
}
