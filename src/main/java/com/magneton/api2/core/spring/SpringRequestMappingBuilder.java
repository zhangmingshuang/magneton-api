package com.magneton.api2.core.spring;

import com.magneton.api2.core.requestmapping.RequestMappingBuilder;
import com.sun.javadoc.ClassDoc;

/**
 * @author zhangmingshuang
 * @since 2019/9/19
 */

public class SpringRequestMappingBuilder implements RequestMappingBuilder {
    @Override
    public String name() {
        return "org.springframework.web.bind.annotation.RequestMapping";
    }

    @Override
    public String getRequestMapping(ClassDoc classDoc) {
        RequestMapping requestMapping = RequestMappingUtil.parseServletMapping(classDoc.annotations());
        return requestMapping.getPath()[0];
    }

    @Override
    public String getRequestMethod(ClassDoc classDoc) {
        RequestMapping requestMapping = RequestMappingUtil.parseServletMapping(classDoc.annotations());
        return requestMapping.getMethod()[0];
    }
}
