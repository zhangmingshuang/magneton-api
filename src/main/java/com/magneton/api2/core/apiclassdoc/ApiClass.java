package com.magneton.api2.core.apiclassdoc;

import com.sun.javadoc.ClassDoc;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 对应解析到的一个Class类的信息
 *
 * @author zhangmsh
 * @since 2019-09-18
 */
@Setter
@Getter
@ToString
public class ApiClass {
    /**
     * 对应解析JavaDoc的ClassDoc
     */
    private ClassDoc classDoc;
    /**
     * 类名称
     */
    private String simpleName;
    /**
     * 类完整名称，包括包名
     */
    private String qualifiedTypeName;
    /**
     * 类注释中的@author值
     */
    private String author = "unknown";
    /**
     * 类注释中的@since值
     */
    private String since;
    /**
     * 类注释中的@version值
     */
    private String version;
    /**
     * 抽象的注释信息。以便在具体的API生成中生成对应的链接格式。
     */
    private List<ApiComment> apiComments;
    /**
     * 类中的方法
     */
    private List<ApiMethod> apiMethods;
//    /**
//     * 类注释中的@see链接
//     */
//    private List<ApiSee> apiSees;
    /**
     * 类注释中的@Deprecated
     */
    private boolean deprecated;
}
