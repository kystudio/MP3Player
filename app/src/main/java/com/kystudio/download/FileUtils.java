package com.kystudio.download;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by 20236320 on 2016/8/14.
 */
public class FileUtils {
    private String SDPATH;

    public String getSDPATH() {
        return SDPATH;
    }

    public FileUtils() {
        SDPATH = Environment.getExternalStorageDirectory() + "/";
    }

    /**
     * 判断SD卡上的文件是否存在
     *
     * @param fileName
     * @return
     */
    public boolean isFileExist(String fileName) {
        File file = new File(SDPATH + fileName);
        return file.exists();
    }

    /**
     * 在SD卡上创建目录
     *
     * @param dirName
     * @return
     */
    public File createSDDir(String dirName) {
        File dir = new File(SDPATH + dirName);
        dir.mkdir();

        return dir;
    }

    /**
     * 在SD卡上创建文件
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public File createSDFile(String fileName) throws IOException {
        File file = new File(SDPATH + fileName);
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
            file = createSDFile(path + fileName);
            output = new FileOutputStream(file);

            byte[] buffer = new byte[4 * 1024];

            while (-1 != (input.read(buffer))) {
                output.write(buffer);
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
}
