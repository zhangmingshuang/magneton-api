package com.magneton.api2.util;

/**
 * @author zhangmingshuang
 * @since 2019/11/25
 */
public class StringUtil {

    public static final boolean isEmpty(CharSequence charSequence) {
        return charSequence == null || charSequence.length() < 1;
    }
}
