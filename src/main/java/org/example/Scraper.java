package org.example;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Scraper {

    public static void main(String[] args) {
        for (int contestnumber = 42; contestnumber < 416; contestnumber++) {
            String url = "https://atcoder.jp/contests/abc" + String.format("%03d", contestnumber) + "/tasks";
            Elements rows = Taskurl.get(contestnumber);
            if(rows == null){
                continue;
            }
            Taskdata.get(rows);
            System.out.println("======================================");
        }
    }
}