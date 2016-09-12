package com.kystudio.mp3player;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.kystudio.download.HttpDownloader;
import com.kystudio.model.Mp3Info;
import com.kystudio.xml.Mp3ListContentHandler;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

public class MP3ListActivity extends ListActivity {
    private static final int UPDATE = 1;
    private static final int ABOUT = 2;
    private String xml;

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
            downloadXML("http://172.28.19.115:8080/MP3/resource.xml");
            return true;
        } else if (id == ABOUT) {


        }

        return super.onOptionsItemSelected(item);
    }

    private void downloadXML(String urlStr){
        MyDownloadThread md = new MyDownloadThread(urlStr);
        md.start();
    }

    private List<Mp3Info> parse(String xmlStr){
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        System.out.println("xmlStr:" + xmlStr);
        try {
            XMLReader xmlReader = saxParserFactory.newSAXParser().getXMLReader();
            List<Mp3Info> infos = new ArrayList<Mp3Info>();
            Mp3ListContentHandler mp3ListContentHandler = new Mp3ListContentHandler(infos);
            xmlReader.setContentHandler(mp3ListContentHandler);
            xmlReader.parse(new InputSource(new StringReader(xmlStr)));

            for (Iterator iterator = infos.iterator();iterator.hasNext();){
                Mp3Info mp3Info = (Mp3Info) iterator.next();
                System.out.println("mp3Info:" + mp3Info);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    class MyDownloadThread extends Thread
    {
        private String urlStr;
        public MyDownloadThread(String urlStr){
            this.urlStr = urlStr;
        }
        @Override
        public void run() {
            HttpDownloader httpDownloader = new HttpDownloader();
            String result = httpDownloader.downloadTxt(urlStr);

            parse(result);
        }
    }
}
