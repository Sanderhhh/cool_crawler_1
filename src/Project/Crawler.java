package Project;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Queue;

/**
 * Main crawler class that iterates through the links in the website and stores the indexed information that
 * we will analyze later.
 */
public class Crawler {
    private final int max_steps;
    private ArrayList<String> visited_list = new ArrayList<>();

    public Crawler(String url, String suburl, int max_steps) {
        this.max_steps = max_steps;
        Crawl(1, url + suburl);
    }

    private void Crawl(int current_step, String current_url){
        for (String visited : visited_list) {              // Check if current url is present in the list
            if (Objects.equals(current_url, visited)) {    // TODO: more efficient data structure?
                return;
            }
        }
        visited_list.add(current_url);
        ArrayList<String> text_and_links = fetch_data(current_url);
        TextHandler th = new TextHandler(text_and_links.get(0));
        // TODO: Do something with the body
        text_and_links.remove(0);                     // remove the body, now there are only links left.
        if (current_step != max_steps) {                    // don't want to waste time on links over the step limit
            for (String URL : text_and_links) {
                Crawl(current_step + 1, URL);
            }
        }
    }

    public ArrayList<String> fetch_data(String full_url) {
        Document document;
        ArrayList<String> text_and_links = new ArrayList<>();
        try {
            Connection connection = Jsoup.connect(full_url);
            document = connection.get();

            if (document == null)
            {
                return null;
            }
        }
        catch(IOException e) {
            return null;
        }
        text_and_links.add(document.body().text());                 // body of the page in the first array-list slot
        for (Element href : document.select("a[href]")) {   // all the other slots will be the links
            text_and_links.add(href.absUrl("href"));
        }
        return text_and_links;
    }
}
