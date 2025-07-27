package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Taskdata {
    public static void get(Elements rows) {
        for (Element row : rows) {
            Element link = row.selectFirst("a[href]");
            if (link == null) {
                continue;
            }
            String relativeurl = link.attr("href");
            String tasktitle = link.text();

            String taskurl = "https://atcoder.jp" + relativeurl;
            System.out.println("Task: " + tasktitle);
            System.out.println("URL: " + taskurl);
            try {
                Document taskdocument = Jsoup.connect(taskurl).get();
                // Extract title - AtCoder uses span with class "h2" for the main title
                Element titleElement = taskdocument.selectFirst("span.h2");
                String title = titleElement != null ? titleElement.text().replace("Editorial", "").trim() : "Title not found";

                // Extract points/score - Looking for the specific pattern in AtCoder
                String points = "Points not found";

                // Try multiple selectors for score
                Element scoreElements = taskdocument.selectFirst("p:contains(points)");
                if(scoreElements!= null){
                    points = scoreElements.text();
                }

                // Extract problem statement
                Element problemSection = taskdocument.selectFirst("h3:contains(Problem Statement)");
                String problemStatement = "Problem statement not found";

                if (problemSection != null) {
                    Element parentDiv = problemSection.parent();
                    if (parentDiv != null) {
                        // Get the next sibling elements until we hit another h3
                        Elements problemContent = new Elements();
                        Element nextSibling = problemSection.nextElementSibling();

                        while (nextSibling != null && !nextSibling.tagName().equals("h3")) {
                            problemContent.add(nextSibling);
                            nextSibling = nextSibling.nextElementSibling();
                        }

                        StringBuilder sb = new StringBuilder();
                        for (Element elem : problemContent) {
                            sb.append(elem.text()).append(" ");
                        }
                        problemStatement = sb.toString().trim();
                    }
                }

                System.out.println("Title: " + title);
                System.out.println("Points: " + points);
                System.out.println("Problem Statement: " + problemStatement);
                System.out.println("---");
                Mongo.insert(relativeurl, title, points, problemStatement);
                try {
                    Thread.sleep(100); // Wait 0.5 second between each contest
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } catch (IOException e) {
                System.out.println("Error fetching task page: " + e.getMessage());
            }
        }
    }
}
