package com.changgou.controller;

import com.changgou.file.FastDFSFile;
import com.changgou.util.FastDFSClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Author MartinMYZ
 * @description 文件上传
 * @created at 2019/8/11
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */
@RestController
@CrossOrigin
public class FileController {

    @PostMapping("/upload")
    public String upload(@RequestParam("file")MultipartFile file){
        String path = "";
        try {
            path = saveFile(file);
            System.out.println(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    private String saveFile(MultipartFile multipartFile) throws IOException {
        String[] fileAbsolutePath={};
        String fileName = multipartFile.getOriginalFilename();
        String ext = fileName.substring(fileName.lastIndexOf(".")+1);
        byte[] file_buff = null;
        InputStream inputStream = multipartFile.getInputStream();
        if(inputStream != null){
            int len1 = inputStream.available();
            file_buff = new byte[len1];
            inputStream.read(file_buff);
        }
        inputStream.close();
        FastDFSFile file = new FastDFSFile(fileName, file_buff, ext);
        try {
            fileAbsolutePath = FastDFSClient.upload(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String path = FastDFSClient.getTrackerUrl() + fileAbsolutePath[0] + "/" + fileAbsolutePath[1];
        return path;
    }
}
