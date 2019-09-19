package com.magneton.api2.core.reverseproxy;

import com.magneton.api2.core.ApiFile;
import com.magneton.api2.core.spi.Spi;

import java.nio.file.Path;

/**
 * @author zhangmingshuang
 * @since 2019/9/19
 */
public interface ApiReverseProxy extends Spi {
    ApiFile createApiFile(int port, String reverse, Path folder);
}
