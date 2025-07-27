import java.io.IOException;
import java.util.Optional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Scraper {
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 60000;
    private static final int TASK_DELAY_MS = 100;




    public static void main(String[] args) {
        new Scraper().scrapeAllContests();
    }

    private void scrapeAllContests() {
        for (int contestNumber = 42; contestNumber < 416; contestNumber++) {
            scrapeContest(contestNumber);
        }
    }

    private void scrapeContest(int contestNumber) {
        String url = buildContestUrl(contestNumber);

        Optional<Document> contestDoc = fetchDocumentWithRetry(url);
        if (contestDoc.isEmpty()) {
            return;
        }

        printContestHeader(contestNumber, url);
        processContestTasks(contestDoc.get());
        printContestFooter();
    }

    private String buildContestUrl(int contestNumber) {
        return "https://atcoder.jp/contests/abc" + String.format("%03d", contestNumber) + "/tasks";
    }

    private Optional<Document> fetchDocumentWithRetry(String url) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                return Optional.of(Jsoup.connect(url).get());
            } catch (org.jsoup.HttpStatusException e) {
                if (shouldRetry(e, attempt, url)) {
                    continue;
                }
                return Optional.empty();
            } catch (IOException e) {
                System.err.println("IO error for " + url + ": " + e.getMessage());
                return Optional.empty();
            }
        }

        System.out.println("Max retries reached for: " + url);
        return Optional.empty();
    }

    private boolean shouldRetry(org.jsoup.HttpStatusException e, int attempt, String url) {
        boolean isRateLimited = (e.getStatusCode() == 429 || e.getStatusCode() == 403);

        if (!isRateLimited || attempt >= MAX_RETRIES) {
            return false;
        }

        System.out.println("Rate limited (429). Timeout for 1 minute " +
                attempt + "/" + MAX_RETRIES);
        sleepSafely(RETRY_DELAY_MS);
        return true;
    }

    private void processContestTasks(Document contestDocument) {
        Elements rows = contestDocument.select("tbody tr");

        for (Element row : rows) {
            processTaskRow(row);
        }
    }

    private void processTaskRow(Element row) {
        Element link = row.selectFirst("a[href]");
        if (link == null) {
            return;
        }

        String relativeUrl = link.attr("href");
        String taskTitle = link.text();
        String taskUrl = "https://atcoder.jp" + relativeUrl;

        printTaskHeader(taskTitle, taskUrl);

        Optional<Document> taskDoc = fetchTaskDocument(taskUrl);
        if (taskDoc.isEmpty()) {
            printTaskError(taskUrl);
            return;
        }

        TaskDetails details = extractTaskDetails(taskDoc.get());
        printTaskDetails(details);

        sleepSafely(TASK_DELAY_MS);
    }

    private Optional<Document> fetchTaskDocument(String taskUrl) {
        try {
            return Optional.of(Jsoup.connect(taskUrl).get());
        } catch (IOException e) {
            System.out.println("Error fetching task page: " + e.getMessage());
            return Optional.empty();
        }
    }

    private TaskDetails extractTaskDetails(Document taskDocument) {
        String title = extractTitle(taskDocument);
        String points = extractPoints(taskDocument);
        String problemStatement = extractProblemStatement(taskDocument);

        return new TaskDetails(title, points, problemStatement);
    }

    private String extractTitle(Document doc) {
        return Optional.ofNullable(doc.selectFirst("span.h2"))
                .map(Element::text)
                .map(text -> text.replace("Editorial", "").trim())
                .orElse("Title not found");
    }

    private String extractPoints(Document doc) {
        Elements scoreElements = doc.select("p:contains(points)");

        for (Element scoreElem : scoreElements) {
            String scoreText = scoreElem.text();
            if (scoreText.contains("points")) {
                return scoreText.replaceAll(".*?(\\d+)\\s*points.*", "$1");
            }
        }

        return "Points not found";
    }

    private String extractProblemStatement(Document doc) {
        Element problemSection = doc.selectFirst("h3:contains(Problem Statement)");
        if (problemSection == null) {
            return "Problem statement not found";
        }

        StringBuilder content = new StringBuilder();
        Element nextSibling = problemSection.nextElementSibling();

        while (nextSibling != null && !nextSibling.tagName().equals("h3")) {
            content.append(nextSibling.text()).append(" ");
            nextSibling = nextSibling.nextElementSibling();
        }

        return content.toString().trim();
    }

    private void sleepSafely(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Sleep interrupted", e);
        }
    }

    // Printing methods
    private void printContestHeader(int contestNumber, String url) {
        String separator = "======================================";
        System.out.println(separator);
        System.out.println("contest number: " + contestNumber);
        System.out.println("contest URL: " + url);
        System.out.println(separator);
    }

    private void printTaskHeader(String taskTitle, String taskUrl) {
        System.out.println("Task: " + taskTitle);
        System.out.println("URL: " + taskUrl);
    }

    private void printTaskDetails(TaskDetails details) {
        System.out.println("Title: " + details.title);
        System.out.println("Points: " + details.points);
        System.out.println("Problem Statement: " + details.problemStatement);
        System.out.println("---");
    }

    private void printContestFooter() {
        System.out.println("======================================");
    }

    private void printTaskError(String taskUrl) {
        System.out.println("Error fetching task page: " + taskUrl);
    }

    // Simple data class
    private static class TaskDetails {
        final String title;
        final String points;
        final String problemStatement;

        TaskDetails(String title, String points, String problemStatement) {
            this.title = title;
            this.points = points;
            this.problemStatement = problemStatement;
        }
    }
}
