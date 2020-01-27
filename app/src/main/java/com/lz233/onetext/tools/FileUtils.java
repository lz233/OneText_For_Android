package com.lz233.onetext.tools;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

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

    public static File createFile(String FilePath) {
        return new File(FilePath);
    }

    public static boolean isFile(String FilePath){
        boolean isFile = false;
        try {
            File file = new File(FilePath);
            isFile = file.isFile();
        }catch (Exception e) {

        }
        return isFile;
    }

    public static boolean isDirectory(String DirectoryPath){
        File file = new File(DirectoryPath);
        return file.isDirectory();
    }

    public static boolean deleteFile(String FilePath){
        if(isFile(FilePath)){
            File file = new File(FilePath);
            return file.delete();
        }else {
            return false;
        }
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
    public static Uri getUriFromFile(File file, Context context)  {
        Uri imageUri = null;
        if (file != null && file.exists() && file.isFile()) {
            imageUri = FileProvider.getUriForFile(context,"com.lz233.onetext.fileprovider",file);
        }
        return imageUri;
    }
}
