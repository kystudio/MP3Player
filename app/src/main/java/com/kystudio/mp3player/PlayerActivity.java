package com.kystudio.mp3player;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.kystudio.model.Mp3Info;

import java.io.File;

public class PlayerActivity extends AppCompatActivity {
    private Button beginButton, pauseButton, stopButton;
    private MediaPlayer mediaPlayer = null;

    private boolean isPlaying = false;
    private boolean isPause = false;
    private boolean isReleased = false;

    private Mp3Info mp3Info = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Intent intent = getIntent();
        mp3Info = (Mp3Info) intent.getSerializableExtra("mp3Info");

        beginButton = (Button) findViewById(R.id.begin);
        pauseButton = (Button) findViewById(R.id.pause);
        stopButton = (Button) findViewById(R.id.stop);

        beginButton.setOnClickListener(new BeginButtonListener());
        pauseButton.setOnClickListener(new PauseButtonListener());
        stopButton.setOnClickListener(new StopButtonListener());
    }

    class BeginButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (!isPlaying) {
                String path = getMp3Path(mp3Info);
                mediaPlayer = MediaPlayer.create(PlayerActivity.this, Uri.parse("file://" + path));
                mediaPlayer.setLooping(false);
                mediaPlayer.start();

                isPlaying = true;
                isReleased = false;
            }
        }
    }

    class PauseButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (mediaPlayer != null) {
                if (!isReleased) {
                    if (!isPause) {
                        mediaPlayer.pause();

                        isPause = true;
                        isPlaying = false;
                    } else {
                        mediaPlayer.start();

                        isPause = false;
                        isPlaying = true;
                    }
                }
            }
        }
    }

    class StopButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(mediaPlayer!=null){
                if(isPlaying){
                    if(!isReleased){
                        mediaPlayer.stop();
                        mediaPlayer.release();

                        isReleased = true;
                    }
                    isPlaying = false;
                }
            }
        }
    }

    /**
     * 根据传入的Mp3Info对象获取MP3的路径
     * @param mp3Info
     * @return
     */
    private String getMp3Path(Mp3Info mp3Info) {
        String SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        String path = SDCardRoot + File.separator + "mp3" + File.separator + mp3Info.getMp3Name();

        return path;
    }
}
