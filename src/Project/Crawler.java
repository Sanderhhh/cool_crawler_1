package Project;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Queue;

/**
 * Main crawler class that iterates through the links in the website and stores the indexed information that
 * we will analyze later.
 */
public class Crawler {
    private String url;
    private int max_steps;
    private ArrayList<String> visited_list = new ArrayList<>();
    private Index index = new Index();

    public Crawler(String url, String suburl, int max_steps) {
        this.url = url;
        this.max_steps = max_steps;
        Crawl(1, url + suburl);
    }

    private void Crawl(int current_step, String current_url){
        for (String visited : visited_list) {              // Check if current url is present in the list
            if (Objects.equals(current_url, visited)) {    // TODO: more efficient data structure?
                return;
            }
        }
        // TODO: Don't crawl suffixed links! (such as links with '.pdf' at the end
        visited_list.add(current_url);
        ArrayList<String> text_and_links = index.fetch_data(current_url);
        text_and_links.remove(0);                     // remove the body, now there are only links left.
        // TODO: Do something with the body
        if (current_step != max_steps) {                    // don't want to waste time on links over the step limit
            for (String URL : text_and_links) {
                Crawl(current_step + 1, URL);
            }
        }
    }
}
