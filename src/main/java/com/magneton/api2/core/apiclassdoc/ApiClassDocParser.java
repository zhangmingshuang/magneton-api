package com.magneton.api2.core.apiclassdoc;

import com.magneton.api2.util.ApiTagUtil;
import com.sun.javadoc.*;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javadoc.ProgramElementDocImpl;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 基础解析器，将JAVADOC解析为框架通用的格式
 *
 * @author zhangmingshuang
 * @since 2019/9/12
 */
public class ApiClassDocParser {

    private Set<String> paramFilters;

    public void setParamFilter(Set<String> paramFilters) {
        this.paramFilters = paramFilters;
    }

    public Apis parse(List<ClassDoc> classDocs) {
        return this.doParse(classDocs);
    }

    protected Apis doParse(List<ClassDoc> classDocs) {
        List<ApiClass> apiClasses = new ArrayList<>();
        for (ClassDoc classDoc : classDocs) {
            ApiClass apiClass = this.classDocParse(classDoc);
            apiClasses.add(apiClass);
        }
        Apis apis = new Apis();
        apis.setApiClasses(apiClasses);
        return apis;
    }

    private ApiClass classDocParse(ClassDoc classDoc) {
        ApiClass apiClass = this.doApiClassParse(classDoc);
        List<ApiMethod> apiMethods = this.doApiMethodParse(classDoc);
        apiClass.setApiMethods(apiMethods);
        return apiClass;
    }

    private List<ApiMethod> doApiMethodParse(ClassDoc classDoc) {
        MethodDoc[] methodDocs = classDoc.methods();
        List<ApiMethod> apiMethods = new ArrayList<>(methodDocs.length);
        for (MethodDoc methodDoc : methodDocs) {
            ApiMethod apiMethod = new ApiMethod();
            apiMethod.setMethodDoc(methodDoc);
            apiMethod.setSimpleName(methodDoc.name());
            apiMethod.setQualifiedTypeName(methodDoc.qualifiedName());
            //注释下的Tag
            Tag[] tags = methodDoc.tags();
            //所有的注释，包括Tag信息
            Tag[] inlineTags = methodDoc.inlineTags();
            List<ApiComment> apiComments = parseApiComments(inlineTags);
            apiMethod.setApiComments(apiComments);
            for (Tag tag : tags) {
                TagType tagType = TagType.getTag(tag.kind());
                this.apiMethodStriving(apiMethod, tagType, tag);
            }
            apiMethods.add(apiMethod);
        }
        return apiMethods;
    }

    private ApiClass doApiClassParse(ClassDoc classDoc) {
        ApiClass apiClass = new ApiClass();
        apiClass.setClassDoc(classDoc);
        apiClass.setSimpleName(classDoc.name());
        apiClass.setQualifiedTypeName(classDoc.qualifiedTypeName());
        //注释下的Tag
        Tag[] tags = classDoc.tags();
        //所有的注释，包括Tag信息
        Tag[] inlineTags = classDoc.inlineTags();
        List<ApiComment> apiComments = this.parseApiComments(inlineTags);
        apiClass.setApiComments(apiComments);
        for (Tag tag : tags) {
            TagType tagType = TagType.getTag(tag.kind());
            this.apiClassStriving(apiClass, tagType, tag);
        }
        return apiClass;
    }

    private void apiMethodStriving(ApiMethod apiMethod, TagType tagType, Tag tag) {
        //标准JAVADOC方法注释包括
        //@param 参数名 描述
        //@return 描述
        //@throws 异常类名
        //@deprecated 过期标识
        //@see 引用类、方法、变量等
        //@since 在什么版本开发支持
        //@link 链接
        switch (tagType) {
            case Param:
                ApiParam apiParam = this.parseApiMethodParam(apiMethod, (ParamTag) tag);
                if (apiParam != null) {
                    apiMethod.addApiParam(apiParam);
                }
                break;
            case Return:
                ApiReturn apiReturn = this.parseApiMEthodReturn(apiMethod, tag);
                if (apiReturn != null) {
                    apiMethod.setApiReturn(apiReturn);
                }
                break;
            case Throws:
                ThrowsTag throwsTag = (ThrowsTag) tag;
                ApiException apiException = new ApiException();
                apiException.setName(throwsTag.exceptionName());
                apiException.setApiComments(this.parseApiComments(throwsTag.inlineTags()));
                apiMethod.addApiException(apiException);
                break;
            case Deprecated:
                apiMethod.setDeprecated(true);
                break;
            case See:
                break;
            case Since:
                apiMethod.setSince(tag.text());
                break;
        }
    }

