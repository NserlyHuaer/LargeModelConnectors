package com.nserly.Init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class Init<KEY, VALUE> {
    private static final String[] createDirectory = {"data", "data/chat", "cache", "download"};
    private static final String[] createFile = {"data/chat/chatInfo.obj"};

    private static final String[] cleanDirectoryOrFile = {};

    private static final AtomicBoolean isInit = new AtomicBoolean();
    private static final Map DefaultMap = new HashMap<>();
    private final File f;
    private final Properties properties = new Properties();
    private boolean EnableAutoUpdate;
    private static final Logger logger = LoggerFactory.getLogger(Init.class);


    public Init() throws IOException {
        f = new File("data/configuration.ch");
        if ((!f.exists()) || !f.isFile()) {
            f.createNewFile();
        }
    }

    public void InitializeNoneValue(HashMap<KEY, VALUE> DefaultValue) {
        Run();
        DefaultValue.keySet().forEach(e -> {
            if (!containsKey(e)) {
                ChangeValue(e, DefaultValue.get(e));
            }
        });
    }

    public Init(File savePath) throws IOException {
        f = savePath;
        if ((!f.exists()) || !f.isFile()) {
            f.createNewFile();
        }
    }

    public Init(String savePath) throws IOException {
        f = new File(savePath);
        if ((!f.exists()) || !f.isFile()) {
            f.createNewFile();
        }
    }

    public static void init() {
        synchronized (isInit) {
            if (isInit.get()) return;
            File dire;
            File file;
            for (String directory : createDirectory) {
                dire = new File(directory);
                if (!dire.exists()) {
                    dire.mkdir();
                }
            }
            for (String clean : cleanDirectoryOrFile) {
                clearDirectory(new File(clean));
            }
            for (String FilePath : createFile) {
                file = new File(FilePath);
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    logger.error(com.nserly.Logger.getExceptionMessage(e));
                }
            }
            isInit.set(true);
        }
    }

    public static void clearDirectory(File directory) {
        if (!directory.exists()) return;
        if (directory.isFile()) {
            directory.delete();
            return;
        }
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    clearDirectory(file);
                    file.delete();
                    continue;
                }
                file.delete();
            }
        }
    }

    public void Run() {
        if (!isInit.get()) init();
        try {
            if (!f.exists()) {
                Writer(DefaultMap);
            }
            properties.clear();
            properties.load(new BufferedReader(new FileReader(f)));
        } catch (IOException e) {
            logger.error("Failed to read the configuration file");
        }
    }

    public boolean containsKey(KEY key) {
        return properties.containsKey(key);
    }

    public void Loading() {
        if (!isInit.get()) init();
        try {
            properties.load(new BufferedReader(new FileReader(f)));
        } catch (IOException e) {
            logger.error("Failed to read the configuration file");
        }
    }

    public void SetUpdate(boolean EnableAutoUpdate) {
        this.EnableAutoUpdate = EnableAutoUpdate;
    }

    public Properties getProperties() {
        return (Properties) properties.clone();
    }

    public Object getValue(String key) {
        return properties.get(key);
    }

    public void ChangeValue(KEY key, VALUE value) {
        if (key == null || value == null) return;
        properties.remove(key, value);
        properties.put(key, value);
        if (EnableAutoUpdate) Store();
    }

    public void Remove(KEY key, VALUE value) {
        properties.remove(key, value);
        if (EnableAutoUpdate) Store();
    }

    public void Update() {
        Store();
    }

    @SafeVarargs
    public final void Remove(KEY... key) {
        for (KEY i : key) {
            properties.remove(key);
        }
        if (EnableAutoUpdate) Store();
    }

    public void Writer(Map<KEY, VALUE> map) {
        if (!isInit.get()) init();
        properties.putAll(map);
        Store();
    }

    public void Writer(KEY key, VALUE value) {
        if (!isInit.get()) init();
        properties.put(key, value);
        Store();
    }

    private void Store() {
        if (!isInit.get()) init();
        try {
            properties.store(new BufferedWriter(new FileWriter(f)), "");
        } catch (IOException e) {
            logger.error("Failed to save the configuration file");
        }
    }
}
