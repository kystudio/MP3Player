package com.kystudio.mp3player;

/**
 * Created by 20236320 on 2016/9/18.
 */
public interface AppConstant {
    public class PlayerMsg{
        public static final int PLAY_MSG = 1;
        public static final int PAUSE_MSG = 2;
        public static final int STOP_MSG = 1;
    }

    public class URL{
        // 公司电脑的IP
        public static final String BASE_URL = "http://172.28.19.115:8080/MP3/";
        // 家里电脑的IP
        // public static final String BASE_URL = "http://192.168.3.105:8080/MP3/";
    }

    public static final String LRC_MESSAGE_ACTION = "com.kystudio.mp3player.lrcmessage.action";
}
