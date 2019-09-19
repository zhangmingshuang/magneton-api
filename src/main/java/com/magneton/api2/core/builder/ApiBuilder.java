package com.magneton.api2.core.builder;

import com.magneton.api2.core.apiclassdoc.Apis;
import com.magneton.api2.core.scan.HFiles;

/**
 * API构建器
 *
 * @author zhangmingshuang
 * @since 2019/9/12
 */
public interface ApiBuilder {

    Apis build(HFiles hFiles);
}
