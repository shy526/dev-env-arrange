package com.github.shy526.devenvarrange.rpn.oo;

import lombok.Data;

import java.util.List;


@Data
public class OperateResult {
    private List<OperateItem> result;
    private List<OperateItem> operand;

    private boolean success = false;

    public OperateResult(List<OperateItem> result, List<OperateItem> operand, boolean success) {
        this.result = result;
        this.operand = operand;
        this.success = success;
    }

    public static OperateResult success(List<OperateItem> result, List<OperateItem> operand) {
        return new OperateResult(result, operand, true);
    }

    public static OperateResult fail(List<OperateItem> operand) {
        return new OperateResult(null, operand, false);
    }

    public static OperateResult of(List<OperateItem> result, List<OperateItem> operand, boolean success) {
        if (success) {
            return fail(operand);
        }
        return success(result, operand);
    }
}
