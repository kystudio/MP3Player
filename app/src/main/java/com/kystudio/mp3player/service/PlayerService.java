package com.kystudio.mp3player.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.util.StringBuilderPrinter;

import com.kystudio.lrc.LrcProcessor;
import com.kystudio.model.Mp3Info;
import com.kystudio.mp3player.AppConstant;
import com.kystudio.mp3player.PlayerActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Queue;

public class PlayerService extends Service {
    private boolean isPlaying = false;
    private boolean isPause = false;
    private boolean isReleased = false;
    private MediaPlayer mediaPlayer = null;

    private ArrayList<Queue> queues = null;
    private UpdateTimeCallback updateTimeCallback = null;
    private Handler handler = new Handler();
    private long begin = 0;
    private long nextTimeMill = 0;
    private long currentTimeMill = 0;
    private long pauseTimeMill = 0;
    private String message = null;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Mp3Info mp3Info = (Mp3Info) intent.getSerializableExtra("mp3Info");
        int MSG = intent.getIntExtra("MSG", 0);

        if (mp3Info != null) {
            if (MSG == AppConstant.PlayerMsg.PLAY_MSG) {
                play(mp3Info);
            }
        } else {
            if (MSG == AppConstant.PlayerMsg.PAUSE_MSG) {
                pause();
            } else if (MSG == AppConstant.PlayerMsg.STOP_MSG) {
                stop();
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void play(Mp3Info mp3Info) {
        if (!isPlaying) {
            String path = getMp3Path(mp3Info);
            mediaPlayer = MediaPlayer.create(this, Uri.parse("file://" + path));
            mediaPlayer.setLooping(false);
            mediaPlayer.start();
            // 读取lrc文件
            prepareLrc(mp3Info.getLrcName());
            handler.postDelayed(updateTimeCallback, 5);
            // 将begin的值设置为当前毫秒数
            begin = System.currentTimeMillis();

            isPlaying = true;
            isReleased = false;
        }
    }

    private void pause() {
        if (isPlaying) {
            mediaPlayer.pause();
            handler.removeCallbacks(updateTimeCallback);
            pauseTimeMill = System.currentTimeMillis();
        } else {
            mediaPlayer.start();
            handler.postDelayed(updateTimeCallback, 5);
            begin = System.currentTimeMillis() - pauseTimeMill + begin;
        }
        isPlaying = isPlaying ? false : true;
    }

    private void stop() {
        if (mediaPlayer != null) {
            if (isPlaying) {
                if (!isReleased) {
                    handler.removeCallbacks(updateTimeCallback);
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    isReleased = true;
                }
                isPlaying = false;
            }
        }
    }

    /**
     * 根据传入的Mp3Info对象获取MP3的路径
     *
     * @param mp3Info
     * @return
     */
    private String getMp3Path(Mp3Info mp3Info) {
        String SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        String path = SDCardRoot + File.separator + "mp3" + File.separator + mp3Info.getMp3Name();

        return path;
    }

    private void prepareLrc(String lrcName) {
        String lrcPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "mp3" + File.separator + lrcName;
        try {
            InputStream inputStream = new FileInputStream(lrcPath);
            LrcProcessor lrcProcessor = new LrcProcessor();
            queues = lrcProcessor.process(inputStream);

            updateTimeCallback = new UpdateTimeCallback(queues);
            begin = 0;
            currentTimeMill = 0;
            nextTimeMill = 0;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    class UpdateTimeCallback implements Runnable {
        Queue times = null;
        Queue messages = null;

        public UpdateTimeCallback(ArrayList<Queue> queues) {
            times = queues.get(0);
            messages = queues.get(1);
        }

        @Override
        public void run() {
            // 计算偏移量，也就是说从开始播放MP3到现在为止，共消耗了多少时间，以毫秒为单位
            long offset = System.currentTimeMillis() - begin;
            if (0 == currentTimeMill) {
                nextTimeMill = (long) times.poll();
                message = (String) messages.poll();
            }

            if (offset >= nextTimeMill) {
                message = (String) messages.poll();
                nextTimeMill = (long) times.poll();

                Intent intent = new Intent();
                intent.setAction(AppConstant.LRC_MESSAGE_ACTION);
                intent.putExtra("lrcMessage", message);
                sendBroadcast(intent);
            }

            currentTimeMill = currentTimeMill + 5;
            handler.postDelayed(updateTimeCallback, 5);
        }
    }
}
