package com.github.shy526.devenvarrange.rpn.oo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


/**
 * @author shy526
 */
@Data
public class OperateResult {
    private List<OperateItem> result;
    private List<OperateItem> operand;

    private Boolean success = false;

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

    public static OperateResult of(List<OperateItem> operand, boolean success, Object... results) {
        if (success) {
            List<OperateItem> operateItems = new ArrayList<>();
            for (Object result : results) {
                operateItems.add(OperateItem.ofVal(result));
            }
            return success(operateItems, operand);
        }
        return fail(operand);
    }

    public static OperateResult of(List<OperateItem> operand, boolean success) {
        if (success) {
            return success(null, operand);
        }
        return fail(operand);
    }

}
