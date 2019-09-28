package com.magneton.api2.scanner;

import java.nio.file.Path;
import java.util.regex.Pattern;
import lombok.Getter;

/**
 * 文件扫描器
 *
 * 文件扫描器会为二阶段扫描
 *
 * 首先扫描{@link #subordinatePaths}文件
 *
 * 然后再扫描{@link #primaryPaths}文件
 *
 * @author zhangmingshuang
 * @since 2019/9/28
 */
@Getter
public class FileScannerBuilder {

    /**
     * 文件规则过滤器
     * 所以被过滤的文件都会垃圾文件回收器采集
     */
    private Pattern filter;
    /**
     * 过滤文件采集器
     */
    private FilterCollector[] filterCollector;
    /**
     * 所有文件采集器
     */
    private FileCollector[] fileCollector;
    /**
     * 主要扫描目录
     */
    private Path[] primaryPaths;
    /**
     * 次要扫描目录
     */
    private Path[] subordinatePaths;

    public FileScannerBuilder subordinatePaths(Path... subordinatePaths) {
        this.subordinatePaths = subordinatePaths;
        return this;
    }

    public FileScannerBuilder primaryPaths(Path... primaryPaths) {
        this.primaryPaths = primaryPaths;
        return this;
    }

    public FileScannerBuilder filterCollector(FilterCollector[] filterCollector) {
        this.filterCollector = filterCollector;
        return this;
    }

    public FileScannerBuilder fileCollector(FileCollector[] fileCollector) {
        this.fileCollector = fileCollector;
        return this;
    }

    public FileScannerBuilder filter(Pattern filter) {
        this.filter = filter;
        return this;
    }

    public Scanner build() {
        return new FileScanner(this);
    }
}
