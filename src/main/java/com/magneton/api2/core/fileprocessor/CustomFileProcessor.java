package com.magneton.api2.core.fileprocessor;

import com.magneton.api2.core.spi.Spi;

import java.nio.file.Path;

/**
 * @author zhangmingshuang
 * @since 2019/9/12
 */
public interface CustomFileProcessor extends Spi {

    void injectFile(Path filePath);

    String getFileContext();

}
