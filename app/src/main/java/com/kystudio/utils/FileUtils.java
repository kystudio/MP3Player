package com.kystudio.utils;

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
}
