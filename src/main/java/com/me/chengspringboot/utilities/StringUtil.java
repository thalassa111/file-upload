package com.me.chengspringboot.utilities;

public class StringUtil {
    //used to create new unique names for files, takes in a file and adds a counter to it, example(1).txt
    public static String getNewFileName(String fileName, int counter) {
        int dotIndex = fileName.lastIndexOf('.');
        String baseName = fileName.substring(0, dotIndex);
        String extension = fileName.substring(dotIndex);
        return baseName + "(" + counter + ")" + extension;
    }
}
