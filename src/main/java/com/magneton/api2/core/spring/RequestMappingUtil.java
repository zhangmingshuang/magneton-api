package com.magneton.api2.core.spring;

import com.magneton.api2.util.ApiLog;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.AnnotationValue;

/**
 * @author zhangmingshuang
 * @since 2019/6/19
 */
public class RequestMappingUtil {

    private static final String REQUEST_MAPPING = "REQUESTMAPPING";
    private static final String GET_MAPPING = "GETMAPPING";
    private static final String POST_MAPPING = "POSTMAPPING";
    private static final String PUT_MAPPING = "PUTMAPPING";
    private static final String DELETE_MAPPING = "DELETEMAPPING";
    private static final String PATCH_MAPPING = "PATCHMAPPING";

    public static void parseValueTo(RequestMapping requestMapping,
                                    AnnotationDesc.ElementValuePair[] elementValuePairs) {
        for (AnnotationDesc.ElementValuePair pair : elementValuePairs) {
            String name = pair.element().name();
            AnnotationValue value = pair.value();
            switch (name.toUpperCase()) {
                case "VALUE":
                case "PATH":
                    AnnotationValue[] values = (AnnotationValue[]) value.value();
                    requestMapping.setPath(toStringArray(values));
                    break;
            }
        }
    }

    public static String[] toStringArray(AnnotationValue[] values) {
        String[] array = new String[values.length];
        for (int i = 0, l = values.length; i < l; ++i) {
            array[i] = values[i].value().toString();
        }
        return array;
    }

    public static String[] parseSupportMethods(String name) {
        switch (name.toUpperCase()) {
            case REQUEST_MAPPING:
                return new String[]{"ALL"};
            case GET_MAPPING:
                return new String[]{RequestMethod.GET.name()};
            case POST_MAPPING:
                return new String[]{RequestMethod.POST.name()};
            case PUT_MAPPING:
                return new String[]{RequestMethod.PUT.name()};
            case DELETE_MAPPING:
                return new String[]{RequestMethod.DELETE.name()};
            case PATCH_MAPPING:
                return new String[]{RequestMethod.PATCH.name()};
            default:
                //未解析到可用的Servlet请求地址配置
                return null;
        }
    }


    /**
     * 构建Spring中的RequestMapping数据
     *
     * @param annotations AnnotationDesc
     * @return RequestMapping
     */
    public static final RequestMapping parseServletMapping(AnnotationDesc[] annotations) {

        for (AnnotationDesc desc : annotations) {
            AnnotationTypeDoc annotationTypeDoc = desc.annotationType();
            String name = annotationTypeDoc.name();
            if (!"RequestMapping".equals(name)) {
                continue;
            }
            RequestMapping requestMapping = new RequestMapping();

            String[] supportMethods = RequestMappingUtil.parseSupportMethods(name);
            if (supportMethods == null) {
                continue;
            }
            requestMapping.setMethod(supportMethods);
//            AnnotationDesc.ElementValuePair[] elementValuePairs = desc.elementValues();
//            for (AnnotationDesc.ElementValuePair elementValuePair : elementValuePairs) {
//                Control.out("RequestMapping name=" + elementValuePair.element().name()
//                        + ", value=" + elementValuePair.value().value());
//            }
            if (desc.elementValues().length < 1) {

                String qualifiedTypeName = desc.annotationType().qualifiedTypeName();
                ApiLog.out("不支持的" + qualifiedTypeName + "解析");
            }
            RequestMappingUtil.parseValueTo(requestMapping, desc.elementValues());
            return requestMapping;
        }
        return null;
    }
}
