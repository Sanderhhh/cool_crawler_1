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
    private TextHandler th;

    public Crawler(String url, String suburl, int max_steps, TextHandler th) {
        this.max_steps = max_steps;
        this.th = th;
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
        th.preprocess(text_and_links.get(0), text_and_links.get(1));
        // TODO: Do something with the body
        text_and_links.remove(0);                     // remove the body
        text_and_links.remove(0);                     // remove the title, now there are only links left.
        if (current_step != max_steps) {                    // don't want to waste time on links over the step limit
            for (String URL : text_and_links) {
                Crawl(current_step + 1, URL);
            }
        }
        if (visited_list.size() == 10)
        {
            th.calculate_word_importance();
            return;
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
        text_and_links.add(document.title());                       // title of the page in the second slot
        for (Element href : document.select("a[href]")) {   // all the other slots will be the links
            text_and_links.add(href.absUrl("href"));
        }
        return text_and_links;
    }
}
