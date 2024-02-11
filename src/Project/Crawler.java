package Project;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Main crawler class that iterates through the links in the website and stores the indexed information that
 * we will analyze later.
 */
public class Crawler {
    private final int max_steps;
    private final boolean stay_on_website;
    long endtime;
    private final String url;
    private ArrayList<String> visited_list = new ArrayList<>();
    private TextHandler th;

    /**
     *
     * @param url               String with the first part of the link. e.g. https://www.example.com
     * @param max_steps         Amount of steps that the algorithm will travel away from the homepage
     * @param stay_on_website   Whether we want to stay on the same website or not.
     * @param time_in_seconds   Time that the crawler should run for
     * @param th                TextHandler class that will process the text
     */
    public Crawler(String url, int max_steps, boolean stay_on_website, long time_in_seconds, TextHandler th) {
        this.url = url;
        this.max_steps = max_steps;
        this.stay_on_website = stay_on_website;
        this.endtime = System.currentTimeMillis() + (time_in_seconds * 1000);
        this.th = th;
    }

    /**
     * This function performs the main crawling part of the program.
     * It is recursive, so pay close attention to the step limit in order to prevent
     * memory issues.
     * In this function, the website data is gathered, sent to the texthandler, and
     * the other links are then crawled recursively.
     */
    public void crawl(int current_step, String current_url){
        // return if the page has already been visited, or if we are over the time limit
        if(already_visited(current_url))
        {
            return;
        }
        if(System.currentTimeMillis() >= this.endtime)
        {
            // TODO: Program exceeds time by definition
            // TODO: because we need to return from this function multiple times.
            return;
        }

        visited_list.add(current_url);                              // website has been visited
        ArrayList<String> text_and_links = fetch_data(current_url); // connect to the website and get the data
        th.preprocess(text_and_links.get(0), current_url);
        text_and_links.remove(0);                              // remove the body and pass the rest
        th.calculate_keywords();

        if (current_step != max_steps) {                    // don't want to waste time on links over the step limit
            if(stay_on_website){
                crawl_arraylist_regex(text_and_links, current_step);
            }
            else
            {
                crawl_arraylist(text_and_links, current_step);
            }
        }
    }

    /**
     * This function performs a naive crawl with no criteria.
     * @param urls          Arraylist of every url that we want to visit
     * @param current_step  the step that we are currently at
     */
    private void crawl_arraylist(ArrayList<String> urls, int current_step) {
        for (String candidate_url : urls) {
            crawl(current_step + 1, candidate_url);
        }
    }

    /**
     * This function performs a crawl after matching each url to a regex with the website that
     * we are indexing.
     * @param urls          Arraylist of every url that we want to visit
     * @param current_step  the step that we are currently at
     */
    private void crawl_arraylist_regex(ArrayList<String> urls, int current_step) {
        Pattern pattern = Pattern.compile(url, Pattern.CASE_INSENSITIVE);   // url that we want to match
        boolean matchFound;
        for (String candidate_url : urls) {
            Matcher matcher = pattern.matcher(candidate_url);
            matchFound = matcher.find();
            if (matchFound) {                                               // only crawl url if regex has been matched
                crawl(current_step + 1, candidate_url);
            }
        }
    }

    /**
     * Checks if a url has been visited
     * @return boolean; true if visited, false otherwise
     */
    private boolean already_visited(String current_url) {
        for (String visited : visited_list) {              // Check if current url is present in the visited_list
            if (Objects.equals(current_url, visited)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This function connects to a website and fetches the data that we need in order to determine the keywords.
     * @param full_url  Full url used to connect to the website
     * @return          Returns an ArrayList<String>. The first index of this arraylist will have the
     *                  text body of the page, whereas the other indexes will consist of every link on the webpage.
     */
    public ArrayList<String> fetch_data(String full_url) {
        Document document;
        ArrayList<String> text_and_links = new ArrayList<>();
        try {
            Connection connection = Jsoup.connect(full_url);
            document = connection.get();

            if (document == null) {
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
