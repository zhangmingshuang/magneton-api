package com.magneton.api2.builder.doc;

import com.sun.javadoc.*;
import java.util.*;

/**
 * 基础解析器，将JAVADOC解析为框架通用的格式
 *
 * @author zhangmingshuang
 * @since 2019/9/12
 */
public class ApiDocParser {

    private ApiDocParserBuilder builder;

    public ApiDocParser(ApiDocParserBuilder builder) {
        this.builder = builder;
    }

    public Apis parse(List<ClassDoc> classDocs) {
        return this.doParse(classDocs);
    }

    protected Apis doParse(List<ClassDoc> classDocs) {
        List<ApiClassDoc> apiClasses = new ArrayList<>();
        ClassDocCollector classDocCollector = builder.getClassDocCollector();
        ClassDocFilter classDocFilter = builder.getClassDocFilter();
        MethodDocFilter methodDocFilter = builder.getMethodDocFilter();
        ParamDocFilter paramDocFilter = builder.getParamDocFilter();
        for (ClassDoc classDoc : classDocs) {
            ApiClassDoc apiClass = ApiClassDoc.parseApiClass(classDoc);
            if (apiClass == null) {
                continue;
            }

            if (classDocCollector != null) {
                classDocCollector.collect(apiClass, classDoc);
            }
            if (classDocFilter != null && classDocFilter.filter(apiClass, classDoc)) {
                continue;
            }

            List<ApiMethodDoc> apiMethods = ApiMethodDoc.parseApiMethods(classDoc);

            if (apiMethods != null && apiMethods.size() > 0) {
                if (methodDocFilter != null) {
                    Iterator<ApiMethodDoc> iterator = apiMethods.iterator();
                    for (; iterator.hasNext(); ) {
                        ApiMethodDoc apiMethodDoc = iterator.next();
                        if (methodDocFilter.filter(apiMethodDoc, apiMethodDoc.getMethodDoc())) {
                            apiMethods.remove(apiMethodDoc);
                        }
                    }
                }

                if (paramDocFilter != null) {
                    Iterator<ApiMethodDoc> iterator = apiMethods.iterator();
                    for (; iterator.hasNext(); ) {
                        ApiMethodDoc apiMethodDoc = iterator.next();
                        List<ApiParamDoc> apiParamDocs = apiMethodDoc.getApiParamDocs();
                        if (apiParamDocs == null || apiParamDocs.isEmpty()) {
                            continue;
                        }
                        Iterator<ApiParamDoc> paramDocIterator = apiParamDocs.iterator();
                        for (; paramDocIterator.hasNext(); ) {
                            ApiParamDoc apiParamDoc = paramDocIterator.next();
                            if (paramDocFilter.filter(apiParamDoc, apiParamDoc.getParamTag())) {
                                apiParamDocs.remove(apiParamDoc);
                            }
                        }
                    }
                }
            }
            apiClass.setApiMethodDocs(apiMethods);
            apiClasses.add(apiClass);
        }
        Apis apis = new Apis();
        apis.setApiClasses(apiClasses);
        return apis;
    }

}
