package com.magneton.api2.builder.doc;

import lombok.Getter;

/**
 * API DOC 解析器构造器
 * 用来构建整个解析环境基础
 *
 * @author zhangmingshuang
 * @since 2019/9/28
 */
@Getter
public class ApiDocParserBuilder {

    private ClassDocCollector classDocCollector;
    private ClassDocFilter classDocFilter;
    private MethodDocFilter methodDocFilter;
    private ParamDocFilter paramDocFilter;

    public ApiDocParserBuilder paramDocFilter(ParamDocFilter paramDocFilter) {
        this.paramDocFilter = paramDocFilter;
        return this;
    }

    public ApiDocParserBuilder methodDocFilter(MethodDocFilter methodDocFilter) {
        this.methodDocFilter = methodDocFilter;
        return this;
    }

    public ApiDocParserBuilder classDocFilter(ClassDocFilter classDocFilter) {
        this.classDocCollector = classDocCollector;
        return this;
    }

    public ApiDocParserBuilder classDocCollector(ClassDocCollector classDocCollector) {
        this.classDocCollector = classDocCollector;
        return this;
    }

    public ApiDocParser build() {
        return new ApiDocParser(this);
    }
}
