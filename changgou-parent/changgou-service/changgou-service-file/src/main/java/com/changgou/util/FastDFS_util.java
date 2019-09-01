package com.changgou.util;

import com.changgou.file.FastDFSFileBean;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

/**
 * @Author MartinMYZ
 * @description 上传文件的工具类
 * @created at 2019/8/12
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */
public class FastDFS_util {

    static {
        //初始化,加载配置文件
        String config_name = new ClassPathResource("fdfs_client.conf").getPath();

        try {
            ClientGlobal.init(config_name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //文件上传的方法
    public static String[] uploadFile(FastDFSFileBean fastDFSFileBean) {
            TrackerClient trackerClient = new TrackerClient();
        System.out.println("util中TrackerClient为"+trackerClient);
        //1. 创建tracker 服务器客户端,
        try {
            byte[] file_buff = fastDFSFileBean.getContent();
            String file_exe_name = fastDFSFileBean.getExt();
            NameValuePair[] meta_list = new NameValuePair[1];
            meta_list[0] = new NameValuePair("author", fastDFSFileBean.getAuthor());
            //获得trackerClient
            //获得trackerServer
            TrackerServer trackerServer = trackerClient.getConnection();
            //获得storageClient
            StorageClient storageClient = new StorageClient(trackerServer, null);

            String[] uploadResult = storageClient.upload_appender_file(file_buff, file_exe_name, meta_list);
            return uploadResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //拼接url
    public static String getServerUrl(FastDFSFileBean fastDFSFileBean) {
        //1. 创建tracker 服务器客户端,
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = null;
        try {
            trackerServer = trackerClient.getConnection();
            //获得hostname,
            String hostName = trackerServer.getInetSocketAddress().getHostName();
            //获得端口
            int port = ClientGlobal.getG_tracker_http_port();
            String url = "http://" + hostName + ":" + port+"/";
            return url;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * FastDFS文件下载
     * @param group_name
     * @param remote_filename
     * @return
     */
    public static byte[] download(String group_name, String remote_filename) {
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = null;
        try {
            trackerServer = trackerClient.getConnection();
            StorageClient storageClient = new StorageClient(trackerServer, null);


            byte[] downloadfile = storageClient.download_file(group_name, remote_filename);
            return downloadfile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除文件
     */
    public static void deleteFile(String group_name, String remote_filename){
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = null;
        try {
            trackerServer = trackerClient.getConnection();
            StorageClient storageClient = new StorageClient(trackerServer, null);
            storageClient.delete_file(group_name, remote_filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
