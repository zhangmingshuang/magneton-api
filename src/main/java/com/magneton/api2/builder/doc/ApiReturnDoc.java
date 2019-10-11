package com.magneton.api2.builder.doc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;
import java.util.ArrayList;
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
public class ApiReturnDoc extends ApiSeeCollectorDoc {

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
    private List<ApiFieldDoc> links;

    public static ApiReturnDoc parseApiReturn(ApiMethodDoc apiMethodDoc, Tag tag) {
        Tag[] returnInlineTags = tag.inlineTags();

        List<ApiFieldDoc> links = null;

        for (Tag returnInlineTag : returnInlineTags) {
            TagType tagType = TagType.getTag(returnInlineTag.kind());
            if (tagType != TagType.See) {
                continue;
            }
            //解析对象
            ClassDoc linkClass = ((SeeTag) returnInlineTag).referencedClass();
            if (linkClass != null) {
                links = ApiFieldDoc.parseApiFields(linkClass);
                break;
            }
        }
        ApiReturnDoc apiReturnDoc = new ApiReturnDoc();
        apiReturnDoc.setTag(tag);
        apiReturnDoc.setTypeName(ApiMethodDoc.parseMethodReturnType(apiMethodDoc.getMethodDoc()));

        apiReturnDoc.seeCollector(tag.inlineTags());
        List<ApiComment> apiComments = ApiComment.parseApiComments(tag.inlineTags());
        if (apiComments != null) {
            for (ApiComment apiComment : apiComments) {
                apiReturnDoc.addSees(apiComment.getSees());
            }
        }
        apiReturnDoc.setApiComments(apiComments);
        apiReturnDoc.setLinks(links);

        if (links != null && links.size() > 0) {
            for (ApiFieldDoc link : links) {
                apiReturnDoc.addSees(link.getSees());
            }
        }
        return apiReturnDoc;
    }


}
