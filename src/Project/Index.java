package Project;

import java.util.ArrayList;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

/**
 * This class takes a url and handles the web requests, returning the necessary data by the end
 */
public class Index {

    public Index() {
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
