package com.magneton.api2.core.apiclassdoc;

import com.sun.javadoc.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 注释文档
 *
 * @author zhangmingshuang
 * @since 2019/9/12
 */
@Setter
@Getter
@ToString
public class ApiComment {
    /**
     * 是否一个连接
     */
    private boolean link;
    /**
     * 内容
     */
    private String text;
    /**
     * 对应JAVADOC签标
     * 如果link为true,则表示这是一个SeeTag
     */
    private Tag tag;
}
