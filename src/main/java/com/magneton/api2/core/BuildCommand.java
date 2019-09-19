package com.magneton.api2.core;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.magneton.api2.worker.ApiDocApiWorker;
import com.magneton.api2.util.ApiLog;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 构建参数命令
 *
 * @author zhangmingshuang
 * @since 2019/8/19
 */
@Setter
@Getter
@ToString
public class BuildCommand {

    @Parameter(names = {"-otype", "-outputType"},
            description = "API输出类型，支持apidoc")
    private String apiType = ApiDocApiWorker.NAME;
    @Parameter(names = {"-f"},
            description = "配置要读取的API文件格式，默认读取所有的*Controller.java文件")
    private String scanFileFilter = ".*Controller\\.java";
    @Parameter(names = {"-s", "-scan"},
            description = "配置要搜索的目录，默认为当前目录")
    private String scanFolder;
    @Parameter(names = {"-se"},
            description = "配置要搜索关联的目录，用来关联类引用时作用")
    private String scanExtFolder;
    @Parameter(names = {"-o", "-output"},
            description = "配置要输出的目录，默认为当前目录目录中")
    private String outputFolder;
    @Parameter(names = {"-oc", "-output-charset"},
            description = "配置输出的文件编码，默认utf-8")
    private String outputCharset = "utf-8";
    @Parameter(names = {"-sc", "-source-charset"},
            description = "配置要解析的文件编码，默认utf-8")
    private String sourceCharset = "utf-8";
    @Parameter(names = {"-help", "-h"}, help = true)
    private boolean help;
    @Parameter(names = {"-pf", "-param-filter"},
            description = "以添加的方式配置解析时参数类型过滤，如：HttpServletRequest。多类型以,分隔" +
                    " 默认过滤HttpServletRequest&HttpServletResponse")
    private String paramFilter;
    @Parameter(names = {"-pfn", "-param-filter-new"},
            description = "强制过滤过滤为参数-pf/-param-filter指定的过滤")
    private boolean paramFilterInNew = false;
    @Parameter(names = {"-reverse"},
            description = "指定一个反向代理服务，将根据服务生成对应配置文件。支持nginx")
    private String reverse;

    public static BuildCommand parse(String[] args) {
        long s = System.currentTimeMillis();
        try {
            BuildCommand command = new BuildCommand();
            if (args == null || args.length < 1) {
                return command;
            }
            JCommander.newBuilder()
                    .addObject(command)
                    .build()
                    .parse(args);
            return command;
        } finally {
            ApiLog.out("parse command in " + (System.currentTimeMillis() - s) + "ms");
        }
    }
}
