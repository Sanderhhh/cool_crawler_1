package Project;

public class Main {
    public static void main(String[] args) {
        TextHandler th = new TextHandler("quantity", false);
        Crawler crawler = new Crawler("https://en.wikipedia.org", "/wiki/Open-source_intelligence",
                2, th);
    }
}