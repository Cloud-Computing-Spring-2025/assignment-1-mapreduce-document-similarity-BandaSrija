package com.example;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.*;

public class DocumentSimilarityReducer extends Reducer<Text, Text, Text, Text> {
    private final Map<String, Set<String>> docWordMap = new HashMap<>();

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        for (Text value : values) {
            Set<String> wordSet = new HashSet<>(Arrays.asList(value.toString().split(";")));
            docWordMap.put(key.toString(), wordSet);
        }
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        List<String> docList = new ArrayList<>(docWordMap.keySet());

        for (int i = 0; i < docList.size(); i++) {
            for (int j = i + 1; j < docList.size(); j++) {
                String docA = docList.get(i);
                String docB = docList.get(j);

                Set<String> wordsA = docWordMap.get(docA);
                Set<String> wordsB = docWordMap.get(docB);

                double similarityScore = calculateJaccard(wordsA, wordsB);

                // Ensuring correct order for output
                String orderedPair = (docA.compareTo(docB) < 0) ? (docA + ", " + docB) : (docB + ", " + docA);
                context.write(new Text(orderedPair), new Text("Similarity: " + String.format("%.2f", similarityScore)));
            }
        }
    }

    private double calculateJaccard(Set<String> set1, Set<String> set2) {
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        return (double) intersection.size() / union.size();
    }
}
