package com.magneton.api2;

import com.magneton.api2.builder.doc.ApiDocParser;
import com.magneton.api2.builder.doc.ApiDocParserBuilder;
import com.magneton.api2.command.CommonApiCommand;
import com.magneton.api2.core.*;
import com.magneton.api2.builder.Api;
import com.magneton.api2.builder.doc.Apis;
import com.magneton.api2.core.ApiForemaner;
import com.magneton.api2.builder.ApiBuilder;
import com.magneton.api2.generater.ApiFileGenerater;
import com.magneton.api2.scanner.FileScannerBuilder;
import com.magneton.api2.scanner.HFiles;
import com.magneton.api2.scanner.Scanner;
import com.magneton.api2.util.ApiLog;
import com.magneton.api2.util.StringUtil;
import java.io.File;
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

        ApiForemaner apiForemaner = ApiForemaner.contactWorker(args);

        Scanner scanner = this.getFileScanner(apiForemaner);
        HFiles hFiles = scanner.search();
        if (hFiles.isEmpty()) {
            ApiLog.out("no file found in " + apiForemaner);
            System.exit(0);
            return;
        }

        CommonApiCommand commonApiCommand = apiForemaner.getCommonApiCommand();

        ApiWorker apiWorker = apiForemaner.getApiWorker();
        apiWorker.afterApiCommanderSet(commonApiCommand);

        List<String> paramTypeFilters = commonApiCommand.getParamTypeFilters();
        ApiDocParser apiDocParser = new ApiDocParserBuilder()
            .ignore(commonApiCommand.getParamIgnore())
            .paramDocFilter(new ParamTypeFilter(paramTypeFilters))
            .classDocCollector(apiWorker.classDocCollector())
            .build();

        String scanFilter = commonApiCommand.getScanFileFilter();

        Api api = new ApiBuilder()
            .hFiles(hFiles)
            .charset(commonApiCommand.getSourceCharset())
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
            this.doFileGenerater(commonApiCommand.getOutputFolder(), apiFileGenerater,
                                 commonApiCommand.getOutputCharset(), commonApiCommand.isEnv());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    private Scanner getFileScanner(ApiForemaner apiForemaner) {
        CommonApiCommand commonApiCommand = apiForemaner.getCommonApiCommand();
        String filter = commonApiCommand.getScanFileFilter();
        Pattern filterPattern = null;
        if (!StringUtil.isEmpty(filter)) {
            filterPattern = Pattern.compile(commonApiCommand.getScanExtFileFilter());
        }

        List<String> scanFolders = commonApiCommand.getScanFolders();
        List<String> scanExtFolders = commonApiCommand.getScanExtFolders();

        ApiWorker apiWorker = apiForemaner.getApiWorker();

        return new FileScannerBuilder()
            .filter(filterPattern)
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
                                 String outputCharset, boolean forceEnv) throws IOException {
        //开始生成
        Path folder = Paths.get(outputFolder, apiFileGenerater.getFolder());
        if (!Files.exists(folder) || forceEnv) {
            try (InputStream inputStream
                = ApiBootstrap.class.getClassLoader().getResourceAsStream(apiFileGenerater.getEnvLib())) {
                if (inputStream == null) {
                    throw new FileNotFoundException("无法找到" + apiFileGenerater.getEnvLib());
                }
                if (!Files.exists(folder)) {
                    Files.createDirectories(folder);
                }
                if (!Files.isWritable(folder)) {
                    ApiLog.error("folder：" + folder + " not writable.");
                    return;
                }
                File file = folder.toFile();
                if (!file.canWrite()) {
                    ApiLog.error("folder：" + folder + " not writable.");
                    return;
                }
                try {
                    ZipUtil.unpack(inputStream, file);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
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
}
