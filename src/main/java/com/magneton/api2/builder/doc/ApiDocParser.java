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


    private boolean isIgnore(List<ApiComment> apiComments) {
        List<String> ignore = builder.getIgnore();
        if (ignore == null || ignore.size() < 1) {
            return false;
        }
        for (String flag : ignore) {
            if (apiComments == null || apiComments.size() < 1) {
                continue;
            }
            ApiComment apiComment = apiComments.get(0);
            if (apiComment == null) {
                continue;
            }
            String text = apiComment.getText();
            if (text != null && text.toLowerCase().indexOf(flag.toLowerCase()) != -1) {
                return true;
            }
        }
        return false;
    }

    protected Apis doParse(List<ClassDoc> classDocs) {
        List<ApiClassDoc> apiClasses = new ArrayList<>();
        List<SeeTag> apiSeeClasses = new ArrayList<>();
        ClassDocCollector classDocCollector = builder.getClassDocCollector();
        ClassDocFilter classDocFilter = builder.getClassDocFilter();
        MethodDocFilter methodDocFilter = builder.getMethodDocFilter();
        ParamDocFilter paramDocFilter = builder.getParamDocFilter();
        List<String> ignore = builder.getIgnore();

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
                Iterator<ApiMethodDoc> methodDocIterator = apiMethods.iterator();
                for (; methodDocIterator.hasNext(); ) {
                    ApiMethodDoc apiMethodDoc = methodDocIterator.next();
                    if (methodDocFilter != null
                        && methodDocFilter.filter(apiMethodDoc, apiMethodDoc.getMethodDoc())) {
                        methodDocIterator.remove();
                        continue;
                    }
                    if (isIgnore(apiMethodDoc.getApiComments())) {
                        methodDocIterator.remove();
                        continue;
                    }
                }//end method iterator

                for (ApiMethodDoc apiMethodDoc : apiMethods) {
                    List<ApiParamDoc> apiParamDocs = apiMethodDoc.getApiParamDocs();
                    if (apiParamDocs == null || apiParamDocs.isEmpty()) {
                        continue;
                    }
                    Iterator<ApiParamDoc> iterator = apiParamDocs.iterator();
                    for (; iterator.hasNext(); ) {
                        ApiParamDoc apiParamDoc = iterator.next();
                        if (paramDocFilter != null
                            && paramDocFilter.filter(apiParamDoc, apiParamDoc.getParamTag())) {
                            iterator.remove();
                            continue;
                        }
                        if (isIgnore(apiParamDoc.getApiComments())) {
                            iterator.remove();
                            continue;
                        }

                        List<ApiFieldDoc> links = apiParamDoc.getLinks();
                        if (links != null) {
                            Iterator<ApiFieldDoc> apiFieldDocIterator = links.iterator();
                            for (; apiFieldDocIterator.hasNext(); ) {
                                ApiFieldDoc apiFieldDoc = apiFieldDocIterator.next();
                                if (isIgnore(apiFieldDoc.getApiComments())) {
                                    apiFieldDocIterator.remove();
                                    continue;
                                }

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
