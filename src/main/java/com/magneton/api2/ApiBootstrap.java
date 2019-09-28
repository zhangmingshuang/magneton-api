package com.magneton.api2;

import com.magneton.api2.builder.doc.ApiDocParser;
import com.magneton.api2.builder.doc.ApiDocParserBuilder;
import com.magneton.api2.builder.doc.ParamDocFilter;
import com.magneton.api2.commander.CommonApiCommander;
import com.magneton.api2.core.*;
import com.magneton.api2.builder.Api;
import com.magneton.api2.builder.doc.Apis;
import com.magneton.api2.core.ApiForeman;
import com.magneton.api2.builder.ApiBuilder;
import com.magneton.api2.generater.ApiFileGenerater;
import com.magneton.api2.scanner.FileScannerBuilder;
import com.magneton.api2.scanner.HFiles;
import com.magneton.api2.scanner.Scanner;
import com.magneton.api2.util.ApiLog;
import com.magneton.service.core.util.StringUtil;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import org.zeroturnaround.zip.ZipUtil;

/**
 * API文档生成启动类
 *
 * @author zhangmingshuang
 * @since 2019/8/19
 */
public class ApiBootstrap {

    public static void main(String[] args) {
        ApiBootstrap apiBootstrap = new ApiBootstrap();
        apiBootstrap.doWork(args);
    }

    public void doWork(String[] args) {
        ApiLog.out("=====Magneton ApiClass Builder=======");

        ApiForeman apiForeman = ApiForeman.contactWorker(args);
        ApiWorker apiWorker = apiForeman.getApiWorker();
        apiWorker.afterApiCommanderSet(apiForeman.getCommonApiCommander());

        //文件扫描
        Scanner scanner = this.getFileScanner(apiForeman);
        HFiles hFiles = scanner.search();
        if (hFiles.isEmpty()) {
            ApiLog.out("no file found.");
            System.exit(0);
        }

        CommonApiCommander commonApiCommander = apiForeman.getCommonApiCommander();

        List<String> paramTypeFilters = commonApiCommander.getParamTypeFilters();
        ApiDocParser apiDocParser = new ApiDocParserBuilder()
            .paramDocFilter(new ParamFilter(paramTypeFilters))
            .classDocCollector(apiWorker.classDocCollector())
            .build();

        String scanFilter = commonApiCommander.getScanFileFilter();
        Api api = new ApiBuilder()
            .hFiles(hFiles)
            .charset(commonApiCommander.getSourceCharset())
            .filter(StringUtil.isEmpty(scanFilter) ? null : Pattern.compile(scanFilter))
            .apiDocParser(apiDocParser)
            .build();

        Apis apis = api.create();

        if (apis.isEmpty()) {
            ApiLog.out("not api created.");
            System.exit(0);
        }
        ApiLog.out(apis);

        ApiFileGenerater apiFileGenerater = apiWorker.createGenerateFiles(apis);
        try {
            this.doFileGenerater(commonApiCommander.getOutputFolder(), apiFileGenerater,
                commonApiCommander.getOutputCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    private Scanner getFileScanner(ApiForeman apiForeman) {
        CommonApiCommander commonApiCommander = apiForeman.getCommonApiCommander();
        String filter = commonApiCommander.getScanFileFilter();
        Pattern pattern = null;
        if (!StringUtil.isEmpty(filter)) {
            pattern = Pattern.compile(commonApiCommander.getScanExtFileFilter());
        }

        List<String> scanFolders = commonApiCommander.getScanFolders();
        List<String> scanExtFolders = commonApiCommander.getScanExtFolders();

        ApiWorker apiWorker = apiForeman.getApiWorker();

        return new FileScannerBuilder()
            .filter(pattern)
            .fileCollector(apiWorker == null ? null : apiWorker.fileCollector())
            .filterCollector(apiWorker == null ? null : apiWorker.filterCollector())
            .primaryPaths(this.getPaths(scanFolders))
            .subordinatePaths(this.getPaths(scanExtFolders))
            .build();
    }

    private Path[] getPaths(List<String> scanFolders) {
        Path[] scanPaths = null;
        if (scanFolders != null && scanFolders.size() > 0) {
            List<Path> paths = new ArrayList<>(scanFolders.size());
            for (String scanFolder : scanFolders) {
                if (StringUtil.isEmpty(scanFolder)) {
                    continue;
                }
                paths.add(Paths.get(scanFolder));
            }
            scanPaths = paths.toArray(new Path[0]);
        }
        return scanPaths;
    }

    private void doFileGenerater(String outputFolder, ApiFileGenerater apiFileGenerater,
        String outputCharset) throws IOException {
        //开始生成
        Path folder = Paths.get(outputFolder, apiFileGenerater.getFolder());
        if (!Files.exists(folder)) {
            try (InputStream inputStream = ApiBootstrap.class.getClassLoader()
                .getResourceAsStream(apiFileGenerater.getEnvLib())) {
                if (inputStream == null) {
                    throw new FileNotFoundException("无法找到" + apiFileGenerater.getEnvLib());
                }
                Files.createDirectories(folder);
                ZipUtil.unpack(inputStream, folder.toFile());
            }
        }
        apiFileGenerater.getApiFiles().forEach(file -> {
            try {
                Files.write(folder.resolve(file.getFileName()),
                    file.getContent().getBytes(outputCharset));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

//    private void addFileInclude(Path folder, ApiFileGenerater apiFileGenerater) {
//        String reverse = ApiConstant.REVERSE_PROXY;
//        if (reverse != null && !reverse.isEmpty()) {
//            String[] infos = reverse.split(":");
//            String name = infos[0];
//            int port = 881;
//            if (infos.length > 1) {
//                try {
//                    port = Integer.parseInt(infos[1]);
//                } catch (Throwable e) {
//                    //Ignore
//                }
//            }
//            ApiReverseProxy apiReverseProxy
//                = SpiServices.getService(ApiReverseProxy.class, name);
//            if (apiReverseProxy != null) {
//                ApiFile proxyFile = apiReverseProxy
//                    .createApiFile(port, ApiConstant.OUTPUT_FOLDER.getFileName().toString(),
//                        folder);
//                if (proxyFile != null) {
//                    apiFileGenerater.addApiFile(proxyFile);
//                }
//            }
//        }
//    }
}
