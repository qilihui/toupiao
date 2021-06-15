package com.example.demo.config;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author qilihui
 * @date 2021/6/15 22:15
 */
@Data
@AllArgsConstructor
public class R {
    private Integer code;
    private String msg;
    private Object data;

    public static R success(Object data) {
        return new R(200, "", data);
    }
}
