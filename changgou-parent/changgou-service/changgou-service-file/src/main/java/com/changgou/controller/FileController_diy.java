package com.changgou.controller;

import com.changgou.file.FastDFSFileBean;
import com.changgou.util.FastDFS_util;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author MartinMYZ
 * @description
 * @created at 2019/8/12
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */
@RestController
@CrossOrigin
public class FileController_diy {

    @PostMapping("/upload2")
    public String upload(@RequestParam(value = "file") MultipartFile file) throws Exception {
        //获得文件名
        String fileName = file.getOriginalFilename();
        //获得内容(字节数组)
        byte[] content = file.getBytes();
        //获得文件扩展名
        String ext = FilenameUtils.getExtension(fileName);
        FastDFSFileBean fastDFSFileBean = new FastDFSFileBean(fileName, ext, content);
        //上传文件后返回的路径
        String[] url = FastDFS_util.uploadFile(fastDFSFileBean);
        //拼接路径
        String location = FastDFS_util.getServerUrl(fastDFSFileBean) + url[0] + "/" + url[1];
        return location;
    }


}
