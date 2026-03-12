import java.util.*;

public class PlagiarismDetector {

    // n-gram -> set of document IDs
    private HashMap<String, Set<String>> ngramIndex;

    // document -> set of ngrams
    private HashMap<String, Set<String>> documentNgrams;

    private int N = 5; // using 5-grams

    public PlagiarismDetector() {
        ngramIndex = new HashMap<>();
        documentNgrams = new HashMap<>();
    }

    // Extract n-grams from text
    private Set<String> generateNgrams(String text) {

        String[] words = text.toLowerCase().split("\\s+");
        Set<String> ngrams = new HashSet<>();

        for (int i = 0; i <= words.length - N; i++) {

            StringBuilder gram = new StringBuilder();

            for (int j = 0; j < N; j++) {
                gram.append(words[i + j]).append(" ");
            }

            ngrams.add(gram.toString().trim());
        }

        return ngrams;
    }

    // Add document to database
    public void addDocument(String documentId, String text) {

        Set<String> ngrams = generateNgrams(text);

        documentNgrams.put(documentId, ngrams);

        for (String gram : ngrams) {

            ngramIndex.putIfAbsent(gram, new HashSet<>());

            ngramIndex.get(gram).add(documentId);
        }

        System.out.println(documentId + " indexed with " + ngrams.size() + " n-grams.");
    }

    // Analyze a new document
    public void analyzeDocument(String documentId, String text) {

        Set<String> newDocNgrams = generateNgrams(text);

        System.out.println("\nAnalyzing " + documentId);
        System.out.println("Extracted " + newDocNgrams.size() + " n-grams");

        HashMap<String, Integer> matchCounts = new HashMap<>();

        for (String gram : newDocNgrams) {

            if (ngramIndex.containsKey(gram)) {

                for (String doc : ngramIndex.get(gram)) {

                    matchCounts.put(doc, matchCounts.getOrDefault(doc, 0) + 1);
                }
            }
        }

        for (Map.Entry<String, Integer> entry : matchCounts.entrySet()) {

            String doc = entry.getKey();
            int matches = entry.getValue();

            double similarity = (matches * 100.0) / newDocNgrams.size();

            System.out.println("Found " + matches + " matching n-grams with "
                    + doc + " → Similarity: " + String.format("%.2f", similarity) + "%");

            if (similarity > 60) {
                System.out.println("⚠ PLAGIARISM DETECTED with " + doc);
            }
        }
    }

    public static void main(String[] args) {

        PlagiarismDetector detector = new PlagiarismDetector();

        String essay1 = "machine learning is a field of artificial intelligence that focuses on data analysis";
        String essay2 = "machine learning is a branch of artificial intelligence focused on analyzing data";
        String essay3 = "the history of ancient rome is an important part of world civilization";

        detector.addDocument("essay_089.txt", essay1);
        detector.addDocument("essay_092.txt", essay2);

        detector.analyzeDocument("essay_123.txt", essay1);
    }
}

