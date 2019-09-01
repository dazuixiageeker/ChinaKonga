package com.changgou.file;

/**
 * @Author MartinMYZ
 * @description 封装附件信息
 * @created at 2019/8/12
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */
public class FastDFSFileBean {
    private String name; //文件名
    private String ext;
    private byte[] content;
    private String md5;
    private String author;

    public FastDFSFileBean() {
    }

    public FastDFSFileBean(String name, String ext, byte[] content, String md5, String author) {
        this.name = name;
        this.ext = ext;
        this.content = content;
        this.md5 = md5;
        this.author = author;
    }

    public FastDFSFileBean(String name, String ext, byte[] content) {
        this.name = name;
        this.ext = ext;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
