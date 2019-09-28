package com.magneton.api2.builder.doc;

import com.magneton.api2.util.ApiTagUtil;
import com.sun.javadoc.Tag;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 注释文档
 *
 * @author zhangmingshuang
 * @since 2019/9/12
 */
@Setter
@Getter
@ToString
public class ApiComment implements ApiDoc {

    /**
     * 是否一个连接
     */
    private boolean link;
    /**
     * 内容
     */
    private String text;
    /**
     * 对应JAVADOC签标
     * 如果link为true,则表示这是一个SeeTag
     */
    private Tag tag;

    public static List<ApiComment> parseApiComments(Tag[] inlineTags) {
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
}
