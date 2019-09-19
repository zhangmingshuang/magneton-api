package com.magneton.api2.core;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 整个API运行时环境
 *
 * @author zhangmingshuang
 * @since 2019/9/12
 */
public class Api {

    private Api() {
        throw new RuntimeException("deny");
    }

    /**
     * 运行时目录
     */
    public static final String RUNTIME_DIR;
    /**
     * 运行时是否是Jar包运行
     */
    public static final boolean IS_JAR;

    public static final String VERSION;

    /**
     * 字段过滤
     */
    public static final Set<String> PARAM_FILTER = new HashSet<>();

    static {
        RUNTIME_DIR = System.getProperty("user.dir");

        URL resource = Api.class.getResource("");
        IS_JAR = resource.getProtocol().equals("jar");

        Properties properties = new Properties();
        try {
            properties.load(Api.class.getClassLoader().getResourceAsStream("magneton.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        VERSION = (String) properties.get("magneton.version");

        PARAM_FILTER.add("HttpServletRequest".toLowerCase());
        PARAM_FILTER.add("HttpServletResponse".toLowerCase());
    }

    /**
     * 文件输出时编码
     */
    public static String OUTPUT_CHARSET = "utf-8";
    /**
     * 文件输入时编码
     */
    public static String INPUT_CHARSET = "utf-8";
    /**
     * 扫描时文件过滤正则
     */
    public static Pattern SCAN_FILE_FILTER;
    /**
     * 扫描目录
     */
    public static Path[] SCAN_FOLDER;
    /**
     * 扫描扩展目录
     */
    public static Path[] SCAN_EXT_FOLDER;
    /**
     * 输出目录
     */
    public static Path OUTPUT_FOLDER;

    public static Stoper STOPER;

    public static String REVERSE_PROXY;
}
