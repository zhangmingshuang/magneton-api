package com.magneton.api2.core.apiclassdoc;

import com.sun.javadoc.SeeTag;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhangmingshuang
 * @since 2019/9/12
 */
@Setter
@Getter
public class ApiSee {
    private String text;

    public static ApiSee create(SeeTag tag) {
        ApiSee apiSee = new ApiSee();
        apiSee.setText(tag.text());
        return apiSee;
    }
}
