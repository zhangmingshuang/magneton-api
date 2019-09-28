package com.magneton.api2.scanner;

/**
 * @author zhangmingshuang
 * @since 2019/9/18
 */
public enum HFileType {

    JAVA(".java");

    private final String endpoint;

    HFileType(String endpoint) {
        this.endpoint = endpoint;
    }

    public String endpoint() {
        return endpoint;
    }
}
