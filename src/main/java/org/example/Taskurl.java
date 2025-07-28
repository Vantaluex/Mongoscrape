package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Taskurl {
    public static Elements get(int contestnumber) {
        String url = "https://atcoder.jp/contests/abc" + String.format("%03d", contestnumber) + "/tasks";
        Document contestdocument = null;
        int maxRetries = 3;
        int retryCount = 0;

        while (contestdocument == null && retryCount < maxRetries) {
            try {
                contestdocument = Jsoup.connect(url).get();
            } catch (org.jsoup.HttpStatusException e) {
                if (e.getStatusCode() == 429 || e.getStatusCode() == 403) {
                    retryCount++;
                    System.out.println("Rate limited (429). Waiting 5 seconds before retry " + retryCount + "/" + maxRetries);
                    Sleeper.timeout(5000);
                }
                if (retryCount >= maxRetries) {
                    System.out.println("Max retries reached for: " + url);
                    continue; // Skip to next contest
                }
            } catch (java.io.IOException e) { // Add this catch block
                System.out.println("IO error: " + e.getMessage());
                retryCount++;
                if (retryCount >= maxRetries) {
                    break;
                }
                Sleeper.timeout(5000);
            }
        }
        if (contestdocument == null) {
            return null;
        }

        System.out.println("======================================");
        System.out.println("contest number: " + contestnumber);
        System.out.println("contest URL: " + url);
        System.out.println("======================================");
        return contestdocument.select("tbody tr");
    }
}
