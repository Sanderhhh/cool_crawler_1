package Project;

import java.util.*;

public class TextHandler {
    private int top;                                                             // How many keywords we calculate
    private String keyword_formula;                                              // either quantity, tf-idf
    private boolean trim_stopwords;                                              // whether stop-words are trimmed
    private Hashtable<String, HashMap<String, Integer>> term_frequency_table;    // term_frequency by page title
    private Hashtable<String, ArrayList<String>> word_importance_table;   // sorted list with keywords per title

    public TextHandler(String keyword_formula, boolean trim_stopwords)
    {
        this.keyword_formula = keyword_formula;
        this.trim_stopwords = trim_stopwords;
        term_frequency_table = new Hashtable<>();
        word_importance_table = new Hashtable<>();
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
        ArrayList<String> tokenized = tokenize(body);
        // System.out.println(tokenized);
        // turn tokenized list into term frequency list
        this.term_frequency_table.put(title, hash(tokenized));        // add the webpage to the term_frequency dictionary
        calculate_word_importance(title);
    }

    private void calculate_word_importance(String title) {
        switch (this.keyword_formula)
        {
            case "quantity":
                HashMap<String, Integer> current = term_frequency_table.get(title);
        }

    }

    private HashMap<String, Integer> hash(ArrayList<String> tokenized) {
        HashMap<String, Integer> term_frequency = new HashMap<String, Integer>();
        for (String token : tokenized) {
            if (term_frequency.get(token) != null) {
                term_frequency.put(token, term_frequency.get(token) + 1);
            }
            else
            {
                term_frequency.put(token, 1);
            }
        }
        return term_frequency;
    }

    private ArrayList<String> tokenize(String body)
    {
        // First up; tokenize the list with built-in string tokenizer
        StringTokenizer st = new StringTokenizer(body);
        ArrayList<String> tokenized = new ArrayList<>();
        while(st.hasMoreTokens())
        {
            tokenized.add(st.nextToken());
        }
        return tokenized;
    }

}