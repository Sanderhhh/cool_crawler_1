package Project;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.StringTokenizer;

/**
 * The TextHandler class preprocesses the text, as well as determines the keywords.
 */
public class TextHandler {
    private final int top;                                                     // How many keywords we calculate
    private final int corpus_size;
    private final String keyword_formula;                                      // either quantity, tf-idf
    private final boolean lowercase;                                           // treat all words as being lowercase

    private Hashtable<String, Integer> word_count;                             // word count of each document
    private Hashtable<String, HashMap<String, Float>> term_frequency_table;    // term_frequency by page title
    private Hashtable<String, HashMap<String, Float>> tfidf_table;             // table with tf-idf values per title
    private Hashtable<String, ArrayList<String>> keywords_table;               // sorted list with keywords per title

    /**
     * @param keyword_formula   Either "quanity" or "tf-idf". Determines the method that is used to get the keywords
     * @param lowercase         Determines whether the text in the body is converted to lowercase during preprocessing
     * @param top               The amount of keywords that we want to determine.
     * @param corpus_size       The size of the corpus used in tf-idf. Corpus is drawn from the crawled documents.
     */
    public TextHandler(String keyword_formula, boolean lowercase, int top, int corpus_size)
    {
        this.keyword_formula = keyword_formula;
        this.lowercase = lowercase;
        this.top = top;
        this.corpus_size = corpus_size;

        this.word_count = new Hashtable<>();
        this.term_frequency_table = new Hashtable<>();
        this.keywords_table = new Hashtable<>();
        this.tfidf_table = new Hashtable<>();
    }

    /**
     * Preprocessing happens in several steps: tokenization -> word count -> create term frequency table
     * @param body      Main body of the text that we want to analyse
     * @param title     The title that we will label this page under in the output
     */
    public void preprocess(String body, String title)
    {
        // TODO: Stop using the title in order to determine the key of the page in the map,
        // TODO: different pages might share the same title.
        String[] tokenized = tokenize(body);
        word_count.put(title, tokenized.length);
        // turn tokenized list into term frequency list
        this.term_frequency_table.put(title, calculate_term_frequency(tokenized));

    }

    /**
     * This function calls the keyword determination function based on whether the corpus threshold has been reached
     * and our preferred keyword formula.
     */
    public void calculate_keywords() {
        // only calculate word importance if corpus is big enough
        if(term_frequency_table.size() >= corpus_size) {
            switch (this.keyword_formula) {
                case "quantity":
                    for(String title : this.term_frequency_table.keySet()) {
                        calculate_keywords_table(title, this.term_frequency_table);
                    }
                    break;
                case "tf-idf":
                    calculate_tfidf();
            }
        }
    }

    /**
     * Calculates the tf-idf values of each document that has not been processed yet.
     * This version iterates over all documents, whereas the function calculate_document_tfidf iterates
     * within one document.
     */
    private void calculate_tfidf() {
        int processed_documents = keywords_table.size();            // the amount of documents that have keywords

        // The keys are the titles of the documents, we want to them in a seperate variable for readability.
        String[] document_titles = term_frequency_table.keySet().toArray(new String[0]);
        String title;

        // now, we loop over the documents that need keywords
        for (int idx = processed_documents; idx < term_frequency_table.size(); idx++) {
            title = document_titles[idx];                               // document title
            HashMap<String, Float> current_term_frequency;
            current_term_frequency = term_frequency_table.get(title);   // term frequency table of current document

            HashMap<String, Float> temp_tfidf_map = new HashMap<>();    // temporary map for tfidf values of this doc
            float current_word_count = word_count.get(title);
            if (current_word_count > 0) {
                calculate_document_tfidf(temp_tfidf_map, current_term_frequency, current_word_count);
            }
            this.tfidf_table.put(title, temp_tfidf_map);
            calculate_keywords_table(title, this.tfidf_table);
        }
    }

