package in.ac.iitm.shaili;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Created by Awanish Raj on 14/05/16.
 */
public class SaltAndPepper {

    public static BufferedImage applySaltAndPepper(BufferedImage input, double amount) {
        BufferedImage image = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_ARGB);
        double low = amount / 2;
        double high = 1 - low;
        double tmp;
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                if ((tmp = Math.random()) <= low) {
                    image.setRGB(i, j, Color.black.getRGB());
                } else if (tmp >= high) {
                    image.setRGB(i, j, Color.white.getRGB());
                } else {
                    image.setRGB(i,j,input.getRGB(i,j));
                }
            }
        }

        return image;

    }
}