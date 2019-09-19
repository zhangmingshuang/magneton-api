package com.magneton.api2.core.scan;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhangmingshuang
 * @since 2019/9/18
 */
public abstract class AbstractFileScanner implements Scanner {

    /**
     * 标识支持的扫描文件类型
     */
    public abstract Pattern primariesType();

    public abstract Pattern subordinatesType();

    /**
     * 垃圾文件收集器
     * 所有配置的{@link #hfileType()}不支持的文件，都会放置到该垃圾收集器中
     */
    private FileCollector garbageCollector;

    @Override
    public void registerFileGarbageCollector(FileCollector garbageCollector) {
        this.garbageCollector = garbageCollector;
    }

    @Override
    public HFiles searchFiles(Path[] primaries, Path[] subordinates) {
        //优先对附属文件进行扫描，如果主要文件中包括附属文件，则删除对应的附属文件，避免重复影响
        HFiles hFiles = new HFiles();
        if (subordinates != null) {
            for (Path subordinate : subordinates) {
                List<HFile> subordinateFiles = this.doSearch(subordinate, this.subordinatesType());
                hFiles.addSubordinates(subordinateFiles);
            }
        }
        for (Path primary : primaries) {
            List<HFile> primaryFiles = this.doSearch(primary, this.primariesType());
            hFiles.addPrimaries(primaryFiles);
        }
        return hFiles;
    }

    protected List<HFile> doSearch(Path folderPath, Pattern pattern) {
        try {
            final List<HFile> files = new ArrayList<>();
            Files.walkFileTree(folderPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
                    String fileName = filePath.getFileName().toString();
                    Matcher matcher = pattern.matcher(fileName);
                    if (!matcher.find()) {
                        AbstractFileScanner.this.doGarbageCollector(filePath);
                        return FileVisitResult.CONTINUE;
                    }
                    HFile file = new HFile();
                    file.setFileName(fileName);
                    int pointIndex = fileName.lastIndexOf(".");
                    file.setName(pointIndex == -1 ? fileName : fileName.substring(0, pointIndex));
                    file.setPath(filePath);
                    files.add(file);
                    return FileVisitResult.CONTINUE;
                }
            });
            return files;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void doGarbageCollector(Path file) {
        if (this.garbageCollector == null) {
            return;
        }
        this.garbageCollector.chuck(file);
    }

}
