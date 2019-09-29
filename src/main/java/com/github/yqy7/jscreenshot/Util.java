package com.github.yqy7.jscreenshot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author qiyun.yqy
 * @date 2019/9/25
 */
public class Util {
    private final static Logger logger = LoggerFactory.getLogger(Util.class);

    public final static String OS_NAME = System.getProperty("os.name");

    public static Path TMP_DIRECTORY;

    static {
        try {
            TMP_DIRECTORY = Files.createTempDirectory("jscreenshot");
            logger.info("TMP_DIRECTORY: " + TMP_DIRECTORY);
        } catch (IOException e) {
            logger.error("", e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                delFiles(TMP_DIRECTORY.toFile());
            } catch (Exception e) {
                logger.error("", e);
            }
        }));
    }

    private static void delFiles(File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                delFiles(f);
            }

            file.delete();
        } else {
            file.delete();
        }
    }


    public static boolean isWindows() {
        return OS_NAME.startsWith("Windows");
    }

    public static boolean isMac() {
        return OS_NAME.startsWith("Mac") || OS_NAME.startsWith("Darwin");
    }
}
