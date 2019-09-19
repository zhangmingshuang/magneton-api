package com.magneton.api2.core.apiclassdoc;

import com.sun.javadoc.ParamTag;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author zhangmingshuang
 * @since 2019/9/18
 */
@Setter
@Getter
@ToString
public class ApiParam {

    private ParamTag paramTag;

    private String name;
    /**
     * 方法注释
     */
    private List<ApiComment> apiComments;

    private String typeName;
    /**
     * 一个Param参数，如果在参数注释中注解了@link关联对象，并且其关联对象是参数自身
     * 则会解析该对象所有的字段属性
     */
    private List<ApiField> link;
}
