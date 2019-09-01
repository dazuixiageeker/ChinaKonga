package com.changgou.test;

import com.changgou.util.FastDFS_util;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @Author MartinMYZ
 * @description
 * @created at 2019/8/12
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FastDFSTest {

    /**
     * 文件下载测试
     */
    @Test
    public void DownloadTest() {
        String groupName = "group1";
        String fileName = "M00/00/00/wKi9g11RKh6AZxXqAAAcm8MHan0017.jpg";
        byte[] bytes = FastDFS_util.download(groupName, fileName);
        try {
            IOUtils.write(bytes, new FileOutputStream("G:/aaa.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**l
     * 文件删除测试
     */
    @Test
    public void DeleteFileTest() {
        String groupName = "group1";
        String fileName = "M00/00/00/wKi9g11RUOyEbJPTAAAAAIR7Pd0204.JPG";
        try {
            FastDFS_util.deleteFile(groupName, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
