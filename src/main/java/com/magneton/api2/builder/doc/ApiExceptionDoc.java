package com.magneton.api2.builder.doc;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author zhangmingshuang
 * @since 2019/9/19
 */
@Setter
@Getter
public class ApiExceptionDoc implements ApiDoc{
    private String name;
    private List<ApiComment> apiComments;
}
