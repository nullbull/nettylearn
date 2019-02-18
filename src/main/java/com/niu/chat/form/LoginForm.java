package com.niu.chat.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author 牛贞昊（niuzhenhao@58.com）
 * @date 2019/2/18 13:15
 * @desc
 */
@Data
public class LoginForm {
    @NotEmpty(message = "用户名不能为空")
    private String fUserName;

    @NotEmpty(message = "密码不能为空")
    private String fPassWord;
}
