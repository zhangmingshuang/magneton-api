package com.magneton.api2.builder.doc;

import com.sun.javadoc.SeeTag;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhangmingshuang
 * @since 2019/9/12
 */
@Setter
@Getter
public class ApiSeeDoc implements ApiDoc {

    private String text;

    public static ApiSeeDoc create(SeeTag tag) {
        ApiSeeDoc apiSeeDoc = new ApiSeeDoc();
        apiSeeDoc.setText(tag.text());
        return apiSeeDoc;
    }
}
