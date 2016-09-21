package com.kystudio.mp3player.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.kystudio.download.HttpDownloader;
import com.kystudio.model.Mp3Info;
import com.kystudio.mp3player.AppConstant;

/**
 * Created by kystudio on 2016/9/14.
 */
public class DownloadService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Mp3Info mp3Info = (Mp3Info) intent.getSerializableExtra("mp3Info");

        DownloadThread downloadThread = new DownloadThread(mp3Info);
        new Thread(downloadThread).start();
        return super.onStartCommand(intent, flags, startId);
    }

    class DownloadThread implements Runnable{
        private Mp3Info mp3Info = null;
        public DownloadThread(Mp3Info mp3Info){
            this.mp3Info = mp3Info;
        }
        @Override
        public void run() {
            String mp3Url = AppConstant.URL.BASE_URL  + mp3Info.getMp3Name();
            HttpDownloader httpDownloader = new HttpDownloader();
            int result = httpDownloader.downloadFile(mp3Url,"mp3",mp3Info.getMp3Name());

            String resultMsg = "";
            if (-1 == result){
                resultMsg = "下载失败";
            }else if (0==result){
                resultMsg = "文件已经存在，不需要重复下载";
            }else if (1==result){
                resultMsg = "文件下载成功";
            }
            System.out.println("result--->" + resultMsg);
            // 使用Notification提示客户下载结果
            // NotificationManager notificationManager = (NotificationManager) DownloadService.this.getSystemService(Context.NOTIFICATION_SERVICE);

        }
    }
}
