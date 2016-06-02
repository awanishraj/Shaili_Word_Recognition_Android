package in.ac.iitm.shaili;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Awanish Raj on 13/05/16.
 */
public class HindiChars {

    public static List<String> VOWELS;
    public static List<String> MODIFIER_VOWELS;
    public static List<String> CONSONANTS;
    public static List<String> CONJUNCT;
    public static List<String> MODIFIER_OTHERS;
    public static List<String> DIGITS;
    public static List<String> PUNCTUATION;

    public static void initialize(String filepath) throws IOException {

        File charFile = new File(filepath);
        BufferedReader reader = new BufferedReader(new FileReader(charFile));

        String line;
        int linenum = 0;
        while ((line = reader.readLine()) != null) {
            if (line.charAt(0) != '#') {
                loadLine(linenum, line);
                linenum++;
            }
        }

    }


    private static void loadLine(int linenum, String line) {
        List<String> arr = new ArrayList<>(Arrays.asList(line.split(" ")));
        switch (linenum) {
            case 0:
                VOWELS = arr;
                break;
            case 1:
                MODIFIER_VOWELS = arr;
                break;
            case 2:
                CONSONANTS = arr;
                break;
            case 3:
                CONJUNCT = arr;
                break;
            case 4:
                MODIFIER_OTHERS = arr;
                break;
            case 5:
                DIGITS = arr;
                break;
            case 6:
                PUNCTUATION = arr;
                break;
        }
    }

    public static List<String> getAllCombinations() {
        List<String> allcombinations = new ArrayList<>();

        /**
         * Adding all vowels
         */
        allcombinations.addAll(VOWELS);
        /**
         * Adding all consonants
         */
        allcombinations.addAll(CONSONANTS);

        /**
         * Adding maatra form of consonants
         */
        for (String consonant : CONSONANTS) {
            for (String matra : MODIFIER_VOWELS) {
                allcombinations.add(consonant + matra);
            }
        }

        /**
         * Adding conjunct consonants
         */
        String conjunct = CONJUNCT.get(0);
        for (String consonant1 : CONSONANTS) {
            for (String consonant2 : CONSONANTS) {
                allcombinations.add(consonant1 + conjunct + consonant2);
                for (String maatra : MODIFIER_VOWELS) {
                    allcombinations.add(consonant1 + conjunct + consonant2 + maatra);
                }
            }
        }

        /**
         * Adding digits
         */
        allcombinations.addAll(DIGITS);

        /**
         * Adding punctuation
         */
        allcombinations.addAll(PUNCTUATION);

        return allcombinations;
    }

}
