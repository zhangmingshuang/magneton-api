package com.magneton.api2.core;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * 整个API运行时环境
 *
 * @author zhangmingshuang
 * @since 2019/9/12
 */
public class ApiConstant {

    private ApiConstant() {
        throw new RuntimeException("deny");
    }

    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    /**
     * 运行时目录
     */
    public static final String RUNTIME_DIR;
    /**
     * 运行时是否是Jar包运行
     */
    public static final boolean IS_JAR;

    public static final String VERSION;

    static {
        RUNTIME_DIR = System.getProperty("user.dir");

        URL resource = ApiConstant.class.getResource("");
        IS_JAR = resource.getProtocol().equals("jar");

        Properties properties = new Properties();
        try {
            properties.load(ApiConstant.class.getClassLoader().getResourceAsStream("magneton.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        VERSION = (String) properties.get("magneton.version");
    }
}
