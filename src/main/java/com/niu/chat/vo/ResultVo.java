package com.niu.chat.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Auth justinniu
 * @Date 2018/12/7
 * @Desc
 */
@Data
public class ResultVo<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer code;

    private String msg;

    private T data;
}
