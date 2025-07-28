package org.example;

import org.jsoup.select.Elements;

public class Sleeper {
    public static void timeout(long sleeptime) {
        try {
            Thread.sleep(sleeptime);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}