    /**
     * Calculates the tfidf values for all unique terms in a document
     * @param temp_tfidf_map            Temporary map to store the tfidf values
     * @param current_term_frequency    Hashmap of the term_frequency table of the current document
     * @param current_word_count        Word count of the current document
     */
    private void calculate_document_tfidf(HashMap<String, Float> temp_tfidf_map,
                                          HashMap<String, Float> current_term_frequency,
                                          float current_word_count) {
        for (String token : current_term_frequency.keySet()) {
            float idf;
            int frequency = 1;
            float tf;
            // the term frequency is the amount of times this word appears as a share of all words in this document
            tf = current_term_frequency.get(token) / current_word_count;

            int N = term_frequency_table.size();            // number of documents
            int idx = 0;
            for (String document : term_frequency_table.keySet()) {
                HashMap<String, Float> current_hashmap = term_frequency_table.get(document);
                if (current_hashmap.get(token) != null) {
                    frequency++;
                }
                if (idx == corpus_size) {
                    break;
                }
                idx++;
            }
            // the inverse document frequency is a function on the amount of times the word appears in every other document
            idf = (float) Math.log((float) N / (float) frequency);
            float tfidf = tf * idf;
            temp_tfidf_map.put(token, tfidf);
        }
    }

    /**
     * Takes the top-k keywords for a document and arranges them in an arraylist
     * @param table hashtable with the document titles, and a hashmap with words and their value as a keyword.
     */
    private void calculate_keywords_table(String title, Hashtable<String, HashMap<String, Float>> table) {
        HashMap<String, Float> current = table.get(title);
        ArrayList<String> keywords = new ArrayList<>();
        float max = 0;                                          // keep track of the word with the highest value
        String max_word = "";                                   // word corresponding to the key with the max value
        float max_id = java.lang.Math.min(top, current.size()); // stopping condition for the for loop
        for (int idx = 0; idx < max_id; idx++) {
            for (String token : current.keySet()) {             // only add the keyword if not already present
                if (!keywords.contains(token)) {                // check if word has already been added
                    if (current.get(token) > max) {
                        max = current.get(token);
                        max_word = token;
                    }
                }
            }
            keywords.add(max_word);                             // add the highest value word and reset
            max_word = "";
            max = 0;
        }
        keywords_table.put(title, keywords);
    }

    /**
     * @param   tokenized tokenized String[] with the document text
     * @return  a HashMap<String, Float> of how often each word appears in the text
     */
    private HashMap<String, Float> calculate_term_frequency(String[] tokenized) {
        // Uses Float because the tfidf map also uses floats, and they need to be compatible with the same functions
        HashMap<String, Float> term_frequency = new HashMap<>();
        for (String token : tokenized) {
            if (term_frequency.get(token) != null) {
                term_frequency.put(token, term_frequency.get(token) + 1);
            }
            else {
                term_frequency.put(token, (float)1);
            }
        }
        return term_frequency;
    }

    /**
     * Tokenizes a body of text
     * @param body String with the full, untokenized text
     * @return String[] with the tokenized text
     */
    private String[] tokenize(String body) {
        String[] tokenized = new String[body.length()];
        // First up; tokenize the list with built-in string tokenizer
        if (this.lowercase) {
            body = body.toLowerCase();
        }

        // TODO: find better tokenization method
        StringTokenizer st = new StringTokenizer(body);
        int idx = 0;
        while (st.hasMoreTokens()) {
            tokenized[idx] = st.nextToken();
            idx++;
        }
        return tokenized;
    }

    /**
     * This function takes the list of websites and corresponding keywords, and prints them to "output.txt"
     */
    public void output_to_file() {
        try {
            PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
            System.setOut(out);
            for(String title : keywords_table.keySet()) {
                // prints the title, the keywords and a newline
                String message = title + ": " + keywords_table.get(title);
                out.println(message);
            }
            out.close();
        }
        catch (FileNotFoundException f) {
            // TODO: exception handling
        }
    }
}