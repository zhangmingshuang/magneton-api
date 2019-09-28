package com.magneton.api2.builder.doc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 对应解析到的一个方法的@return注解信息
 *
 * @author zhangmingshuang
 * @since 2019/9/18
 */
@Setter
@Getter
@ToString
public class ApiReturnDoc implements ApiDoc {

    private Tag tag;
    /**
     * 返回注释
     */
    private List<ApiComment> apiComments;

    private String typeName;
    /**
     * 一个Return如果注释有@link。 则会解析该link的所有字段。
     * 如果有多个link注释，只解析第一个link对象
     */
    private List<ApiFieldDoc> link;

    public static ApiReturnDoc parseApiReturn(ApiMethodDoc apiMethodDoc, Tag tag) {
        Tag[] returnInlineTags = tag.inlineTags();

        List<ApiFieldDoc> link = null;

        for (Tag returnInlineTag : returnInlineTags) {
            TagType tagType = TagType.getTag(returnInlineTag.kind());
            if (tagType != TagType.See) {
                continue;
            }
            //解析对象
            ClassDoc linkClass = ((SeeTag) returnInlineTag).referencedClass();
            if (linkClass != null) {
                link = ApiFieldDoc.parseApiFields(linkClass);
                break;
            }
        }
        ApiReturnDoc apiReturnDoc = new ApiReturnDoc();
        apiReturnDoc.setTag(tag);
        apiReturnDoc.setTypeName(ApiMethodDoc.parseMethodReturnType(apiMethodDoc.getMethodDoc()));
        apiReturnDoc.setApiComments(ApiComment.parseApiComments(tag.inlineTags()));
        apiReturnDoc.setLink(link);
        return apiReturnDoc;
    }
}
