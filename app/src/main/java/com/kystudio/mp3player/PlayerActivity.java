package com.kystudio.mp3player;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
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

    private Mp3Info mp3Info = null;
    private IntentFilter intentFilter = null;
    private BroadcastReceiver receiver = null;

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

    class BeginButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // 创建一个Intent对象，用于通知service开始播放MP3
            Intent intent = new Intent();
            intent.setClass(PlayerActivity.this, PlayerService.class);
            intent.putExtra("mp3Info", mp3Info);
            intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);
            startService(intent);
        }
    }

    class PauseButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(PlayerActivity.this, PlayerService.class);
            intent.putExtra("MSG", AppConstant.PlayerMsg.PAUSE_MSG);
            startService(intent);
        }
    }

    class StopButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(PlayerActivity.this, PlayerService.class);
            intent.putExtra("MSG", AppConstant.PlayerMsg.STOP_MSG);
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new LrcMessageBroadcastReceiver();
        registerReceiver(receiver, getIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    /**
     * 广播接收器，主要作用是接受Service所发送的广播，并且更新UI，也就是放置歌词到TextView
     */
    class LrcMessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String lrcMessage = intent.getStringExtra("lrcMessage");
            lrcTextView.setText(lrcMessage);
        }
    }

    private IntentFilter getIntentFilter() {
        if (intentFilter == null) {
            intentFilter = new IntentFilter();
            intentFilter.addAction(AppConstant.LRC_MESSAGE_ACTION);
        }
        return intentFilter;
    }
}
