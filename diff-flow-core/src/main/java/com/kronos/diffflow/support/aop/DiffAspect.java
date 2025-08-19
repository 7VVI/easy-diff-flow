package com.kronos.diffflow.support.aop;

import com.kronos.diffflow.model.DiffItem;
import com.kronos.diffflow.support.DiffContext;
import com.kronos.diffflow.support.DiffHolder;
import com.kronos.diffflow.support.DiffRegistry;
import com.kronos.diffflow.support.DiffRuntime;
import com.kronos.diffflow.support.annotations.Diff;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * @author zhangyh
 * @Date 2025/8/19 11:15
 * @desc
 */
@Aspect
@Component
class DiffAspect {
    private final ExpressionParser parser  = new SpelExpressionParser();
    private final DiffRuntime      runtime = new DiffRuntime();

    @Around("@annotation(diff)")
    public Object around(ProceedingJoinPoint pjp, Diff diff) throws Throwable {
        Object[] args = pjp.getArgs();
        EvaluationContext ctx = new StandardEvaluationContext();
        for (int i=0;i<args.length;i++){ ctx.setVariable("p"+i, args[i]); ctx.setVariable("arg"+i, args[i]); }

        List<DiffItem> items;
        if (diff.computeBeforeProceed()){
            Object left = parser.parseExpression(diff.left()).getValue(ctx);
            Object right = parser.parseExpression(diff.right()).getValue(ctx);
            items = runtime.diff(left, right, DiffRegistry.get(diff.ruleSet()), new DiffContext(currentUser(), null, Instant.now()));
            DiffHolder.set(items);
            return pjp.proceed();
        } else {
            Object ret = pjp.proceed();
            // proceed 后，如需从返回值取新对象，可把返回值放入 ctx
            ctx.setVariable("ret", ret);
            Object left = parser.parseExpression(diff.left()).getValue(ctx);
            Object right = parser.parseExpression(diff.right()).getValue(ctx);
            items = runtime.diff(left, right, DiffRegistry.get(diff.ruleSet()), new DiffContext(currentUser(), null, Instant.now()));
            DiffHolder.set(items);
            return ret;
        }
    }
    private String currentUser(){ return "system"; }
}