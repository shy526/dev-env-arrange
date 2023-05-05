package com.github.shy526.devenvarrange.rpn.env;

import com.github.shy526.regedit.AbsRegOperate;
import com.github.shy526.regedit.CmdRegOperate;
import com.github.shy526.regedit.RegOperate;
import com.github.shy526.regedit.obj.RegRootEnum;



public abstract class AbsEnv implements EnvSymbol {
    private static final RegOperate regOperate = new CmdRegOperate(RegRootEnum.HKEY_LOCAL_MACHINE, AbsRegOperate.SYS_ENVIRONMENT);
    protected RegOperate getRegOperate(){
        return regOperate;
    }
}