    private ApiReturn parseApiMEthodReturn(ApiMethod apiMethod, Tag tag) {
        Tag[] returnInlineTags = tag.inlineTags();

        List<ApiField> link = null;

        for (Tag returnInlineTag : returnInlineTags) {
            TagType tagType = TagType.getTag(returnInlineTag.kind());
            if (tagType != TagType.See) {
                continue;
            }
            //解析对象
            ClassDoc linkClass = ((SeeTag) returnInlineTag).referencedClass();
            if (linkClass != null) {
                link = this.parseClassApiField(linkClass);
                break;
            }
        }
        ApiReturn apiReturn = new ApiReturn();
        apiReturn.setTag(tag);
        apiReturn.setTypeName(this.parseMethodReturnType(apiMethod.getMethodDoc()));
        apiReturn.setApiComments(this.parseApiComments(tag.inlineTags()));
        apiReturn.setLink(link);
        return apiReturn;
    }

    private ApiParam parseApiMethodParam(ApiMethod apiMethod, ParamTag paramTag) {
        String paramName = paramTag.parameterName();
        List<JCTree.JCVariableDecl> methodArgs = this.getMethodArgs((MethodDoc) paramTag.holder());

        List<ApiField> link = null;
        String typeName = null;

        args:
        for (JCTree.JCVariableDecl methodArg : methodArgs) {
            if (!paramName.equals(methodArg.name.toString())) {
                continue;
            }
            typeName = ((JCTree.JCIdent) methodArg.vartype).name.toString();
            if (paramFilters != null
                    && paramFilters.contains(typeName.toLowerCase())) {
                return null;
            }
            //如果要解析成附加LINK属性对象
            Tag[] paramInlineTags = paramTag.inlineTags();
            for (Tag paramInlineTag : paramInlineTags) {
                TagType paramTagType = TagType.getTag(paramInlineTag.kind());
                if (paramTagType != TagType.See) {
                    continue;
                }
                //解析对象
                ClassDoc linkClass = ((SeeTag) paramInlineTag).referencedClass();
                if (linkClass != null) {
                    link = this.parseClassApiField(linkClass);
                }
                break args;
            }
        }
        ApiParam apiParam = new ApiParam();
        apiParam.setParamTag(paramTag);
        apiParam.setName(paramName);
        apiParam.setTypeName(typeName);
        apiParam.setLink(link);
        apiParam.setApiComments(this.parseApiComments(paramTag.inlineTags()));
        return apiParam;
    }

    private void apiClassStriving(ApiClass apiClass, TagType tagType, Tag tag) {
        //标准JAVADOC类注释包括
        //@author 作者标识
        //@version 版本号
        //@deprecated 过期标识
        //@see 引用类、方法、变量等
        //@since 在什么版本开发支持
        //@link 链接
        switch (tagType) {
            case Author:
                apiClass.setAuthor(tag.text());
                break;
            case Version:
                apiClass.setVersion(tag.text());
                break;
            case Deprecated:
                apiClass.setDeprecated(true);
                break;
            case See:
                break;
            case Since:
                apiClass.setSince(tag.text());
                break;
        }
    }

