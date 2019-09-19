package com.magneton.api2.core;

import com.magneton.api2.core.scan.AbstractFileScanner;

import java.util.regex.Pattern;

/**
 * @author zhangmingshuang
 * @since 2019/8/19
 */
public class FileFiltrationScanner extends AbstractFileScanner {

    @Override
    public Pattern primariesType() {
        return Api.SCAN_FILE_FILTER;
    }

    @Override
    public Pattern subordinatesType() {
        return Pattern.compile("\\.java");
    }
}
