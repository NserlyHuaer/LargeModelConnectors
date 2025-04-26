package com.nserly;

public class Logger {
    public static void setUncaughtExceptionHandler(org.slf4j.Logger logger) {
        Thread.setDefaultUncaughtExceptionHandler((e1, e2) -> {
            logger.error(getExceptionMessage(e2));
        });
    }

    public static String getExceptionMessage(Throwable e) {
        if (e == null) return null;
        StringBuilder stringBuilder = new StringBuilder(e.getClass().getName() + ":" + e.getMessage() + "\n");
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        if (stackTraceElements != null) {
            for (StackTraceElement stackTraceElement : stackTraceElements) {
                stringBuilder.append("at ").append(stackTraceElement.getClassName()).append("(line:").append(stackTraceElement.getLineNumber()).append(")\n");
            }
        }
        Throwable throwable = e.getCause();
        if (throwable == null) return stringBuilder.toString();
        stringBuilder.append("Caused by:").append(getExceptionMessage(throwable));
        return stringBuilder.toString();
    }
}
