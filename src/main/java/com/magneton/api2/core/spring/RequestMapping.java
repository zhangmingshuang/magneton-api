package com.magneton.api2.core.spring;

import lombok.Getter;
import lombok.Setter;


/**
 * Spring中的RequestMpping数据组装
 *
 * @author zhangmingshuang
 * @since 2019/6/19
 */
@Setter
@Getter
public class RequestMapping {
    /**
     * 请求的实际地址，如：/a/b
     */
    private String[] path = {"/"};
    /**
     * 请求时指定使用的方法类型
     * ALL 表示所有
     * GET 表示Get
     * POST 表示POST
     * 等等
     */
    private String[] method;
    /**
     * 表示请求时必须参数，如果不包括参数则不处理请求
     */
    private String[] params;
    /**
     * 请求头设置
     * 表示请求头必须存在设置的参数，否则不处理
     */
    private String[] headers;
    /**
     * 指定内容类型，Content-Type
     * 如application/json, text/html
     */
    private String[] consumes;
    /**
     * 指定返回内容，权当Request请求头中的Accept包括该指定类型才返回
     */
    private String[] produces;

}
