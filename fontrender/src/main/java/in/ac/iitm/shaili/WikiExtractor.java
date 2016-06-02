package in.ac.iitm.shaili;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Awanish Raj on 19/05/16.
 */
public class WikiExtractor {

    private static final String DIRECTORY = "output";

    private static List<WordEntry> wordsList = new ArrayList<>();
    private static Map<String, Integer> hashmap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ArrayList<File> files = new ArrayList<>();
        listf(DIRECTORY, files);
        int i = 0;
        for (File file : files) {
            i++;
//            if (i > 10)
//                break;
            System.out.println("Processing file: " + i + "/" + files.size());

            BufferedReader reader = getFileReader(file);
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("<"))
                    continue;
//                String[] words = line.split("[\\p{Punct}\\s\u0964-\u096F0-9a-zA-Z\u00A0\u00B0\u2022\u2013]+");
                String[] words = line.split("[\\p{Punct}\\s0-9a-zA-Z\u00A0\u00B0\u2022\u2013]+");

                for (String word : words) {
                    word = word.trim();
                    if (word.length() == 0) {
                        continue;
                    }
                    if (!hashmap.containsKey(word)) {
                        hashmap.put(word, 1);
                    } else {
                        hashmap.put(word, hashmap.get(word) + 1);
                    }
                }

            }
        }

        for (String entry : hashmap.keySet()) {
            wordsList.add(new WordEntry(entry, hashmap.get(entry)));
        }

        Collections.sort(wordsList, new Comparator<WordEntry>() {
            @Override
            public int compare(WordEntry o1, WordEntry o2) {
                return o2.count - o1.count;
            }
        });

        BufferedWriter writer = new BufferedWriter(new FileWriter("tewiki_freq.txt"));

        for (WordEntry entry : wordsList) {
            writer.write(entry.word + "\t" + entry.count + "\n");
        }
        writer.flush();

    }

    public static BufferedReader getFileReader(File file) throws FileNotFoundException {
        return new BufferedReader(new FileReader(file));
    }


    public static void listf(String directoryName, ArrayList<File> files) {
        File directory = new File(directoryName);

        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile() && !file.getName().contains("DS_Store")) {
                files.add(file);
            } else if (file.isDirectory()) {
                listf(file.getAbsolutePath(), files);
            }
        }
    }
}
