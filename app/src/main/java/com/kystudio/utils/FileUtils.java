package com.kystudio.utils;

import android.os.Environment;
import com.kystudio.model.Mp3Info;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by 20236320 on 2016/8/14.
 */
public class FileUtils {
    private String SDCardRoot;

    public FileUtils() {
        SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }

    /**
     * 判断SD卡上的文件是否存在
     *
     * @param fileName
     * @return
     */
    public boolean isFileExist(String fileName, String path) {
        File file = new File(SDCardRoot + path + File.separator + fileName);
        return file.exists();
    }

    /**
     * 在SD卡上创建目录
     *
     * @param dir
     * @return
     */
    public File createSDDir(String dir) {
        File dirFile = new File(SDCardRoot + dir + File.separator);
        dirFile.mkdir();

        return dirFile;
    }

    /**
     * 在SD卡上创建文件
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public File createFileInSDCard(String fileName, String dir) throws IOException {
        File file = new File(SDCardRoot + dir + File.separator + fileName);
        file.createNewFile();

        return file;
    }

    /**
     * 将一个InputStream里面的数据写入到SD卡中
     *
     * @param path
     * @param fileName
     * @param input
     * @return
     */
    public File write2SDFromInput(String path, String fileName, InputStream input) {
        File file = null;
        OutputStream output = null;

        try {
            createSDDir(path);
            file = createFileInSDCard(fileName, path);
            output = new FileOutputStream(file);

            byte[] buffer = new byte[4 * 1024];
            int temp;

            while (-1 != (temp = input.read(buffer))) {
                output.write(buffer, 0, temp);
            }
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    /**
     * 读取path中的MP3文件的名字和大小
     * @param path
     * @return
     */
    public List<Mp3Info> getMp3Files(String path){
        List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
        String filePath = SDCardRoot + File.separator + path;
        File file = new File(filePath);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().endsWith("mp3")) {
                Mp3Info mp3Info = new Mp3Info();
                mp3Info.setMp3Name(files[i].getName());
                mp3Info.setMp3Size(files[i].length() + "");
                String temp [] = mp3Info.getMp3Name().split("\\.");
                String eLrcName = temp[0] + ".lrc";
                if(isFileExist(eLrcName, "/mp3")){
                    File lrcFile = new File(filePath + File.separator + eLrcName);
                    mp3Info.setLrcName(eLrcName);
                    mp3Info.setLrcSize(lrcFile.length() + "");
                }
                mp3Infos.add(mp3Info);
            }
        }

        return mp3Infos;
    }
}
