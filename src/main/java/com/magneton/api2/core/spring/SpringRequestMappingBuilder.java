package com.magneton.api2.core.spring;

import com.magneton.api2.core.requestmapping.RequestMappingBuilder;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;

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
    public String[] getRequestMapping(ClassDoc classDoc) {
        RequestMapping requestMapping
            = RequestMappingUtil.parseServletMapping(classDoc.annotations());
        return requestMapping.getPath();
    }

    @Override
    public String getRequestMethod(ClassDoc classDoc) {
        RequestMapping requestMapping
            = RequestMappingUtil.parseServletMapping(classDoc.annotations());
        return requestMapping.getMethod()[0];
    }

    @Override
    public String[] getRequestMapping(MethodDoc methodDoc) {
        RequestMapping requestMapping
            = RequestMappingUtil.parseServletMapping(methodDoc.annotations());
        return requestMapping.getPath();
    }

    @Override
    public String getRequestMethod(MethodDoc methodDoc) {
        RequestMapping requestMapping = RequestMappingUtil
            .parseServletMapping(methodDoc.annotations());
        return requestMapping.getMethod()[0];
    }
}
