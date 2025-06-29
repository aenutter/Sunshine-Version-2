package com.example.android.sunshine.app;

/**
 * Created by aenut on 6/29/2025.
 */

public class MyLogger {
    public static void d(String tag, String message) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        // The element at index 3 typically refers to the caller of this logging method
        String className = stackTraceElements[3].getFileName();
        int lineNumber = stackTraceElements[3].getLineNumber();
        android.util.Log.d(tag, "(" + className + ":" + lineNumber + ") " + message);
    }
}
