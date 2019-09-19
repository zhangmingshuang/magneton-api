package com.magneton.api2.core.apiclassdoc;

import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.Tag;
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
public class ApiField {
    private FieldDoc fieldDoc;
    private String typeName;
    private String name;
    private List<ApiComment> apiComments;
}
