package com.green.greengramver.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyFileUtilsTest {
    private final String FILE_DIRECTORY = "D:/SEA/download/greengram_ver3";
    MyFileUtils myFileUtils;

    @BeforeEach
    void setUp() {
        myFileUtils = new MyFileUtils(FILE_DIRECTORY);
    }

    @Test
    void deleteFolder() {
        String middlePath = String.format("%s/user/ddd", myFileUtils.getUploadPath());
        myFileUtils.deleteFolder(middlePath, false);
    }
}