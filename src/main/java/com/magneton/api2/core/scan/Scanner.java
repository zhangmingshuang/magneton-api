package com.magneton.api2.core.scan;

import java.nio.file.Path;

/**
 * 文件扫描， 用来扫描需要解析的文件
 *
 * @author zhangmingshuang
 * @since 2019/8/19
 */
public interface Scanner {

    /**
     * 注册一个文件垃圾收集器，用来收集被扫描器丢弃的文件
     *
     * @param garbageCollector 文件收集器
     */
    void registerFileGarbageCollector(FileCollector garbageCollector);

    /**
     * 扫描文件
     * <p>
     * 会将配置的目录中符合格式的文件设置到文件句柄{@link HFiles}列表中
     * <p>
     * 如果附加文件中包括了主要文件，则会将对应的文件从附加文件列表中移除。
     *
     * @param primaries    要扫描的主要文件目录列表
     * @param subordinates 要扫描的附加文件目录列表
     * @return {@link HFiles}
     */
    HFiles searchFiles(Path[] primaries, Path[] subordinates);
}
