package com.nserly.Main.WebCrawlers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        String url = "https://cn.bing.com/search?q=MicrosoftEdge如何下载"; // 要抓取的网页地址
        try {
            // 发送HTTP请求，获取网页内容
            Document document = Jsoup.connect(url).get();
            // 提取网页的标题
            String title = document.title();

            System.out.println("标题：" + title);
            // 提取网页的正文内容
            Element contentElement = document.body();
            String str = contentElement.toString();
            String[] strings = str.split("\n");
            ArrayList<String> arrayList = new ArrayList<>();
            for (String s : strings) {
                if (s.contains("href=")&&s.contains("\" h=\"ID=")) {
                    arrayList.add(s.substring(s.indexOf("href=\"")+6, s.lastIndexOf("\" h=\"ID=")));
                }
            }
            for(String s : arrayList){
                System.out.println(s);
            }
            String content = contentElement.text();
//            System.out.println("正文：" + content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
