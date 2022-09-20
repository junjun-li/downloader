package com.ljj.downloader;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class Downloader {
    /**
     * 下载单个文件保存到本地
     * @param source 图片网址
     * @param targetDir 保存目录,需要确保路径有效
     */
    public void download(String source, String targetDir) {
        InputStream is = null;
        OutputStream os = null;
        try {
            // https://www.baidu.com/img.jpg 从"/"开始截取名字
            String fileName = source.substring(source.lastIndexOf("/") + 1);
            File targetFile = new File(targetDir + "/" + fileName);
            // 如果没有这个路径,需要创建这个路径
            if (!targetFile.exists()) {
                targetFile.createNewFile();
            }
            // 创建URL连接
            URL url = new URL(source);
            URLConnection connection = url.openConnection();
            // 输入流赋值
            is = connection.getInputStream();
            // 输出流赋值
            os = new FileOutputStream(targetFile);
            byte[] bs = new byte[1024];
            int len = 0;
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            System.out.println("[SUCCESS]image download success:" + source + "\n\t ->" + targetFile.getPath() + " (" +Math.floor(targetFile.length() / 1024) + "kb)");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Downloader downloader = new Downloader();
        downloader.download("https://manongbiji.oss-cn-beijing.aliyuncs.com/imooc/pexels/pexels-photo-11572548.jpeg", "/Users/lijunjun/MKW/java-2022/io/downloader/src/img");
    }
}
