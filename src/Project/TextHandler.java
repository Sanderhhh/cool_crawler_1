package Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class TextHandler {
    public TextHandler(String body)
    {
        preprocess(body);
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

    private void preprocess(String body)
    {
        ArrayList<String> tokenized = tokenize(body);
        // turn tokenized list into term frequency list
        HashMap<String, Integer> term_frequency = hash(tokenized);
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
        System.out.println(term_frequency);
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

    private void  tf_idf()
    {}
}