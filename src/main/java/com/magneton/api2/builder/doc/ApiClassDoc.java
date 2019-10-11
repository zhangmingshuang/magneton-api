package com.magneton.api2.builder.doc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 对应解析到的一个Class类的信息
 *
 * @author zhangmsh
 * @since 2019-09-18
 */
@Setter
@Getter
@ToString
public class ApiClassDoc extends ApiSeeCollectorDoc {

    /**
     * 对应解析JavaDoc的ClassDoc
     */
    private ClassDoc classDoc;
    /**
     * 类名称
     */
    private String simpleName;
    /**
     * 类完整名称，包括包名
     */
    private String qualifiedTypeName;
    /**
     * 类注释中的@author值
     */
    private String author = "unknown";
    /**
     * 类注释中的@since值
     */
    private String since;
    /**
     * 类注释中的@version值
     */
    private String version;
    /**
     * 抽象的注释信息。以便在具体的API生成中生成对应的链接格式。
     */
    private List<ApiComment> apiComments;
    /**
     * 类中的方法
     */
    private List<ApiMethodDoc> apiMethodDocs;
    /**
     * 类注释中的@Deprecated
     */
    private boolean deprecated;

    public static ApiClassDoc parseApiClass(ClassDoc classDoc) {
        ApiClassDoc apiClass = new ApiClassDoc();
        apiClass.setClassDoc(classDoc);
        apiClass.setSimpleName(classDoc.name());
        apiClass.setQualifiedTypeName(classDoc.qualifiedTypeName());
        //注释下的Tag
        Tag[] tags = classDoc.tags();
        apiClass.seeCollector(tags);

        //所有的注释，包括Tag信息
        Tag[] inlineTags = classDoc.inlineTags();
        List<ApiComment> apiComments = ApiComment.parseApiComments(inlineTags);
        apiClass.setApiComments(apiComments);
        for (Tag tag : tags) {
            ApiClassDoc.apiClassStriving(apiClass, tag);
        }

        return apiClass;
    }

    private static void apiClassStriving(ApiClassDoc apiClass, Tag tag) {
        if (tag == null) {
            return;
        }
        TagType tagType = TagType.getTag(tag.kind());
        if (tagType == null) {
            return;
        }
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
            case Since:
                apiClass.setSince(tag.text());
                break;
        }
    }

}
