package com.magneton.api2.core.apiclassdoc;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author zhangmingshuang
 * @since 2019/9/19
 */
@Setter
@Getter
public class ApiException {
    private String name;
    private List<ApiComment> apiComments;
}
