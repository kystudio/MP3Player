package com.kystudio.lrc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 20236320 on 2016/9/18.
 */
public class LrcProcessor {
    public ArrayList<Queue> process(InputStream inputStream) {
        Queue<Long> timeMills = new LinkedList<Long>();
        Queue<String> messages = new LinkedList<String>();
        ArrayList<Queue> queues = new ArrayList<Queue>();

        try {
            InputStreamReader inputReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputReader);
            String temp = null;
            int i = 0;
            Pattern p = Pattern.compile("\\[([^\\]]+)\\]");
            String result = null;
            boolean b = true;

            while (null != (temp = bufferedReader.readLine())) {
                i++;
                Matcher m = p.matcher(temp);
                if (m.find()) {
                    if (result != null) {
                        messages.add(result);
                    }
                    String timeStr = m.group();
                    Long timeMill = time2Long(timeStr.substring(1, timeStr.length() - 1));
                    if (b) {
                        timeMills.offer(timeMill);
                    }
                    String msg = temp.substring(10);
                    result = "" + msg + "\n";
                } else {
                    result = result + temp + "\n";
                }
            }
            messages.add(result);
            queues.add(timeMills);
            queues.add(messages);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return queues;
    }

    /**
     * 将分钟，秒全部转换成毫秒
     *
     * @param timeStr
     * @return
     */
    public Long time2Long(String timeStr) {
        String s[] = timeStr.split(":");
        int min = Integer.parseInt(s[0]);
        String ss[] = s[1].split("\\.");
        int sec = Integer.parseInt(ss[0]);
        int mill = Integer.parseInt(ss[1]);

        return min * 60 * 1000 + sec * 1000 + mill * 10L;
    }
}
