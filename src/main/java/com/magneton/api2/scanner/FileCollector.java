package com.magneton.api2.scanner;

import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 * @author zhangmingshuang
 * @since 2019/9/18
 */
public interface FileCollector {

    /**
     * 设置文件采集器是否只作用于主要文件
     */
    default boolean primariesTypeOnly() {
        return false;
    }

    /**
     * 正则
     *
     * @return 如果返回null则表示不参与采集
     */
    Pattern[] patterns();

    void collect(Path path);
}
