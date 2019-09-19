package com.magneton.api2.core.requestmapping;

import com.magneton.api2.core.spi.SpiServices;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;

/**
 * @author zhangmingshuang
 * @since 2019/9/19
 */
public class RequestMappingBuilderChain {

    public static RequestMappingBuilder getWithAnnotation(ClassDoc classDoc) {
        AnnotationDesc[] annotations = classDoc.annotations();
        for (AnnotationDesc annotation : annotations) {
            String annotationTypeName = annotation.annotationType().qualifiedTypeName();
            RequestMappingBuilder requestMappingBuilder
                    = SpiServices.getService(RequestMappingBuilder.class, annotationTypeName);
            if (requestMappingBuilder == null) {
                continue;
            }
            return requestMappingBuilder;
        }
        return null;
    }
}
