package in.ac.iitm.shaili;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Awanish Raj on 13/05/16.
 */
public class WordGenerator {

    private static final String[] hindiFonts = new String[]{
            "mangal.ttf",
            "ITFDevanagari.ttc",
            "NotoSansDevanagari-Regular.ttf",
            "Kohinoor.ttc"
    };

    private static final String[] tamilFonts = new String[]{
//            "Tamil MN.ttc",
//            "Tamil Sangam MN.ttc",
//            "droidtamil.ttf"
            "NotoSansTamil-Regular.ttf"
//            "AVVAIYAR.TTF",
//            "ETLAKSHM.TTF"
    };

    private static final String[] fontFiles = hindiFonts;

    private static final String SOURCE_FILE = "classes_wiki.txt";
    private static final String PATH_CAFFE = "../..";
    private static final String LMDB_TRAIN_PATH = "files_model/corpus_train";
    private static final String LMDB_TEST_PATH = "files_model/corpus_test";

    private static final int[] ROTATIONS = {0};//{-4, -2, 0, 2, 4};
    private static final int NUM_SALT_PEPPER = 0;
    private static BufferedWriter writer;

    public static void main(String[] args) throws IOException {
        System.out.println("Word Generator");

        List<String> characters = getWords(SOURCE_FILE);
        System.out.println("Generator word set: " + characters.size());

        File tempList = new File("list_tmp.txt");
        writer = new BufferedWriter(new FileWriter(tempList));

        Renderer renderer = new Renderer(null);
        /**
         * Rendering for each font
         */
        for (int j = 0; j < fontFiles.length; j++) {
            System.out.println("\nGenerating for font file : " + fontFiles[j]);
            renderer.loadFont(fontFiles[j]);
            /**
             * Looping through all words
             */
            for (int classNo = 0; classNo < 20 && classNo < characters.size(); classNo++) {
//                if (classNo == 24) continue;
//                if (classNo == 86) continue;
//                if (classNo == 160) continue;
//                if (classNo == 168) continue;
                System.out.print("\rGenerating - " + (classNo + 1) + "/" + characters.size() + " - " + characters.get(classNo));

                BufferedImage image = renderer.getImageForText(characters.get(classNo));
                /**
                 * Generating image for each rotation
                 */
                for (int rotation : ROTATIONS) {
                    BufferedImage rotImage = renderer.rotateImage(image, rotation);
                    String filepath = "wordset/WORD_" + classNo + "_" + j + "_" + rotation + "_0";
                    renderer.saveImageToFile(rotImage, filepath);
                    addFileToClass(filepath, classNo);
                    /**
                     * Adding salt and pepper
                     */
                    for (int sp = 0; sp < NUM_SALT_PEPPER; sp++) {
                        BufferedImage spImage = SaltAndPepper.applySaltAndPepper(rotImage, 0.002 * (sp + 1));
                        filepath = "wordset/WORD_" + classNo + "_" + j + "_" + rotation + "_" + (sp + 1);
                        renderer.saveImageToFile(spImage, filepath);
                        addFileToClass(filepath, classNo);
                    }
                }

            }
        }
        writer.flush();
        writer.close();

        /**
         * Generating LMDB
         */
//        runLMDB();

    }


    private static void runLMDB() throws IOException {
        String[] commands = {
                PATH_CAFFE + "/build/tools/convert_imageset --gray --backend=lmdb --resize_height=50 --resize_width=50 --shuffle ./ list_tmp.txt " + LMDB_TRAIN_PATH,
                PATH_CAFFE + "/build/tools/convert_imageset --gray --backend=lmdb --resize_height=50 --resize_width=50 --shuffle ./ list_tmp.txt " + LMDB_TEST_PATH
        };
        for (String command : commands) {
            ProcessBuilder pb1 = new ProcessBuilder(command.split(" "));
            pb1.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            pb1.redirectError(ProcessBuilder.Redirect.INHERIT);
            pb1.start();
        }
    }

    private static void addFileToClass(String filepath, int classNo) throws IOException {
        writer.write(filepath + ".png" + " " + classNo + "\n");
    }

    private static List<String> getWords(String filepath) throws IOException {
        List<String> words = new ArrayList<>();
        File charFile = new File(filepath);
        BufferedReader reader = new BufferedReader(new FileReader(charFile));
        String line;
        while ((line = reader.readLine()) != null) {
            words.add(line);
        }
        return words;
    }
}
