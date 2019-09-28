package com.magneton.api2.core.requestmapping;

import com.magneton.api2.spi.SpiServices;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;

/**
 * @author zhangmingshuang
 * @since 2019/9/19
 */
public class RequestMappingBuilderChain {

    public static RequestMappingBuilder getWithAnnotation(MethodDoc methodDoc) {
        AnnotationDesc[] annotations = methodDoc.annotations();
        return getWithAnnotation(annotations);
    }

    public static RequestMappingBuilder getWithAnnotation(ClassDoc classDoc) {
        AnnotationDesc[] annotations = classDoc.annotations();
        return getWithAnnotation(annotations);
    }

    public static RequestMappingBuilder getWithAnnotation(AnnotationDesc[] annotationDescs) {
        for (AnnotationDesc annotation : annotationDescs) {
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
