package com.magneton.api2.builder;

import com.magneton.api2.builder.doc.ApiDocParser;
import com.magneton.api2.scanner.HFiles;
import com.magneton.api2.util.StringUtil;
import java.util.regex.Pattern;
import lombok.Getter;

/**
 * @author zhangmingshuang
 * @since 2019/9/28
 */
@Getter
public class ApiBuilder {

    private String charset = "utf-8";
    private HFiles hFiles;
    private Pattern filter;
    private ApiDocParser apiDocParser;

    public ApiBuilder apiDocParser(ApiDocParser apiDocParser) {
        this.apiDocParser = apiDocParser;
        return this;
    }

    public ApiBuilder hFiles(HFiles hFiles) {
        this.hFiles = hFiles;
        return this;
    }

    public ApiBuilder filter(Pattern filter) {
        this.filter = filter;
        return this;
    }

    public ApiBuilder charset(String charset) {
        if (StringUtil.isEmpty(charset)) {
            return this;
        }
        this.charset = charset;
        return this;
    }

    public ApiImpl build() {
        return new ApiImpl(this);
    }
}
