package com.kystudio.download;

import com.kystudio.utils.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by 20236320 on 2016/8/14.
 */
public class HttpDownloader {
    private URL url = null;

    /**
     * 根据URL下载文件，前提是文件内容是文本，函数的返回值是文件的内容。
     *
     * @param urlStr
     * @return
     */
    public String downloadTxt(String urlStr) {
        StringBuffer sb = new StringBuffer();
        String line = null;
        BufferedReader buffer = null;

        try {
            url = new URL(urlStr);

            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            buffer = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

            while (null != (line = buffer.readLine())) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("e:" + e);
        } finally {
            try {
                buffer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    /**
     * 可以下载任何格式文件
     * 返回整形（-1：下载文件出错；0：文件已存在；1：下载文件成功）
     *
     * @param urlStr
     * @param path
     * @param fileName
     * @return
     */
    public int downloadFile(String urlStr, String path, String fileName) {
        InputStream inputStream = null;

        try {
            FileUtils fileUtils = new FileUtils();

            if (fileUtils.isFileExist(fileName, path)) {
                return 0;
            } else {
                inputStream = getInputStreamFromUrl(urlStr);
                File resultFile = fileUtils.write2SDFromInput(path, fileName, inputStream);
                if (resultFile == null) {
                    return -1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return 1;
    }

    /**
     * 根据URL得到输入流
     *
     * @param urlStr
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    public InputStream getInputStreamFromUrl(String urlStr) throws MalformedURLException, IOException {
        url = new URL(urlStr);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        InputStream inputStream = urlConn.getInputStream();

        return inputStream;
    }
}
