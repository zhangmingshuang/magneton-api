package com.magneton.api2.builder.doc;

import com.sun.javadoc.FieldDoc;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author zhangmingshuang
 * @since 2019/9/25
 */
@Setter
@Getter
@ToString
public class ApiErrorCode {

    private FieldDoc fieldDoc;
    private String typeName;
    private String name;
    private List<ApiComment> apiComments;
}
