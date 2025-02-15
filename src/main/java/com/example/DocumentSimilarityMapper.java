package com.example;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.*;

public class DocumentSimilarityMapper extends Mapper<Object, Text, Text, Text> {

    @Override
    protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        String[] elements = value.toString().trim().split(" ", 2);
        
        if (elements.length < 2) {
            return; // Skip lines without content
        }

        String docID = elements[0].trim();
        String content = elements[1].trim();

        Set<String> wordSet = new TreeSet<>(Arrays.asList(content.toLowerCase().split("\\s+")));

        context.write(new Text(docID), new Text(String.join(";", wordSet))); // Changed delimiter for uniqueness
    }
}
