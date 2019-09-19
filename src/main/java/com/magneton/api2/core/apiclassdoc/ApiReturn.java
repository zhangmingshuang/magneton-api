package com.magneton.api2.core.apiclassdoc;

import com.sun.javadoc.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 对应解析到的一个方法的@return注解信息
 *
 * @author zhangmingshuang
 * @since 2019/9/18
 */
@Setter
@Getter
@ToString
public class ApiReturn {
    private Tag tag;
    /**
     * 返回注释
     */
    private List<ApiComment> apiComments;

    private String typeName;
    /**
     * 一个Return如果注释有@link。 则会解析该link的所有字段。
     * 如果有多个link注释，只解析第一个link对象
     */
    private List<ApiField> link;
}
