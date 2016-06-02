package in.ac.iitm.shaili;

import java.io.IOException;
import java.util.List;

/**
 * Created by Awanish Raj on 13/05/16.
 */
public class CharacterGenerator {

    public static void main(String[] args) throws IOException {
        System.out.println("Character Generator");

        Renderer renderer = new Renderer("mangal.ttf");

        HindiChars.initialize("hindi_chars.txt");

        List<String> characters = HindiChars.getAllCombinations();
        System.out.println("Generator word set: " + characters.size());

        for (int i = 0; i < characters.size(); i++) {
            renderer.saveTextToImage(characters.get(i), "charset/CHAR_" + i + ".png");
        }


    }
}