    protected List<ApiField> parseClassApiField(ClassDoc classDoc) {
        if (classDoc == null) {
            return Collections.EMPTY_LIST;
        }
        FieldDoc[] fields = classDoc.fields();
        if (fields == null || fields.length < 1) {
            return Collections.EMPTY_LIST;
        }
        List<ApiField> apiFields = new ArrayList<>(fields.length);
        for (FieldDoc field : fields) {
            ApiField apiField = new ApiField();
            apiField.setFieldDoc(field);
            apiField.setTypeName(field.type().typeName().toString());
            apiField.setName(field.name());
            apiField.setApiComments(this.parseApiComments(field.inlineTags()));
            apiFields.add(apiField);
        }
        return apiFields;
    }

//    protected List<ApiMethod> parseApiMethod(ClassDoc classDoc) {
//        MethodDoc[] methodDocs = classDoc.methods();
//        List<ApiMethod> apiMethods = new ArrayList<>(methodDocs.length);
//        for (MethodDoc methodDoc : methodDocs) {
//            ApiMethod apiMethod = new ApiMethod();
//            apiMethod.setMethodDoc(methodDoc);
//            apiMethod.setSimpleName(methodDoc.name());
//            apiMethod.setQualifiedTypeName(methodDoc.qualifiedName());
//
//            //注释下的Tag
//            Tag[] tags = methodDoc.tags();
//            //所有的注释，包括Tag信息
//            Tag[] inlineTags = methodDoc.inlineTags();
//            List<ApiComment> apiComments = parseApiComments(inlineTags);
//            apiMethod.setApiComments(apiComments);
//
//            Parameter[] parameters = methodDoc.parameters();
//
//            Tag returnTag = null;
//            List<ApiSee> apiSees = new ArrayList<>();
//            List<ApiField> apiFields = new ArrayList<>();
//            for (Tag tag : tags) {
//                if (ApiTagUtil.isSee(tag)) {
//                    ApiSee apiSee = ApiSee.create((SeeTag) tag);
//                    apiSees.add(apiSee);
//                    continue;
//                }
//                if (ApiTagUtil.isReturn(tag)) {
//                    returnTag = tag;
//                    continue;
//                }
//                if (ApiTagUtil.isSince(tag)) {
//                    apiMethod.setSince(tag.text());
//                }
//                if (ApiTagUtil.isParam(tag)) {
//                    ParamTag paramTag = (ParamTag) tag;
//                    String parameterName = paramTag.parameterName();
//                    boolean next = false;
//                    for (Parameter parameter : parameters) {
//                        if (parameter.name().equalsIgnoreCase(parameterName)) {
//                            next = true;
//                            break;
//                        }
//                    }
//                    if (!next) {
//                        continue;
//                    }
//                    apiFields.add(parseField(paramTag));
//                    continue;
//                }
//            }
//            apiMethod.setApiSees(apiSees);
//            apiMethod.setApiFields(apiFields);
//
//            ReturnTypeEnum returnType = parseReturnType(methodDoc);
//            apiMethod.setReturnType(returnType);
//            apiMethod.setReturnTag(returnTag);
//            if (returnTag != null) {
//                //解析返回参数
//                Tag[] returnTags = returnTag.inlineTags();
//                List<ApiComment> apiReturnComments = parseApiComments(returnTags);
//                apiMethod.setApiReturnComments(apiReturnComments);
//
//                ClassDoc expendClass = null;
//                for (Tag tag : returnTags) {
//                    if (expendClass != null) {
//                        break;
//                    }
//                    if (tag.text().indexOf("\n") != -1) {
//                        //return的link必须与return保持在同一行才能作用，并且只作用一次
//                        break;
//                    }
//                    if (!ApiTagUtil.isSee(tag)) {
//                        continue;
//                    }
//                    expendClass = ((SeeTag) tag).referencedClass();
//                }
//                if (expendClass != null) {
//                    //解析字段列表
//                    List<ApiLinkIncludeField> linkIncludeFields = new ArrayList<>();
//                    FieldDoc[] expendFields = expendClass.fields();
//                    if (expendFields != null) {
//                        for (FieldDoc expendField : expendFields) {
//                            ApiLinkIncludeField linkIncludeField = new ApiLinkIncludeField();
//                            linkIncludeField.setName(expendField.name());
//                            linkIncludeField.setComment(parseApiComments(expendField.inlineTags()));
//                            linkIncludeField.setType(expendField.type().toString());
//                            linkIncludeFields.add(linkIncludeField);
//                        }
//                    }
//                    apiMethod.setReturnApiLinkIncludeFields(linkIncludeFields);
//                }
//            }
//            apiMethods.add(apiMethod);
//        }
//        return apiMethods;
//    }

//    protected ApiField parseField(ParamTag paramTag) {
//        Tag[] paramInlineTags = paramTag.inlineTags();
//        List<ApiComment> paramApiComment = parseApiComments(paramInlineTags);
//        String type = parseFieldType(paramTag.parameterName(), paramTag.holder());
//        ApiField field = new ApiField();
////        field.setPrimary(type.startsWith("java.lang"));
//
//        ClassDoc expendClass = null;
////        if (!field.isPrimary()) {
////            for (Tag tag : paramInlineTags) {
////                if (!ApiTagUtil.isSee(tag)) {
////                    continue;
////                }
////                if (((SeeTag) tag).referencedClassName().equals(type)) {
////                    expendClass = ((SeeTag) tag).referencedClass();
////                    break;
////                }
////            }
////        }
////        if (expendClass != null) {
////            //解析字段列表
////            List<ApiLinkIncludeField> linkIncludeFields = new ArrayList<>();
////            FieldDoc[] expendFields = expendClass.fields();
////            if (expendFields != null) {
////                for (FieldDoc expendField : expendFields) {
////                    ApiLinkIncludeField linkIncludeField = new ApiLinkIncludeField();
////                    linkIncludeField.setName(expendField.name());
////                    linkIncludeField.setComment(parseApiComments(expendField.inlineTags()));
////                    linkIncludeField.setType(expendField.type().toString());
////                    linkIncludeFields.add(linkIncludeField);
////                }
////            }
////            field.setApiLinkIncludeFields(linkIncludeFields);
////        }
//        field.setTag(paramTag);
//        field.setType(type);
//        field.setName(paramTag.parameterName());
//        field.setComment(paramApiComment);
//        return field;
//    }

