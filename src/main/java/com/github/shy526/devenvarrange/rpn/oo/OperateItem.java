package com.github.shy526.devenvarrange.rpn.oo;

import lombok.Data;


@Data
public class OperateItem {
    private Object val;
    private OperateType type;

    public OperateItem(Object val, OperateType type) {
        this.val = val;
        this.type = type;
    }

    public static OperateItem of(Object value, OperateType type) {
        return new OperateItem(value, type);
    }

    public <T> T getVal(Class<T> tClass) {
        if (tClass.isInstance(val)) {
            return (T) val;
        }
        throw new RuntimeException("val error");
    }

    @Override
    public String toString() {
        return val.toString();
    }
}
