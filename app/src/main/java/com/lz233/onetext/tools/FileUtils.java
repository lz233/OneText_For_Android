package com.lz233.onetext.tools;

import android.content.Context;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class FileUtils {

    public static File createFile(String FilePath) {
        return new File(FilePath);
    }

    public static boolean isFile(String FilePath) {
        boolean isFile = false;
        try {
            File file = new File(FilePath);
            isFile = file.isFile();
        } catch (Exception e) {

        }
        return isFile;
    }

    public static boolean isDirectory(String DirectoryPath) {
        File file = new File(DirectoryPath);
        return file.isDirectory();
    }

    public static boolean deleteFile(String FilePath) {
        if (isFile(FilePath)) {
            File file = new File(FilePath);
            return file.delete();
        } else {
            return false;
        }
    }

    public static void copyFile(final String From, final String To, final Boolean move, Boolean isBlocking) {
        final Boolean[] isFinish = new Boolean[1];
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String Directory = To.substring(0, To.lastIndexOf("/"));
                    if (!isDirectory(Directory)) {
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
                    if (move) {
                        deleteFile(From);
                    }
                    isFinish[0] = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        if (isBlocking) {
            while ((isFinish[0] == null)) {

            }
        }
    }

    public static void copyAssets(Context context, String assetDir, String dir) {
        String[] files;
        try {
            files = context.getResources().getAssets().list(assetDir);
        } catch (IOException e1) {
            return;
        }
        File mWorkingPath = new File(dir);
        // if this directory does not exists, make one.
        if (!mWorkingPath.exists()) {
            if (!mWorkingPath.mkdirs()) {
            }
        }
        for (int i = 0; i < files.length; i++) {
            try {
                String fileName = files[i];
                // we make sure file name not contains '.' to be a folder.
                if (!fileName.contains(".")) {
                    if (0 == assetDir.length()) {
                        copyAssets(context, fileName, dir + fileName + "/");
                    } else {
                        copyAssets(context, assetDir + "/" + fileName, dir + fileName + "/");
                    }
                    continue;
                }
                File outFile = new File(mWorkingPath, fileName);
                if (outFile.exists())
                    outFile.delete();
                InputStream in = null;
                if (0 != assetDir.length())
                    in = context.getAssets().open(assetDir + "/" + fileName);
                else
                    in = context.getAssets().open(fileName);
                OutputStream out = new FileOutputStream(outFile);
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String readTextFromFile(String FilePath) {
        try {
            if (isFile(FilePath)) {
                FileReader reader = new FileReader(FilePath);
                BufferedReader br = new BufferedReader(reader);
                StringBuffer stringBuffer = new StringBuffer();
                String temp;
                while ((temp = br.readLine()) != null) {
                    stringBuffer.append(temp + "\n");
                }
                return stringBuffer.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeTextToFile(String strcontent, String filePath, String fileName) {
        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                //Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            //Log.e("TestFile", "Error on write File:" + e);
        }
    }

    public static Uri getUriFromFile(File file, Context context) {
        Uri imageUri = null;
        if (file != null && file.exists() && file.isFile()) {
            imageUri = FileProvider.getUriForFile(context, "com.lz233.onetext.fileprovider", file);
        }
        return imageUri;
    }
}
