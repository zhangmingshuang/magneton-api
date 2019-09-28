package com.magneton.api2.scanner;

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
public class FileScanner implements Scanner {

    private FileScannerBuilder fileScannerBuilder;

    public FileScanner(FileScannerBuilder fileScannerBuilder) {
        this.fileScannerBuilder = fileScannerBuilder;
    }

    @Override
    public HFiles search() {
        HFiles hFiles = new HFiles();

        Path[] subordinatePaths = fileScannerBuilder.getSubordinatePaths();
        hFiles.addSubordinates(this.searchFolder(subordinatePaths, false));

        Path[] primaryPaths = fileScannerBuilder.getPrimaryPaths();
        hFiles.addPrimaries(this.searchFolder(primaryPaths, true));

        return hFiles;
    }

    protected List<HFile> searchFolder(Path[] paths, boolean primary) {
        List<HFile> files = new ArrayList<>();
        if (paths != null && paths.length > 0) {
            for (Path path : paths) {
                if (path == null || !Files.isDirectory(path)) {
                    continue;
                }
                List<HFile> list = this.doSearch(path, primary);
                if (list == null) {
                    continue;
                }
                files.addAll(list);
            }
        }
        return files;
    }

    protected List<HFile> doSearch(Path folderPath, boolean primary) {
        try {
            final List<HFile> files = new ArrayList<>();
            Files.walkFileTree(folderPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs)
                    throws IOException {

                    FileScanner.this.doFileCollector(primary, filePath);

                    if (primary && fileScannerBuilder.getFilter() != null) {
                        Matcher matcher = fileScannerBuilder.getFilter()
                            .matcher(filePath.toString());
                        if (!matcher.find()) {
                            FileScanner.this.doFilterCollector(primary, filePath);
                            return FileVisitResult.CONTINUE;
                        }
                    }

                    String fileName = filePath.getFileName().toString();
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

    protected void doFilterCollector(boolean primary, Path filePath) {
        FilterCollector[] filterCollector = fileScannerBuilder.getFilterCollector();
        if (filterCollector == null) {
            return;
        }

        for (FilterCollector collector : filterCollector) {
            if (collector.primariesTypeOnly() && primary) {
                collector.chuck(filePath);
                return;
            }

            collector.chuck(filePath);
        }

    }

    protected void doFileCollector(boolean primary, Path filePath) {
        FileCollector[] fileCollector = fileScannerBuilder.getFileCollector();
        if (fileCollector == null) {
            return;
        }
        for (FileCollector collector : fileCollector) {
            Pattern[] patterns = collector.patterns();
            if (patterns == null || patterns.length < 1) {
                return;
            }
            if (collector.primariesTypeOnly() && primary) {
                for (Pattern pattern : patterns) {
                    if (pattern.matcher(filePath.toString()).find()) {
                        collector.collect(filePath);
                    }
                }
                return;
            }

            for (Pattern pattern : patterns) {
                if (pattern.matcher(filePath.toString()).find()) {
                    collector.collect(filePath);
                }
            }
        }
    }


}
