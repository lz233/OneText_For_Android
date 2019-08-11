package com.lz233.onetext.tools;

import androidx.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileUtils {
    /**
     * 从服务器下载文件
     * @param URLpath 下载文件的地址
     * @param FilePath 文件名字
     */
    public static void downLoadFileFromURL(final String URLpath, final String FilePath,final String FileName, final Boolean isBlocking) {
        final Boolean[] isFinish = new Boolean[1];
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(!isFile(FilePath)){
                        File file = new File(FilePath);
                        file.mkdir();
                    }
                    URL url = new URL(URLpath);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setReadTimeout(5000);
                    con.setConnectTimeout(5000);
                    con.setRequestProperty("Charset", "UTF-8");
                    con.setRequestMethod("GET");
                    if (con.getResponseCode() == 200) {
                        InputStream is = con.getInputStream();//获取输入流
                        FileOutputStream fileOutputStream = null;//文件输出流
                        if (is != null) {
                            FileUtils fileUtils = new FileUtils();
                            fileOutputStream = new FileOutputStream(createFile(FilePath+FileName));//指定文件保存路径，代码看下一步
                            byte[] buf = new byte[1024];
                            int ch;
                            while ((ch = is.read(buf)) != -1) {
                                fileOutputStream.write(buf, 0, ch);//将获取到的流写入文件中
                            }
                        }
                        if (fileOutputStream != null) {
                            fileOutputStream.flush();
                            fileOutputStream.close();
                            isFinish[0] = true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        if (isBlocking) {
            while ((isFinish[0]==null)) {

            }
        }
    }
    public static File createFile(String FilePath) {
        return new File(FilePath);
    }
    public static boolean isFile(String FilePath){
        File file = new File(FilePath);
        return file.isFile();
    }
    public static boolean isDirectory(String DirectoryPath){
        File file = new File(DirectoryPath);
        return file.isDirectory();
    }
    public static boolean deleteFile(String FilePath){
        File file = new File(FilePath);
        return file.delete();
    }
    public static void copyFile (final String From, final String To, final Boolean move, Boolean isBlocking) {
        final Boolean[] isFinish = new Boolean[1];
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String Directory = To.substring(0,To.lastIndexOf("/"));
                    if(!isDirectory(Directory)) {
                        File file = new File(Directory);
                        file.mkdir();
                    }
                    InputStream in = new FileInputStream(From);
                    OutputStream out = new FileOutputStream(To);
                    byte[] buff = new byte[1024];
                    int len;
                    while ((len = in.read(buff)) != -1) {
                        out.write(buff, 0, len);
                    }
                    in.close();
                    out.close();
                    if(move) {
                        deleteFile(From);
                    }
                    isFinish[0] = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        if (isBlocking) {
            while ((isFinish[0]==null)) {

            }
        }
    }
    public static String readTextFromFile (String FilePath) {
        try {
            if (isFile(FilePath)) {
                FileReader reader = new FileReader(FilePath);
                BufferedReader br = new BufferedReader(reader);
                StringBuffer stringBuffer = new StringBuffer();
                String temp;
                while ((temp = br.readLine()) !=  null) {
                    stringBuffer.append(temp+"\n");
                }
                return stringBuffer.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
