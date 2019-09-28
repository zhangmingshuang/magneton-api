package com.magneton.api2.scanner;

import java.nio.file.Path;

/**
 * @author zhangmingshuang
 * @since 2019/9/28
 */
public interface FilterCollector {

    /**
     * 设置文件采集器是否只作用于主要文件
     */
    default boolean primariesTypeOnly() {
        return false;
    }

    void chuck(Path path);
}
