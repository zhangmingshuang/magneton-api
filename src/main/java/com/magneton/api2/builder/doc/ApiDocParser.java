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
        List<SeeTag> apiSeeClasses = new ArrayList<>();
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

            List<SeeTag> sees = apiClass.getSees();
            if (sees != null) {
                apiSeeClasses.addAll(sees);
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
                    for (ApiMethodDoc apiMethodDoc : apiMethods) {
                        List<ApiParamDoc> apiParamDocs = apiMethodDoc.getApiParamDocs();
                        if (apiParamDocs == null || apiParamDocs.isEmpty()) {
                            continue;
                        }
                        Iterator<ApiParamDoc> iterator = apiParamDocs.iterator();
                        for (; iterator.hasNext(); ) {
                            ApiParamDoc apiParamDoc = iterator.next();
                            if (paramDocFilter.filter(apiParamDoc, apiParamDoc.getParamTag())) {
                                iterator.remove();
                            }
                        }
                    }
                }

                for (ApiMethodDoc apiMethod : apiMethods) {
                    sees = apiMethod.getSees();
                    if (sees != null) {
                        apiSeeClasses.addAll(sees);
                    }
                }
            }
            apiClass.setApiMethodDocs(apiMethods);
            apiClasses.add(apiClass);
        }
        Apis apis = new Apis();
        if (!apiSeeClasses.isEmpty()) {
            List<ApiClassDoc> seeClasses = new ArrayList<>();
            seeLoop:
            for (SeeTag see : apiSeeClasses) {
                ClassDoc classDoc = see.referencedClass();
                if (classDoc == null) {
                    continue;
                }
                String name = classDoc.qualifiedTypeName();
                for (ApiClassDoc apiClass : apiClasses) {
                    if (apiClass.getQualifiedTypeName().equals(name)) {
                        continue seeLoop;
                    }
                }
                ApiClassDoc apiClassDoc = ApiClassDoc.parseApiClass(classDoc);
                seeClasses.add(apiClassDoc);
            }
            apis.setApiSessClasses(seeClasses);
        }
        apis.setApiClasses(apiClasses);
        return apis;
    }

}
