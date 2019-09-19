package com.magneton.api2.core.requestmapping;

import com.magneton.api2.core.spi.Spi;
import com.sun.javadoc.ClassDoc;

/**
 * @author zhangmingshuang
 * @since 2019/9/19
 */
public interface RequestMappingBuilder extends Spi {

    String getRequestMapping(ClassDoc classDoc);

    String getRequestMethod(ClassDoc classDoc);
}
