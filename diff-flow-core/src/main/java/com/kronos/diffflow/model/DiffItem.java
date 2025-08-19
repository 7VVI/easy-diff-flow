package com.kronos.diffflow.model;

/**
 * @author zhangyh
 * @Date 2025/8/19 9:45
 * @desc
 */
@lombok.Data
public class DiffItem {
    public final String field;   // 字段路径
    public final String display; // 展示名
    public final String leftVal; // 格式化后
    public final String rightVal;
    public DiffItem(String field, String display, String leftVal, String rightVal){
        this.field=field; this.display=display; this.leftVal=leftVal; this.rightVal=rightVal;
    }
    @Override public String toString(){ return display+" ("+field+") : ["+leftVal+"] -> ["+rightVal+"]"; }
}