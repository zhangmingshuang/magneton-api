package com.magneton.api2.builder.doc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;
import com.sun.tools.javac.tree.JCTree;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author zhangmingshuang
 * @since 2019/9/18
 */
@Setter
@Getter
@ToString
public class ApiParamDoc implements ApiDoc {

    private ParamTag paramTag;

    private String name;
    /**
     * 方法注释
     */
    private List<ApiComment> apiComments;

    private String typeName;
    /**
     * 一个Param参数，如果在参数注释中注解了@link关联对象，并且其关联对象是参数自身
     * 则会解析该对象所有的字段属性
     */
    private List<ApiFieldDoc> link;

    /**
     * 解析API方法参数
     *
     * @param apiMethodDoc ApiMethod
     * @param paramTag ParamTag
     * @param paramFilters 参数类型过滤，如HttpSerlvetRequest
     * @return ApiParam
     */
    public static ApiParamDoc parseApiMethodParam(ApiMethodDoc apiMethodDoc, ParamTag paramTag) {
        String paramName = paramTag.parameterName();
        List<JCTree.JCVariableDecl> methodArgs =
            ApiMethodDoc.getMethodArgs((MethodDoc) paramTag.holder());

        //方法参数中对象关联属性
        //作用条件：@param name 注释中，存在注释@link.并且link的对象类型与param中的类型一致
        List<ApiFieldDoc> link = null;
        String typeName = null;

        args:
        for (JCTree.JCVariableDecl methodArg : methodArgs) {
            if (!paramName.equals(methodArg.name.toString())) {
                continue;
            }
            JCTree.JCExpression vartype = (methodArg.vartype);
            typeName = vartype.type.toString();
            if (vartype.type.isPrimitive()) {
                continue;
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
                if (linkClass == null) {
                    continue;
                }
                String type = linkClass.typeName();
                if (!typeName.equals(type)) {
                    continue;
                }
                link = ApiFieldDoc.parseApiFields(linkClass);
                break args;
            }
        }
        ApiParamDoc apiParamDoc = new ApiParamDoc();
        apiParamDoc.setParamTag(paramTag);
        apiParamDoc.setName(paramName);
        apiParamDoc.setTypeName(typeName);
        apiParamDoc.setLink(link);
        apiParamDoc.setApiComments(ApiComment.parseApiComments(paramTag.inlineTags()));
        return apiParamDoc;
    }
}
