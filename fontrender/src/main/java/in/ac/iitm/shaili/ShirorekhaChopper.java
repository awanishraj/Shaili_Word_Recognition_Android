package in.ac.iitm.shaili;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Created by Awanish Raj on 13/05/16.
 */
public class ShirorekhaChopper {

    public static void main(String[] args) throws IOException {
        System.out.println("Shirorekha Chopper");

        File wordsFolder = new File("wordset/");
        for (File wordFile : wordsFolder.listFiles()) {
            if (!wordFile.getName().contains("MOD") && wordFile.getName().contains(".png")) {
                System.out.println("File: " + wordFile.getName());
                BufferedImage wordImage = ImageIO.read(wordFile);
                wordImage = chopShirorekha(wordImage);
                ImageIO.write(wordImage, "png", new File(wordsFolder.getName() + "/hist/" + wordFile.getName().replace(".", "_MOD.")));
            }
        }
    }

    private static BufferedImage chopShirorekha(BufferedImage wordImage) {

        int width = wordImage.getWidth();
        int height = wordImage.getHeight();

        int[] histX = new int[width];
        int[] histY = new int[height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (wordImage.getRGB(i, j) != Color.white.getRGB()) {
                    histX[i]++;
                    histY[j]++;
                }
            }
        }


        /**
         * Identifying Shirorekha position
         */
//        int max = Integer.MIN_VALUE;
//        for (int j = height / 2; j >= 0; j--) {
//            if (max < histY[j]) max = histY[j];
//
//            if (histY[j] > 0.8f * width) {
//                for (int i = 0; i < width; i++) {
////                    Color mix = mixColorsWithAlpha(new Color(wordImage.getRGB(i, j)), Color.red, 100);
//                    wordImage.setRGB(i, j, Color.white.getRGB());
//                }
//            }
//        }

        /**
         * Drawing Vertical projection
         */
        for (int i = 0; i < wordImage.getWidth(); i++)
            for (int j = height - histX[i]; j < height; j++) {
                Color mix = mixColorsWithAlpha(new Color(wordImage.getRGB(i, j)), Color.green, 100);
                wordImage.setRGB(i, j, mix.getRGB());
            }

        /**
         * Drawing Horizontal projection
         */
        for (int j = 0; j < height; j++)
            for (int i = width - histY[j]; i < width; i++) {
                Color mix = mixColorsWithAlpha(new Color(wordImage.getRGB(i, j)), Color.blue, 100);
                wordImage.setRGB(i, j, mix.getRGB());
            }

//        for (int i = 0; i < wordImage.getWidth(); i++) {
//            if (histX[i] <= min) {
//                for (int j = 0; j < wordImage.getHeight(); j++)
//                    wordImage.setRGB(i, j, Color.GREEN.getRGB());
//            }
//        }

        return wordImage;
    }

    public static Color mixColorsWithAlpha(Color color1, Color color2, int alpha) {
        float factor = alpha / 255f;
        int red = (int) (color1.getRed() * (1 - factor) + color2.getRed() * factor);
        int green = (int) (color1.getGreen() * (1 - factor) + color2.getGreen() * factor);
        int blue = (int) (color1.getBlue() * (1 - factor) + color2.getBlue() * factor);
        return new Color(red, green, blue);
    }

}
