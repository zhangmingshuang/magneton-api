package com.magneton.api2.core;

import com.magneton.api2.builder.doc.Apis;
import com.magneton.api2.builder.doc.ClassDocCollector;
import com.magneton.api2.command.ApiCommand;
import com.magneton.api2.command.CommonApiCommand;
import com.magneton.api2.spi.Spi;
import com.magneton.api2.generater.ApiFileGenerater;
import com.magneton.api2.scanner.FileCollector;
import com.magneton.api2.scanner.FilterCollector;

/**
 * 真正执行JAVADOC解析的实现类
 *
 * @author zhangmingshuang
 * @since 2019/9/12
 */
public interface ApiWorker extends Spi {

    /**
     * 子命令
     */
    ApiCommand apiWorkCommand();

    ApiFileGenerater createGenerateFiles(Apis apis);

    default FileCollector[] fileCollector() {
        return null;
    }

    default FilterCollector[] filterCollector() {
        return null;
    }

    default ClassDocCollector classDocCollector() {
        return null;
    }

    default void afterApiCommanderSet(CommonApiCommand commonApiCommander) {

    }
}
