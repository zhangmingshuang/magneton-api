package com.magneton.api2.worker;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.magneton.api2.commander.ApiCommander;
import lombok.Getter;
import lombok.ToString;

/**
 * @author zhangmingshuang
 * @since 2019/9/28
 */
@Getter
@ToString
@Parameters(commandDescription = "Example: java -jar Magneton-api.jar apidoc")
public class ApiDocCommander implements ApiCommander {

    @Parameter(names = {"-h", "-header"}, description = "指定API头，支持MD格式")
    private String header;

    @Parameter(names = {"-e", "-error"}, description = "错定全局错误码类名")
    private String errorClass;
}
