package com.magneton.api2.core.spi;

import com.magneton.api2.core.ApiFileGenerater;
import com.magneton.api2.core.apiclassdoc.Apis;

/**
 * 真正执行JAVADOC解析的实现类
 *
 * @author zhangmingshuang
 * @since 2019/9/12
 */
public interface ApiWorker extends Spi {
    ApiFileGenerater createGenerateFiles(Apis apis);
}
