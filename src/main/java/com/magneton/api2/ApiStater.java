package com.magneton.api2;

import com.magneton.api2.core.BuildCommand;
import com.magneton.api2.core.*;
import com.magneton.api2.core.builder.ApiBuilder;
import com.magneton.api2.core.JavaDocDocletApiBuilder;
import com.magneton.api2.core.apiclassdoc.Apis;
import com.magneton.api2.core.FileFiltrationScanner;
import com.magneton.api2.core.reverseproxy.ApiReverseProxy;
import com.magneton.api2.core.scan.HFiles;
import com.magneton.api2.core.scan.Scanner;
import com.magneton.api2.core.spi.SpiServices;
import com.magneton.api2.core.ApiWorker;
import com.magneton.api2.util.ApiLog;
import org.zeroturnaround.zip.ZipUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

/**
 * API文档生成启动类
 *
 * @author zhangmingshuang
 * @since 2019/8/19
 */
public class ApiStater {

    public static void main(String[] args) {
        ApiStater apiStater = new ApiStater();
        apiStater.doWork(args);
    }

    public void doWork(String[] args) {
        ApiLog.out("=====Magneton ApiClass Builder=======");

        BuildCommand command = BuildCommand.parse(args);
        ApiLog.out(command);

        this.apiEnvinit(command);

        //文件扫描
        Scanner scanner = new FileFiltrationScanner();
        scanner.registerFileGarbageCollector(new DefaultFileGarbageCollector());
        HFiles hFiles = scanner.searchFiles(Api.SCAN_FOLDER, Api.SCAN_EXT_FOLDER);

        //执行解析
        ApiBuilder apiBuilder = new JavaDocDocletApiBuilder();
        Apis apis = apiBuilder.build(hFiles);
        if (apis == null) {
            Api.STOPER.exit();
        }
        ApiWorker apiWorker = SpiServices.getService(ApiWorker.class, command.getApiType());
        ApiFileGenerater apiFileGenerater = apiWorker.createGenerateFiles(apis);
        try {
            this.doFileGenerater(apiFileGenerater);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Api.STOPER.exit();
    }

    private void doFileGenerater(ApiFileGenerater apiFileGenerater) throws IOException {
        //开始生成
        Path folder = Api.OUTPUT_FOLDER.resolve(apiFileGenerater.getFolder());
        this.addFileInclude(folder, apiFileGenerater);
        if (!Files.exists(folder)) {
            try (InputStream inputStream
                         = ApiStater.class.getClassLoader().getResourceAsStream(apiFileGenerater.getEnvLib())) {
                if (inputStream == null) {
                    throw new FileNotFoundException("无法找到" + apiFileGenerater.getEnvLib());
                }
                Files.createDirectories(folder);
                ZipUtil.unpack(inputStream, folder.toFile());
            }
        }
        apiFileGenerater.getApiFiles().forEach(file -> {
            try {
                Files.write(folder.resolve(file.getFileName()), file.getContent().getBytes(Api.OUTPUT_CHARSET));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void addFileInclude(Path folder, ApiFileGenerater apiFileGenerater) {
        String reverse = Api.REVERSE_PROXY;
        if (reverse != null && !reverse.isEmpty()) {
            String[] infos = reverse.split(":");
            String name = infos[0];
            int port = 881;
            if (infos.length > 1) {
                try {
                    port = Integer.parseInt(infos[1]);
                } catch (Throwable e) {
                    //Ignore
                }
            }
            ApiReverseProxy apiReverseProxy
                    = SpiServices.getService(ApiReverseProxy.class, name);
            if (apiReverseProxy != null) {
                ApiFile proxyFile = apiReverseProxy
                        .createApiFile(port, Api.OUTPUT_FOLDER.getFileName().toString(), folder);
                if (proxyFile != null) {
                    apiFileGenerater.addApiFile(proxyFile);
                }
            }
        }
    }

    private void apiEnvinit(BuildCommand command) {

        Api.OUTPUT_CHARSET = command.getOutputCharset();
        Api.INPUT_CHARSET = command.getSourceCharset();
        Api.SCAN_FILE_FILTER = Pattern.compile(command.getScanFileFilter());

        Api.SCAN_FOLDER = this.parsePaths(command.getScanFolder());
        Api.SCAN_EXT_FOLDER = this.parsePaths(command.getScanExtFolder());

        Api.OUTPUT_FOLDER = this.parsePath(command.getOutputFolder());

        if (command.isParamFilterInNew()) {
            Api.PARAM_FILTER.clear();
        }
        String paramFilter = command.getParamFilter();
        if (paramFilter != null && !paramFilter.isEmpty()) {
            String[] paramFilters = paramFilter.split(",");
            for (String filter : paramFilters) {
                if (filter == null || filter.isEmpty()) {
                    continue;
                }
                Api.PARAM_FILTER.add(filter.toLowerCase());
            }
        }

        Api.REVERSE_PROXY = command.getReverse();

        Api.STOPER = () -> {
            ApiLog.out("exit 0");
            System.exit(0);
        };

    }

    private Path parsePath(String folder) {
        if (folder == null || folder.isEmpty()) {
            return Paths.get(Api.RUNTIME_DIR);
        }
        folder = folder.replace("${project}", Api.RUNTIME_DIR);
        Path path = Paths.get(folder);
        if (!Files.isDirectory(path)) {
            throw new RuntimeException(folder + "is not a dir");
        }
        return Paths.get(folder);
    }

    private Path[] parsePaths(String folder) {
        if (folder == null || folder.isEmpty()) {
            return new Path[]{Paths.get(Api.RUNTIME_DIR)};
        }
        String[] folders = folder.split(",");
        Path[] paths = new Path[folders.length];
        int index = 0;
        for (String f : folders) {
            paths[index++] = this.parsePath(f);
        }
        return paths;
    }
}
