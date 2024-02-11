package Project;

public class Main {

    /**
     * Simple main function that runs the crawler and takes the keywords corresponding to pages on the internet.
     * The keywords are finally output to 'output.txt' after the program is done.
     */
    public static void main(String[] args) {
        String url = "https://en.wikipedia.org";            // e.g. https://www.example.com
        String suburl = "/wiki/Open-source_intelligence";   // e.g. /home/example

        TextHandler th = new TextHandler("tf-idf",
                                        true,
                                        10,
                                        100);

        Crawler crawler = new Crawler(url,
                                    2,
                                    true,
                                    360,
                                        th);

        crawler.crawl(1, url + suburl);
        th.output_to_file();
    }
}