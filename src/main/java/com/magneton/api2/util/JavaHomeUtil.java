package com.magneton.api2.util;

import java.io.File;

/**
 * copy from arthas.
 *
 * @author zhangmingshuang
 * @since 2019/8/27
 */
public class JavaHomeUtil {

    private static String FOUND_JAVA_HOME = null;

    /**
     * <pre>
     * 1. Try to find java home from System Property java.home
     * 2. If jdk > 8, FOUND_JAVA_HOME set to java.home
     * 3. If jdk <= 8, try to find tools.jar under java.home
     * 4. If tools.jar do not exists under java.home, try to find System env JAVA_HOME
     * 5. If jdk <= 8 and tools.jar do not exists under JAVA_HOME, throw IllegalArgumentException
     * </pre>
     *
     * @return
     */
    public static String findJavaHome() {
        if (FOUND_JAVA_HOME != null) {
            return FOUND_JAVA_HOME;
        }

        String javaHome = System.getProperty("java.home");

        if (JavaVersionUtils.isLessThanJava9()) {
            File toolsJar = new File(javaHome, "lib/tools.jar");
            if (!toolsJar.exists()) {
                toolsJar = new File(javaHome, "../lib/tools.jar");
            }
            if (!toolsJar.exists()) {
                // maybe jre
                toolsJar = new File(javaHome, "../../lib/tools.jar");
            }

            if (toolsJar.exists()) {
                FOUND_JAVA_HOME = javaHome;
                return FOUND_JAVA_HOME;
            }

            if (!toolsJar.exists()) {
                ApiLog.out("Can not find tools.jar under java.home: " + javaHome);

                String javaHomeEnv = System.getenv("JAVA_HOME");
                if (javaHomeEnv != null && !javaHomeEnv.isEmpty()) {
                    ApiLog.out("Try to find tools.jar in System Env JAVA_HOME: " + javaHomeEnv);
                    // $JAVA_HOME/lib/tools.jar
                    toolsJar = new File(javaHomeEnv, "lib/tools.jar");
                    if (!toolsJar.exists()) {
                        // maybe jre
                        toolsJar = new File(javaHomeEnv, "../lib/tools.jar");
                    }
                }

                if (toolsJar.exists()) {
                    ApiLog.out("Found java home from System Env JAVA_HOME: " + javaHomeEnv);
                    FOUND_JAVA_HOME = javaHomeEnv;
                    return FOUND_JAVA_HOME;
                }

                throw new IllegalArgumentException("Can not find tools.jar under java home: " + javaHome
                        + ", please try to start arthas-boot with full path java. Such as /opt/jdk/bin/java -jar arthas-boot.jar");
            }
        } else {
            FOUND_JAVA_HOME = javaHome;
        }
        return FOUND_JAVA_HOME;
    }

    public static File findToolsJar() {
        findJavaHome();

        if (JavaVersionUtils.isGreaterThanJava8()) {
            return null;
        }

        String javaHome = findJavaHome();
        File toolsJar = new File(javaHome, "lib/tools.jar");
        if (!toolsJar.exists()) {
            toolsJar = new File(javaHome, "../lib/tools.jar");
        }
        if (!toolsJar.exists()) {
            // maybe jre
            toolsJar = new File(javaHome, "../../lib/tools.jar");
        }

        if (!toolsJar.exists()) {
            throw new IllegalArgumentException("Can not find tools.jar under java home: " + javaHome);
        }

        ApiLog.out("Found tools.jar: " + toolsJar.getAbsolutePath());
        return toolsJar;
    }

    public static File findRtJar() {
        findJavaHome();

        if (JavaVersionUtils.isGreaterThanJava8()) {
            return null;
        }

        String javaHome = findJavaHome();
        File toolsJar = new File(javaHome, "lib/rt.jar");
        if (!toolsJar.exists()) {
            toolsJar = new File(javaHome, "../lib/rt.jar");
        }
        if (!toolsJar.exists()) {
            // maybe jre
            toolsJar = new File(javaHome, "../../lib/rt.jar");
        }
        //maybe jdk
        if (!toolsJar.exists()) {
            toolsJar = new File(javaHome, "jre/lib/rt.jar");
        }
        if (!toolsJar.exists()) {
            throw new IllegalArgumentException("Can not find rt.jar under java home: " + javaHome);
        }

        ApiLog.out("Found tools.jar: " + toolsJar.getAbsolutePath());
        return toolsJar;
    }
}
