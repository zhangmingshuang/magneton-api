package com.magneton.api2.util;

import com.magneton.api2.core.ApiConstant;

import java.io.UnsupportedEncodingException;

/**
 * @author zhangmingshuang
 * @since 2019/6/18
 */
public class ApiLog {

    public static void error(Object info) {
        if (info instanceof String) {
            println("[error] " + info);
            return;
        }
        println("[error] " + info.toString());
    }

    public static void out(Object info) {
        if (info instanceof String) {
            println((String) info);
            return;
        }
        println(info.toString());
    }

    private static void println(String info) {
        System.out.println(info);
    }
}
