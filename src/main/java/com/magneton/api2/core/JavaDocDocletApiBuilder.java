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
import com.sun.tools.javadoc.Main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
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
        List<ClassDoc> classDocs = this.docletBuild(hFiles);
        if (classDocs == null || classDocs.isEmpty()) {
            return null;
        }
        //因为Doclet会将所有的文件解析，所以，这里需要重新过滤文件
        Map<String, HFile> primaries = hFiles.getPrimariesMap();
        List<ClassDoc> availableDocs = new ArrayList<>();
        for (ClassDoc classDoc : classDocs) {
            String fileName = classDoc.name();
            if (!primaries.containsKey(fileName)) {
                continue;
            }
            availableDocs.add(classDoc);
        }
        ApiLog.out("doing builder. api classdoc file number in " + classDocs.size());
        ApiClassDocParser apiClassDocParser = new ApiClassDocParser();
        apiClassDocParser.setParamFilter(Api.PARAM_FILTER);
        return apiClassDocParser.parse(availableDocs);
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
        List<HFile> primaries = hFiles.getPrimaries();
        if (primaries.size() < 1) {
            ApiLog.error("not file exists.");
            return null;
        }
        List<String> javaDocArgs = new ArrayList<>();
        for (String configArg : configArgs) {
            javaDocArgs.add(configArg);
        }
        primaries.forEach(hFile -> javaDocArgs.add(hFile.getPath().toAbsolutePath().toString()));

        List<HFile> subordinates = hFiles.getSubordinates();
        if (subordinates.size() > 0) {
            javaDocArgs.add("-bootclasspath");
            subordinates.forEach(hFile -> javaDocArgs.add(hFile.getPath().toAbsolutePath().toString()));
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
