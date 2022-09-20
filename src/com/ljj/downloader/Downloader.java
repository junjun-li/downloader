package com.ljj.downloader;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Downloader {
    private int threadNum = 10;

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
            System.out.println("[SUCCESS]image download success:" + source + "\n\t ->" + targetFile.getPath() + " (" + Math.floor(targetFile.length() / 1024) + "kb)");
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

    /**
     * 从指定的txt文件中读取下载地址,批量下载网络资源
     * @param targetDir 下载文件的储存目录
     * @param downloadTxt 完整路径
     */
    public void multiDownloadFromFile(String targetDir, String downloadTxt) {
        File dir = new File(targetDir);
        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println("[INFO]发现目录\"" + dir.getPath() + "\"不存在,已自动为您创建");
        }
        ArrayList<String> resources = new ArrayList<>();
        BufferedReader reader = null;
        ExecutorService threadPool = null;
        try {
            reader = new BufferedReader(new FileReader(downloadTxt));
            String line = null;
            while ((line = reader.readLine()) != null) {
                resources.add(line);
            }
            // 开启多线程下载
            threadPool = Executors.newFixedThreadPool(this.threadNum);
            Downloader that = this;
            for (String item : resources) {
                that.download(item, targetDir);
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        that.download(item, targetDir);
                    }
                });
            }
            // System.out.println(resources);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (threadPool != null) {
                threadPool.shutdown();
            }

            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取文件目录,开始多线程下载
     * @param propDir config.properties的目录
     */
    public void start(String propDir) {
        File propFile = new File(propDir + "/config.properties");
        Properties properties = new Properties();
        Reader reader = null;
        try {
            reader = new FileReader(propFile);
            // 读取配置文件
            properties.load(reader);
            // 读取对应的key值
            String threadNum = properties.getProperty("thread-num");
            String targetDir = properties.getProperty("target-dir");
            String downloadFile = properties.getProperty("download-file");
            this.threadNum = Integer.parseInt(threadNum);
            // System.out.println(targetDir);
            // System.out.println(downloadFile);
            this.multiDownloadFromFile(targetDir, downloadFile);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        Downloader downloader = new Downloader();
        // downloader.download("https://manongbiji.oss-cn-beijing.aliyuncs.com/imooc/pexels/pexels-photo-11572548.jpeg", "src/img");
        // downloader.multiDownloadFromFile("src/img", "src/downloadList.txt");
        downloader.start("src");
    }
}
