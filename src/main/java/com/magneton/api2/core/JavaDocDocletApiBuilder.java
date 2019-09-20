package com.magneton.api2.core;

import com.magneton.api2.core.apiclassdoc.ApiClassDocParser;
import com.magneton.api2.core.builder.ApiBuilder;
import com.magneton.api2.core.builder.ApiDoclet;
import com.magneton.api2.core.apiclassdoc.Apis;
import com.magneton.api2.core.scan.HFile;
import com.magneton.api2.core.scan.HFiles;
import com.magneton.api2.util.ApiLog;
import com.magneton.api2.util.JavaHomeUtil;
import com.sun.javadoc.ClassDoc;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javadoc.ClassDocImpl;
import com.sun.tools.javadoc.Main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * @author zhangmingshuang
 * @since 2019/9/12
 */
public class JavaDocDocletApiBuilder implements ApiBuilder {
    private static final File RT_JAR;
    private static final Path JAR_DEPENDENCY_PATH;

    static {
        //JDK环境
        RT_JAR = JavaHomeUtil.findRtJar();
        JAR_DEPENDENCY_PATH = JavaDocDocletApiBuilder.doJarDependency();
    }

    @Override
    public Apis build(HFiles hFiles) {
        try {
            List<ClassDoc> classDocs = this.docletBuild(hFiles);
            if (classDocs == null || classDocs.isEmpty()) {
                return null;
            }
            //因为Doclet会将所有的文件解析，所以，这里需要重新过滤文件
            Map<String, HFile> primaries = hFiles.getPrimariesMap();
            List<ClassDoc> availableDocs = new ArrayList<>();
            Field tsym = ClassDocImpl.class.getDeclaredField("tsym");
            tsym.setAccessible(true);
            for (ClassDoc classDoc : classDocs) {
                String fileName = classDoc.name();
                HFile file = primaries.get(fileName);
                if (file == null) {
                    continue;
                }
                Symbol.ClassSymbol classSymbol = (Symbol.ClassSymbol) tsym.get(classDoc);
                if (classSymbol == null
                        || classSymbol.sourcefile == null) {
                    continue;
                }
                Matcher matcher = Api.SCAN_FILE_FILTER.matcher(classSymbol.sourcefile.toString());
                if (!matcher.find()) {
                    continue;
                }
                availableDocs.add(classDoc);
            }
            ApiLog.out("doing builder. api classdoc file number in " + classDocs.size());
            ApiClassDocParser apiClassDocParser = new ApiClassDocParser();
            apiClassDocParser.setParamFilter(Api.PARAM_FILTER);
            return apiClassDocParser.parse(availableDocs);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private List<ClassDoc> docletBuild(HFiles hFiles) {
        String[] configArgs = new String[]{
                "-locale", "zh_CN",
                "-doclet", ApiDoclet.class.getName(),
                "-private",
                "-encoding", Api.INPUT_CHARSET,
                "-quiet",
                //replace ; to , if in linux
                "-extdirs", JAR_DEPENDENCY_PATH.toString() + ";" + RT_JAR.getParent(),
                "-sourcepath", ""
        };
        //系统上扫描的文件可能存在不同目录但是相同命名的情况
        List<HFile> primaries = hFiles.getPrimaries();
        if (primaries.size() < 1) {
            ApiLog.error("not file exists.");
            return null;
        }
        List<String> javaDocArgs = new ArrayList<>();
        for (String configArg : configArgs) {
            javaDocArgs.add(configArg);
        }
        Set<String> primaryFiles = new HashSet<>();
        primaries.forEach(hFile -> {
            String file = hFile.getPath().toAbsolutePath().toString();
            primaryFiles.add(file);
            javaDocArgs.add(file);
        });

        List<HFile> subordinates = hFiles.getSubordinates();
        if (subordinates.size() > 0) {
            javaDocArgs.add("-bootclasspath");
            subordinates.forEach(hFile -> {
                String file = hFile.getPath().toAbsolutePath().toString();
                if (primaryFiles.contains(file)) {
                    return;
                }
                javaDocArgs.add(file);
            });
        }

        String[] args = new String[javaDocArgs.size()];
        javaDocArgs.toArray(args);

        List<ClassDoc> classDocs = new LinkedList<>();
        ApiDoclet.addCollector(classDocs);
        try {
            int error = Main.execute(args);
            if (error > 0) {
                ApiLog.out("api doclet parse error. exit with exception.");
                return null;
            }
            return classDocs;
        } finally {
            ApiDoclet.remove();
        }
    }

    private static Path doJarDependency() {
        String supportJarHome = System.getProperty("user.home");
        Path supportPath = Paths.get(supportJarHome, ".magneton-api");
        if (!Files.exists(supportPath)) {
            try {
                Files.createDirectories(supportPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            ClassLoader classLoader = FileFiltrationScanner.class.getClassLoader();
            URL resource = classLoader.getResource("support");
            if (Api.IS_JAR) {
                //执行支持的Jar包复制
                JarURLConnection jarURLConnection = (JarURLConnection) resource.openConnection();
                Enumeration<JarEntry> entries = jarURLConnection.getJarFile().entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    String name = jarEntry.getName();
                    if (!name.startsWith("support/")
                            || !name.endsWith(".jar")) {
                        continue;
                    }
                    Path file = supportPath.resolve(name.substring(name.indexOf("/") + 1));
                    if (Files.exists(file)) {
                        continue;
                    }
                    try (InputStream inputStream = classLoader.getResourceAsStream(name);
                         FileOutputStream fos = new FileOutputStream(file.toFile())) {
                        byte[] bytes = new byte[1024 << 2];
                        inputStream.read(bytes);
                        int len;
                        while ((len = inputStream.read(bytes)) != -1) {
                            fos.write(bytes, 0, len);
                        }
                    }
                }
            } else {
                //是一个文件，获取目录下文件
                List<Path> collect = Files.list(Paths.get(resource.toURI())).collect(Collectors.toList());
                for (Path path : collect) {
                    Path file = supportPath.resolve(path.getFileName());
                    if (Files.exists(file)) {
                        continue;
                    }
                    Files.copy(path, file);
                }
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return supportPath;
    }
}
