package Project;

public class Main {
    public static void main(String[] args) {
        TextHandler th = new TextHandler("tf-idf", false,10);
        Crawler crawler = new Crawler("https://en.wikipedia.org", "/wiki/Open-source_intelligence",
                2, th);
        th.calculate_word_importance();
        return;
    }
}