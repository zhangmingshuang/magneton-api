package com.magneton.api2.command;

import com.beust.jcommander.Parameter;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 * 通用的API命令
 *
 * @author zhangmingshuang
 * @since 2019/9/28
 */
@Getter
public class CommonApiCommand implements ApiCommand {

    @Parameter(names = {"-h", "-help", "-?"}, description = "帮助文档", help = true)
    private boolean help;

    @Parameter(names = {"-oc", "-output-charset"}, description = "配置输出文件编码")
    private String outputCharset = "utf-8";

    @Parameter(names = {"-sc", "-source-charset"}, description = "配置解析文件编码")
    private String sourceCharset = "utf-8";

    @Parameter(names = {"-s", "-scan"}, description = "配置API文件目录，默认为当前目录")
    private List<String> scanFolders = new ArrayList(1) {
        {
            this.add(System.getProperty("user.dir"));
        }
    };

    @Parameter(names = {"-se", "-scan-extend"}, description = "配置扩展关联目录，可以用来关联类引用")
    private List<String> scanExtFolders = new ArrayList<>(0);

    @Parameter(names = {"-sf", "-scan-file-filter"}, description = "配置要API文件过滤规则")
    private String scanFileFilter = ".*Controller\\.java";

    @Parameter(names = {"-sef", "-scan-extend-file-filter"}, description = "配置扩展关联目录文件过滤规则")
    private String scanExtFileFilter = ".*.";

    @Parameter(names = {"-ptf", "-param-type-filter"},
        description = "指定过滤参数类型", variableArity = true)
    private List<String> paramTypeFilters = new ArrayList(2) {
        {
            this.add("HttpServletRequest");
            this.add("HttpServletResponse");
        }
    };

    @Parameter(names = {"-ignore"},
        description = "注释指定过滤文本，如果注释文本首行或者Param注释中包含文本（忽略大小写），则过滤该方法或参数",
        variableArity = true)
    private List<String> paramIgnore = new ArrayList(1) {
        {
            this.add("[Ignore]");
        }
    };

    @Parameter(names = {"-o", "-output"}, description = "API文档输出目录")
    private String outputFolder = System.getProperty("user.dir");

    @Parameter(names = {"-env"}, description = "强制刷新API生成目录文档")
    private boolean env;
}
