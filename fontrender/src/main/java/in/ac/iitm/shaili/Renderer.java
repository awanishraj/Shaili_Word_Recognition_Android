package in.ac.iitm.shaili;

import com.objectplanet.image.PngEncoder;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Awanish Raj on 13/05/16.
 */
public class Renderer {

    private Font font;
    private FontMetrics fm;

    public Renderer(String fontPath) {
        if (fontPath != null)
            initFontMetrics(fontPath);
    }

    public void loadFont(String fontpath) {
        initFontMetrics(fontpath);
    }

    private void initFontMetrics(String fontPath) {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath)).deriveFont(48f);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        g2d.dispose();
    }
public BufferedImage getImageForText(String text) {
        int width = fm.stringWidth(text) + 10;
        int height = (int) (fm.getHeight() * 2f);
        if (width * height == 0) return null;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = img.createGraphics();

        applyProperties(g2d);

        g2d.drawString(text, 5, fm.getAscent() + fm.getHeight() / 2);

        g2d.dispose();

        return img;
    }

    public BufferedImage rotateImage(BufferedImage image, double angleDeg) {
        AffineTransform transform = new AffineTransform();
        transform.rotate(Math.toRadians(angleDeg), image.getWidth() / 2, image.getHeight() / 2);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
//        return fillBackground(op.filter(image, null));
        return trimImage(fillBackground(op.filter(image, null)));
    }

    public void saveImageToFile(BufferedImage image, String filename) {
        image = fillBackground(image);
        image = trimImage(image);
//        image = getScaledImage(image, 100, 50);
//        image = (BufferedImage) image.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
//        image = Thumbnails.of(img).forceSize(newW, newH).asBufferedImage();
        try {
            File file = new File(filename + ".png");
            file.getParentFile().mkdirs();


            //Older alternative
//            ImageIO.write(image, "png", new File(filename + ".png"));

            //Newer alternative
            BufferedOutputStream imageOutputStream = new BufferedOutputStream(new FileOutputStream(filename + ".png"));
            new PngEncoder().encode(image, imageOutputStream);
            imageOutputStream.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private BufferedImage getScaledImage(BufferedImage src, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TRANSLUCENT);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(src, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
    }

    public void saveTextToImage(String text, String filename) {
        try {
            BufferedImage image = getImageForText(text);
            File file = new File(filename);
            if (file.getParentFile() != null)
                file.getParentFile().mkdirs();

            BufferedOutputStream imageOutputStream = new BufferedOutputStream(new FileOutputStream(filename));
            new PngEncoder().encode(image, imageOutputStream);
            imageOutputStream.close();
//            ImageIO.write(image, "png", new File(filename));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static final int transparent = new Color(0, 0, 0, 0).getRGB();

    private BufferedImage fillBackground(BufferedImage image) {
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                if (image.getRGB(i, j) == transparent)
                    image.setRGB(i, j, Color.WHITE.getRGB());
            }
        }
        return image;
    }

    private BufferedImage trimImage(BufferedImage image) {
        int startY = 0;
        int endY = image.getHeight()-1;
        int startX = 0;
        int endX = image.getWidth()-1;


        for (int j = 0; j < image.getHeight(); j++) {
            boolean empty = true;
            for (int i = 0; i < image.getWidth(); i++) {
                if (image.getRGB(i, j) != Color.WHITE.getRGB()) {
                    empty = false;
                    break;
                }
            }
            if (!empty) {
                startY = j;
                break;

            }
        }

        for (int j = image.getHeight() - 1; j >= 0; j--) {
            boolean empty = true;
            for (int i = 0; i < image.getWidth(); i++) {
                if (image.getRGB(i, j) != Color.WHITE.getRGB()) {
                    empty = false;
                    break;
                }
            }
            if (!empty) {
                endY = j;
                break;
            }
        }

        for (int i = 0; i < image.getWidth(); i++) {
            boolean empty = true;
            for (int j = 0; j < image.getHeight(); j++) {
                if (image.getRGB(i, j) != Color.WHITE.getRGB()) {
                    empty = false;
                    break;
                }
            }
            if (!empty) {
                startX = i;
                break;
            }
        }

        for (int i = image.getWidth() - 1; i >= 0; i--) {
            boolean empty = true;
            for (int j = 0; j < image.getHeight(); j++) {
                if (image.getRGB(i, j) != Color.WHITE.getRGB()) {
                    empty = false;
                    break;
                }
            }
            if (!empty) {
                endX = i;
                break;
            }
        }

        return image.getSubimage(startX, startY, endX - startX + 1, endY - startY + 1);
    }

    private void applyProperties(Graphics2D g2d) {
//        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
//        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
//        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
//        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
//        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);
        g2d.setColor(Color.BLACK);
        g2d.setBackground(Color.WHITE);
    }
}
