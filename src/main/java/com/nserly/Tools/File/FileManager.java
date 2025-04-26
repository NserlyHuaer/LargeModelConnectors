package com.nserly.Tools.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

public class FileManager {
    private static final Logger log = LoggerFactory.getLogger(FileManager.class);

    public static String read(String path) {
        String content = null;
        try (FileInputStream fileInputStream = new FileInputStream(path)) {
            content = new String(fileInputStream.readAllBytes());
        } catch (IOException ioException) {
            log.error(com.nserly.Logger.getExceptionMessage(ioException));
        }
        return content;
    }

    public static String read(URL url) {
        return read(url.getPath());
    }
}
