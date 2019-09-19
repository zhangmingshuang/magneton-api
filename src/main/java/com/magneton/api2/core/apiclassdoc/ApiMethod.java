package com.magneton.api2.core.apiclassdoc;

import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * 类下的方法
 *
 * @author zhangmingshuang
 * @since 2019/9/12
 */
@Setter
@Getter
@ToString
public class ApiMethod {
    /**
     * 对应解析JavaDoc的MethodDoc
     */
    private MethodDoc methodDoc;
    /**
     * 方法简名称
     */
    private String simpleName;
    /**
     * 方法全名称，包括类名
     */
    private String qualifiedTypeName;
//    /**
//     * 方法注释中的@see链接
//     */
//    private List<ApiSee> apiSees;
    /**
     * 方法注释中的@since值
     */
    private String since;
    /**
     * 方法注释中的@Deprecated
     */
    private boolean deprecated;
    /**
     * 方法注释
     */
    private List<ApiComment> apiComments;

    private List<ApiParam> apiParams;

    private List<ApiException> apiExceptions;

    private ApiReturn apiReturn;

    public void addApiException(ApiException apiException) {
        if (apiExceptions == null) {
            apiExceptions = new ArrayList<>();
        }
        apiExceptions.add(apiException);
    }

    public void addApiParam(ApiParam apiParam) {
        if (apiParams == null) {
            apiParams = new ArrayList();
        }
        apiParams.add(apiParam);
    }

}
