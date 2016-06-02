package in.ac.iitm.shaili;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Awanish Raj on 20/05/16.
 */
public class CoverageCalculator {
    private static final String INPUT_FILE = "hiwiki.txt";
    private static final float COVERAGE = 0.98f;

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(INPUT_FILE));
        String line;

        List<WordEntry> wordsList = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            String[] freq = line.split("\t");
            wordsList.add(new WordEntry(freq[0], Integer.valueOf(freq[1])));
        }

        int wordCount = 0;
        for (WordEntry entry : wordsList) {
            wordCount += entry.count;
        }

        System.out.println("Total word count = " + wordCount);


        BufferedWriter writer = new BufferedWriter(new FileWriter("classes_hiwiki.txt"));

        int accum = 0;
        int index = 0;
        List<WordEntry> tempList = new ArrayList<>();
        for (WordEntry entry : wordsList) {
            index++;
            accum += entry.count;
            tempList.add(entry);
            if (accum * 1.0f / wordCount >= COVERAGE) {
                System.out.print("Number of words: " + index + "/" + wordsList.size());
                break;
            }
        }

        Collections.sort(tempList, new Comparator<WordEntry>() {
            @Override
            public int compare(WordEntry o1, WordEntry o2) {
                return o2.word.length() - o1.word.length();
            }
        });

        for (WordEntry entry : tempList) {
            writer.write(entry.word + "\n");
        }

        /**
         * Writing digits
         */
        for (int i = 0x0964; i <= 0x0096F; i++) {
            writer.write(Character.toString((char) i) + "\n");
        }
        writer.write(",\n");
        writer.write(".\n");
        writer.write("?\n");
        writer.write(":\n");
        writer.write(";\n");
        writer.write("!\n");
        writer.flush();
        writer.close();
    }
}
