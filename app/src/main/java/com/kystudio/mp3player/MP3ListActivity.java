package com.kystudio.mp3player;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.kystudio.download.HttpDownloader;

public class MP3ListActivity extends ListActivity {
    private static final int UPDATE = 1;
    private static final int ABOUT = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp3_list);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        menu.add(0, UPDATE, 1, R.string.mp3list_update);
        menu.add(0, ABOUT, 2, R.string.mp3list_about);
        return super.onCreateOptionsMenu(menu);

//        getMenuInflater().inflate(R.menu.menu_mp3_list, menu);
//        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == UPDATE) {
            String xml = downloadXML("http://192.168.3.105:8080/MP3/resource.xml");

            System.out.println(xml);
            return true;
        } else if (id == ABOUT) {


        }

        return super.onOptionsItemSelected(item);
    }

    private String downloadXML(String urlStr){
        HttpDownloader httpDownloader = new HttpDownloader();
        String result = httpDownloader.downloadTxt(urlStr);

        return result;
    }
}
