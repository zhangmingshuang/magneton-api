package com.magneton.api2.builder.doc;

import com.magneton.api2.util.ApiLog;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Tag;
import com.sun.javadoc.ThrowsTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javadoc.ProgramElementDocImpl;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * 类下的方法
 *
 * @author zhangmingshuang
 * @since 2019/9/12
 */
@Setter
@Getter
@ToString
public class ApiMethodDoc implements ApiDoc {

    /**
     * 对应解析JavaDoc的MethodDoc
     */
    private MethodDoc methodDoc;
    /**
     * 方法简名称
     */
    private String simpleName;
    /**
     * 方法全名称，包括类名
     */
    private String qualifiedTypeName;
//    /**
//     * 方法注释中的@see链接
//     */
//    private List<ApiSee> apiSees;
    /**
     * 方法注释中的@since值
     */
    private String since;
    /**
     * 方法注释中的@Deprecated
     */
    private boolean deprecated;
    /**
     * 方法注释
     */
    private List<ApiComment> apiComments;

    private List<ApiParamDoc> apiParamDocs;

    private List<ApiExceptionDoc> apiExceptionDocs;

    private ApiReturnDoc apiReturnDoc;

    public static List<JCVariableDecl> getMethodArgs(MethodDoc methodDoc) {
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

    public void addApiException(ApiExceptionDoc apiExceptionDoc) {
        if (apiExceptionDocs == null) {
            apiExceptionDocs = new ArrayList<>();
        }
        apiExceptionDocs.add(apiExceptionDoc);
    }

    public void addApiParam(ApiParamDoc apiParamDoc) {
        if (apiParamDocs == null) {
            apiParamDocs = new ArrayList();
        }
        apiParamDocs.add(apiParamDoc);
    }


    public static List<ApiMethodDoc> parseApiMethods(ClassDoc classDoc) {
        MethodDoc[] methodDocs = classDoc.methods();
        List<ApiMethodDoc> apiMethodDocs = new ArrayList<>(methodDocs.length);
        for (MethodDoc methodDoc : methodDocs) {
            try {
                ApiMethodDoc apiMethodDoc = new ApiMethodDoc();
                apiMethodDoc.setMethodDoc(methodDoc);
                apiMethodDoc.setSimpleName(methodDoc.name());
                apiMethodDoc.setQualifiedTypeName(methodDoc.qualifiedName());
                //注释下的Tag
                Tag[] tags = methodDoc.tags();
                //所有的注释，包括Tag信息
                Tag[] inlineTags = methodDoc.inlineTags();
                List<ApiComment> apiComments = ApiComment.parseApiComments(inlineTags);
                apiMethodDoc.setApiComments(apiComments);
                for (Tag tag : tags) {
                    TagType tagType = TagType.getTag(tag.kind());
                    ApiMethodDoc.apiMethodStriving(apiMethodDoc, tagType, tag);
                }
                apiMethodDocs.add(apiMethodDoc);
            } catch (Throwable e) {
                ApiLog.error(
                    "error parse class " + classDoc.qualifiedTypeName() + ", method " + methodDoc
                        .qualifiedName());
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return apiMethodDocs;
    }

    private static void apiMethodStriving(
        ApiMethodDoc apiMethodDoc, TagType tagType, Tag tag) {
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
                ApiParamDoc apiParamDoc = ApiParamDoc.parseApiMethodParam(apiMethodDoc,
                    (ParamTag) tag);
                if (apiParamDoc != null) {
                    apiMethodDoc.addApiParam(apiParamDoc);
                }
                break;
            case Return:
                ApiReturnDoc apiReturnDoc = ApiReturnDoc.parseApiReturn(apiMethodDoc, tag);
                if (apiReturnDoc != null) {
                    apiMethodDoc.setApiReturnDoc(apiReturnDoc);
                }
                break;
            case Throws:
                ThrowsTag throwsTag = (ThrowsTag) tag;
                ApiExceptionDoc apiExceptionDoc = new ApiExceptionDoc();
                apiExceptionDoc.setName(throwsTag.exceptionName());
                apiExceptionDoc.setApiComments(ApiComment.parseApiComments(throwsTag.inlineTags()));
                apiMethodDoc.addApiException(apiExceptionDoc);
                break;
            case Deprecated:
                apiMethodDoc.setDeprecated(true);
                break;
            case See:
                break;
            case Since:
                apiMethodDoc.setSince(tag.text());
                break;
        }
    }

    public static String parseMethodReturnType(MethodDoc methodDoc) {
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

}
