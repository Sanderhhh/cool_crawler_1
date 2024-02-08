package Project;

import java.util.*;

public class TextHandler {
    private final int top;                                                             // How many keywords we calculate
    private final String keyword_formula;                                              // either quantity, tf-idf
    private boolean lowercase;                                                   // treat all words as being lowercase

    private Hashtable<String, Integer> word_count;                               // word count of each document
    private Hashtable<String, HashMap<String, Float>> term_frequency_table;    // term_frequency by page title
    private Hashtable<String, HashMap<String, Float>> tfidf_table;             // table with tf-idf values per title
    private Hashtable<String, ArrayList<String>> keywords_table;   // sorted list with keywords per title

    public TextHandler(String keyword_formula, boolean lowercase, int top)
    {
        this.keyword_formula = keyword_formula;
        this.top = top;
        this.lowercase = false;
        this.word_count = new Hashtable<>();
        this.term_frequency_table = new Hashtable<>();
        this.keywords_table = new Hashtable<>();
        this.tfidf_table = new Hashtable<>();
        // Have: string with all the words
        // Need: most important words in the text
        // - Tokenize body
        // - Stem words
        // - Remove redundant words
        // - Create term frequency list for document (probably will hashmap)
        // - Calculate tfidf (probably will use corpus)
        // Tf: number of times each word appears in a document relative to total
        // idf: log(N / n)          N = no. of docs     n = no. of docs with term t
        // Last step: tfidf on the data
    }

    public void preprocess(String body, String title)
    {
        String[] tokenized = tokenize(body);
        word_count.put(title, tokenized.length);
        // System.out.println(tokenized);
        // turn tokenized list into term frequency list
        this.term_frequency_table.put(title, calculate_term_frequency(tokenized));        // add the webpage to the term_frequency dictionary

    }

    public void calculate_word_importance()
    {
        switch (this.keyword_formula)
        {
            case "quantity":
                calculate_keywords_table(this.term_frequency_table);
                break;
            case "tf-idf":
                calculate_tfidf();
        }
        System.out.println(keywords_table);
    }

    private void calculate_tfidf() {

        for (String title : term_frequency_table.keySet())
        {
            HashMap<String, Float> current;
            current = term_frequency_table.get(title);

            HashMap<String, Float> temp_tfidf_map = new HashMap<>();

            float current_word_count = word_count.get(title);

            for (String token : current.keySet()) {
                if (current_word_count > 0) {
                    float idf;
                    int frequency = 1;
                    float tf;
                    tf = current.get(token) / current_word_count;

                    int N = term_frequency_table.size();            // number of documents
                    for (String document : term_frequency_table.keySet()) {
                        HashMap<String, Float> current_hashmap = term_frequency_table.get(document);
                        if (current_hashmap.get(token) != null) {
                            frequency++;
                        }
                    }
                    idf = (float) Math.log((float) N / (float) frequency);
                    float tfidf = tf * idf;
                    temp_tfidf_map.put(token, tfidf);
                }
            }
            this.tfidf_table.put(title, temp_tfidf_map);
        }
        calculate_keywords_table(this.tfidf_table);
    }
    private void calculate_keywords_table(Hashtable<String, HashMap<String, Float>> table)
    {
        for (String title : table.keySet())
        {
            HashMap<String, Float> current = table.get(title);
            ArrayList<String> keywords = new ArrayList<>();
            float max = 0;
            String max_word = "";
            float max_id = java.lang.Math.min(top, current.size());
            for (int idx = 0; idx < max_id; idx++) {
                for (String token : current.keySet()) {
                    if (!keywords.contains(token)) {                 // check if word has already been added
                        if (current.get(token) > max) {
                            max = current.get(token);
                            max_word = token;
                        }
                    }
                }
                keywords.add(max_word);
                max_word = "";
                max = 0;
            }
            keywords_table.put(title, keywords);
        }
    }

    private HashMap<String, Float> calculate_term_frequency(String[] tokenized) {
        HashMap<String, Float> term_frequency = new HashMap<>();
        for (String token : tokenized) {
            if (term_frequency.get(token) != null) {
                term_frequency.put(token, term_frequency.get(token) + 1);
            }
            else
            {
                term_frequency.put(token, (float)1);
            }
        }
        return term_frequency;
    }

    private String[] tokenize(String body)
    {
        // First up; tokenize the list with built-in string tokenizer
        if (this.lowercase)
        {
            body = body.toLowerCase();
        }

        return body.split("\\s");
    }

}