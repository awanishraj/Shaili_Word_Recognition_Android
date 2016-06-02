package in.ac.iitm.shaili;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Awanish Raj on 19/05/16.
 */
public class ParagraphGenerator {

    public static final String INPUT_PATH = "hindi_para.txt";

    public static void main(String[] args) throws IOException {

        Renderer renderer = new Renderer("mangal.ttf");

        BufferedReader reader = new BufferedReader(new FileReader(new File(INPUT_PATH)));
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            builder.append(line).append("\n");
        }

        renderer.saveTextToImage(builder.toString(), "para_out.png");
        System.out.print(builder.toString());
    }
}
