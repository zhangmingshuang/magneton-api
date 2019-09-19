package com.magneton.api2.core.scan;

import java.nio.file.Path;

/**
 * @author zhangmingshuang
 * @since 2019/9/18
 */
public interface FileCollector {
    void chuck(Path path);
}
