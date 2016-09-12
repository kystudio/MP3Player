package com.kystudio.xml;

import com.kystudio.model.Mp3Info;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;

/**
 * Created by 20236320 on 2016/9/12.
 */
public class Mp3ListContentHandler extends DefaultHandler {
    private List<Mp3Info> infos = null;
    private Mp3Info mp3Info = null;
    private String tagName = null;

    public Mp3ListContentHandler(List<Mp3Info> infos) {
        this.infos = infos;
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        if (tagName.equals("reosource")) {
            infos.add(mp3Info);
        }
        tagName = null;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        this.tagName = localName;
        if (tagName.equals("resource")) {
            mp3Info = new Mp3Info();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
//        this.tagName = localName;
//        if (tagName.equals("reosource")) {
//            infos.add(mp3Info);
//        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String tmp = new String(ch, start, length);
        if (tagName.equals("id")) {
            mp3Info.setId(tmp);
        } else if (tagName.equals("mp3.name")) {
            mp3Info.setMp3Name(tmp);
        } else if (tagName.equals("mp3.size")) {
            mp3Info.setMp3Size(tmp);
        } else if (tagName.equals("lrc.name")) {
            mp3Info.setLrcName(tmp);
        } else if (tagName.equals("lrc.size")) {
            mp3Info.setLrcSize(tmp);
        }
    }
}
