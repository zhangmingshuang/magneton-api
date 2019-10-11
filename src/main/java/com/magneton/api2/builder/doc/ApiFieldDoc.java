package com.magneton.api2.builder.doc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;
import java.util.ArrayList;
import java.util.Collections;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author zhangmingshuang
 * @since 2019/9/12
 */
@Setter
@Getter
@ToString
public class ApiFieldDoc extends ApiSeeCollectorDoc {

    private FieldDoc fieldDoc;
    private String typeName;
    private String name;
    private List<ApiComment> apiComments;

    /**
     * 解析API字段列表
     *
     * @param classDoc ClassDoc
     * @return ApiFields
     */
    public static List<ApiFieldDoc> parseApiFields(ClassDoc classDoc) {
        if (classDoc == null) {
            return Collections.EMPTY_LIST;
        }
        FieldDoc[] fields = classDoc.fields();
        if (fields == null || fields.length < 1) {
            return Collections.EMPTY_LIST;
        }
        List<ApiFieldDoc> apiFieldDocs = new ArrayList<>(fields.length);
        for (FieldDoc field : fields) {
            ApiFieldDoc apiFieldDoc = new ApiFieldDoc();
            apiFieldDoc.setFieldDoc(field);
            apiFieldDoc.setTypeName(field.type().typeName().toString());
            apiFieldDoc.setName(field.name());
            apiFieldDoc.setApiComments(ApiComment.parseApiComments(field.inlineTags()));

            apiFieldDoc.seeCollector(field.inlineTags());

            apiFieldDocs.add(apiFieldDoc);
        }
        return apiFieldDocs;
    }

}