    protected String parseFieldType(String fieldName, Doc holder) {
        try {
            Field tree = ProgramElementDocImpl.class.getDeclaredField("tree");
            tree.setAccessible(true);
            JCTree jcTree = (JCTree) tree.get(holder);
            Field params = JCTree.JCMethodDecl.class.getDeclaredField("params");
            List<JCTree.JCVariableDecl> vars = (List<JCTree.JCVariableDecl>) params.get(jcTree);
            if (vars != null) {
                for (JCTree.JCVariableDecl var : vars) {
                    if (fieldName.equals(var.name.toString())) {
                        return var.vartype.type.tsym.toString();
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "";
    }


    protected String parseMethodReturnType(MethodDoc methodDoc) {
        try {
            Field tree = ProgramElementDocImpl.class.getDeclaredField("tree");
            tree.setAccessible(true);
            JCTree jcTree = (JCTree) tree.get(methodDoc);
            JCTree returnType = ((JCTree.JCMethodDecl) jcTree).getReturnType();
            return returnType.toString();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "unknown";
    }

//    protected ReturnTypeEnum getTypeName(String name) {
//        switch (name) {
//            case "void":
//                return ReturnTypeEnum.Void;
//            case "list":
//            case "arraylist":
//            case "linkedList":
//                return ReturnTypeEnum.List;
//            default:
//                return ReturnTypeEnum.Bean;
//        }
//    }

    protected ApiClass parseApiClass(ClassDoc classDoc) {
        ApiClass clazz = new ApiClass();
        clazz.setClassDoc(classDoc);
        clazz.setSimpleName(classDoc.name());
        clazz.setQualifiedTypeName(classDoc.qualifiedTypeName());

        //注释下的Tag
        Tag[] tags = classDoc.tags();
        //所有的注释，包括Tag信息
        Tag[] inlineTags = classDoc.inlineTags();

        List<ApiComment> apiComments = parseApiComments(inlineTags);
        clazz.setApiComments(apiComments);

        List<ApiSee> apiSees = new ArrayList<>();
        for (Tag tag : tags) {
            if (ApiTagUtil.isAuthor(tag)) {
                clazz.setAuthor(tag.text());
                continue;
            }
            if (ApiTagUtil.isSince(tag)) {
                clazz.setSince(tag.text());
                continue;
            }
            if (ApiTagUtil.isSee(tag)) {
                ApiSee apiSee = ApiSee.create((SeeTag) tag);
                apiSees.add(apiSee);
                continue;
            }
        }
        return clazz;
    }

    protected List<ApiComment> parseApiComments(Tag[] inlineTags) {
        List<ApiComment> apiComments = new ArrayList<>();
        for (Tag inlineTag : inlineTags) {
            ApiComment apiComment = new ApiComment();
            apiComment.setLink(ApiTagUtil.isSee(inlineTag));
            apiComment.setText(inlineTag.text());
            apiComment.setTag(inlineTag);
            apiComments.add(apiComment);
        }
        return apiComments;
    }


    protected List<JCTree.JCVariableDecl> getMethodArgs(MethodDoc methodDoc) {
        try {
            Field tree = ProgramElementDocImpl.class.getDeclaredField("tree");
            tree.setAccessible(true);
            JCTree jcTree = (JCTree) tree.get(methodDoc);
            Field params = JCTree.JCMethodDecl.class.getDeclaredField("params");
            return (List<JCTree.JCVariableDecl>) params.get(jcTree);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }
}
