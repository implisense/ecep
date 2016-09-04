package com.implisense.ecep.api.sandbox;

import com.google.common.io.Files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by hkorte on 04.09.16.
 */
public class DummyContentGenerator {
    public static void main(String[] args) throws IOException {
        String[] terms = {"open", "data", "is", "the", "idea", "that", "some", "data", "should", "be", "freely",
                "available", "to", "everyone", "to", "use", "and", "republish", "as", "they", "wish", "without",
                "restrictions", "from", "copyright", "patents", "or", "other", "mechanisms", "of", "control", "the",
                "goals", "of", "the", "open", "data", "movement", "are", "similar", "to", "those", "of", "other",
                "open", "movements", "such", "as", "open", "source", "open", "hardware", "open", "content", "and",
                "open", "access", "the", "philosophy", "behind", "open", "data", "has", "been", "long", "established",
                "for", "example", "in", "the", "mertonian", "tradition", "of", "science", "but", "the", "term",
                "open", "data", "itself", "is", "recent", "gaining", "popularity", "with", "the", "rise", "of",
                "the", "internet", "and", "world", "wide", "web", "and", "especially", "with", "the", "launch",
                "of", "open", "data", "government", "initiatives", "such", "as", "data", "gov", "and", "data",
                "gov", "uk"};
        Random random = new Random(42L);
        final int MAX_NUM_ROWS = 10000;
        File inFile = new File("existing-ids-10k.txt");
        File outFile = new File("fake-content-10k.csv");
        BufferedWriter writer = Files.newWriter(outFile, UTF_8);
        int i = 0;
        for (String id : Files.readLines(inFile, UTF_8)) {
            writer.write(id);
            writer.write("\t");
            writer.write(terms[random.nextInt(terms.length)]);
            int numWords = 20 + random.nextInt(80);
            for (int j = 0; j < numWords; j++) {
                writer.write(" ");
                writer.write(terms[random.nextInt(terms.length)]);
            }
            writer.write("\n");
            i++;
            if(i == MAX_NUM_ROWS) {
                break;
            }
        }
        writer.close();
    }
}
