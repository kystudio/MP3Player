package com.kystudio.mp3player;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kystudio.lrc.LrcProcessor;
import com.kystudio.model.Mp3Info;
import com.kystudio.mp3player.service.PlayerService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Queue;

public class PlayerActivity extends Activity {
    private Button beginButton, pauseButton, stopButton;
    private TextView lrcTextView;

    private ArrayList<Queue> queues = null;
    private Mp3Info mp3Info = null;
    private UpdateTimeCallback updateTimeCallback = null;
    private Handler handler = new Handler();
    private long begin = 0;
    private long nextTimeMill = 0;
    private long currentTimeMill = 0;
    private long pauseTimeMill = 0;
    private String message = null;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);

        Intent intent = getIntent();
        mp3Info = (Mp3Info) intent.getSerializableExtra("mp3Info");

        beginButton = (Button) findViewById(R.id.begin);
        pauseButton = (Button) findViewById(R.id.pause);
        stopButton = (Button) findViewById(R.id.stop);
        lrcTextView = (TextView) findViewById(R.id.lrcText);

        beginButton.setOnClickListener(new BeginButtonListener());
        pauseButton.setOnClickListener(new PauseButtonListener());
        stopButton.setOnClickListener(new StopButtonListener());
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
            System.out.println("fileNotFound:" + e);
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
            // 计算偏移量，也就是说从开始播放MP3到现在为止，共消耗了多少时间，以毫秒未单位
            long offset = System.currentTimeMillis() - begin;
            System.out.println("offset---> " + offset);
            if (0 == currentTimeMill) {
                nextTimeMill = (long) times.poll();
                message = (String) messages.poll();
            }

            if (offset >= nextTimeMill) {
                lrcTextView.setText(message);
                message = (String) messages.poll();
                nextTimeMill = (long) times.poll();
            }

            currentTimeMill = currentTimeMill + 10;
            handler.postDelayed(updateTimeCallback, 10);
        }
    }

    class BeginButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // 创建一个Intent对象，用于通知service开始播放MP3
            Intent intent = new Intent();
            intent.setClass(PlayerActivity.this, PlayerService.class);
            intent.putExtra("mp3Info", mp3Info);
            intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);
            // 读取lrc文件
            prepareLrc(mp3Info.getLrcName());
            startService(intent);
            // 将begin的值设置为当前毫秒数
            begin = System.currentTimeMillis();
            handler.postDelayed(updateTimeCallback, 5);
            isPlaying = true;
        }
    }

    class PauseButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(PlayerActivity.this, PlayerService.class);
            intent.putExtra("MSG", AppConstant.PlayerMsg.PAUSE_MSG);
            startService(intent);

            if (isPlaying){
                handler.removeCallbacks(updateTimeCallback);
                pauseTimeMill = System.currentTimeMillis();
            }else {
                handler.postDelayed(updateTimeCallback, 5);
                begin = System.currentTimeMillis() - pauseTimeMill + begin;
            }
            isPlaying = isPlaying ? false:true;
        }
    }

    class StopButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(PlayerActivity.this, PlayerService.class);
            intent.putExtra("MSG", AppConstant.PlayerMsg.STOP_MSG);
            startService(intent);
            handler.removeCallbacks(updateTimeCallback);
        }
    }
}
