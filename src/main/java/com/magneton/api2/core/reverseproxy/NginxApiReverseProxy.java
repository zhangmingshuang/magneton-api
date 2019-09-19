package com.magneton.api2.core.reverseproxy;

import com.magneton.api2.core.ApiFile;

import java.nio.file.Path;

/**
 * @author zhangmingshuang
 * @since 2019/9/19
 */
public class NginxApiReverseProxy implements ApiReverseProxy {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Override
    public String name() {
        return "nginx";
    }

    @Override
    public ApiFile createApiFile(int port, String reverse, Path folder) {
        ApiFile apiFile = new ApiFile();
        apiFile.setFileName("api-nginx.conf");
        String path = folder.toString().replace("\\", "/");
        String content = "server {" + LINE_SEPARATOR
                + "\tlisten " + port + ";" + LINE_SEPARATOR
                + "\tserver_name locatlhost;" + LINE_SEPARATOR
                + "\tlocation /" + reverse + "/ { " + LINE_SEPARATOR
                + "\t\tdefault_type text/html;" + LINE_SEPARATOR
                + "\t\talias " + path + "/;" + LINE_SEPARATOR
                + "\t\tindex index.html;" + LINE_SEPARATOR
                + "\t}" + LINE_SEPARATOR
                + "}";
        apiFile.setContent(content);
        return apiFile;
    }
}
