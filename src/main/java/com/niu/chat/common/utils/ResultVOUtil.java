package com.niu.chat.common.utils;

import com.niu.chat.vo.ResultVo;

/**
 * @Auth justinniu
 * @Date 2018/12/7
 * @Desc
 */
public class ResultVOUtil {
    public static ResultVo success(Object object) {
        ResultVo resultVo = new ResultVo();
        resultVo.setData(object);
        resultVo.setCode(200);
        resultVo.setMsg("成功");
        return resultVo;
    }
    public static ResultVo success() {
        return success(null);
    }

    public static ResultVo error(Integer code, String msg) {
        ResultVo resultVo = new ResultVo();
        resultVo.setCode(code);
        resultVo.setMsg(msg);
        return resultVo;
    }
}
