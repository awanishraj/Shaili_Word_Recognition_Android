package in.ac.iitm.shaili;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Generator {

    private static final String SOURCE_FILE = "hindi_corpus.txt";
    private static final String FONT_PATH = "mangal.ttf";
    private static final String PATH_CAFFE = "../..";
    private static final String LMDB_TRAIN_PATH = "files_model/fruits_db_train";
    private static final String LMDB_TEST_PATH = "files_model/fruits_db_test";
    private static final int[] ROTATIONS = new int[]{-4, -2, 0, 2, 4};


    public static void main(String args[]) {
        initFontMetrics();

        File tempList = new File("list_tmp.txt");
        File listfile = new File(SOURCE_FILE);

        BufferedWriter writer = null;
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(listfile));
            writer = new BufferedWriter(new FileWriter(tempList));
            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
                // process the line.
                for (int rotation : ROTATIONS) {
                    saveTextToImage(line, "renders/OUT_" + i + "_" + rotation + ".png", rotation);
                    writer.write("renders/OUT_" + i + "_" + rotation + ".png " + i + "\n");
                }
                System.out.println("Rendered - " + i + " - " + line);
                i++;
                if (i > 50) {
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            try {
                assert writer != null;
                writer.flush();
                writer.close();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        runLMDB();

    }

    private static void runLMDB() {
        Runtime rt = Runtime.getRuntime();
        try {
            Process pr = rt.exec(PATH_CAFFE + "/build/tools/convert_imageset --backend=lmdb --resize_height=50 --resize_width=50 --shuffle ./ list_tmp.txt " + LMDB_TRAIN_PATH);
            pr = rt.exec(PATH_CAFFE + "/build/tools/convert_imageset --backend=lmdb --resize_height=50 --resize_width=50 --shuffle ./ list_tmp.txt " + LMDB_TEST_PATH);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void saveTextToImage(String text, String filename, int rotation) {
        int width = fm.stringWidth(text) + 10;
        int height = fm.getHeight();
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        applyProperties(g2d);
//        g2d.clearRect(0, 0, width, height);
        g2d.drawString(text, 5, fm.getAscent());
        try {
            File file = new File(filename);
            file.getParentFile().mkdirs();
            img = rotateImage(img, rotation);
            img = fillBackground(img);
            img = trimImage(img);
            ImageIO.write(img, "png", new File(filename));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        g2d.dispose();
    }

    private static BufferedImage rotateImage(BufferedImage image, double angleDeg) {
        AffineTransform transform = new AffineTransform();
        transform.rotate(Math.toRadians(angleDeg), image.getWidth() / 2, image.getHeight() / 2);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        return op.filter(image, null);
    }

//    public static BufferedImage rotateImage(BufferedImage image, double degreesAngle) {
//        int w = image.getWidth();
//        int h = image.getHeight();
//        BufferedImage result = new BufferedImage(w, h, image.getType());
//        Graphics2D g2 = result.createGraphics();
//        g2.setColor(Color.WHITE);
//        g2.fillRect(0, 0, w, h);
//        g2.rotate(Math.toRadians(degreesAngle), w/2, h/2);
//        g2.drawImage(image,null,0,0);
//        return result;
//    }

    private static BufferedImage trimImage(BufferedImage image) {
        int startY = -1;
        int endY = -1;
        int startX = -1;
        int endX = -1;


        for (int j = 0; j < image.getHeight(); j++) {
            boolean empty = true;
            for (int i = 0; i < image.getWidth(); i++) {
                if (image.getRGB(i, j) != Color.WHITE.getRGB()) {
                    empty = false;
                    break;
                }
            }
            if (!empty) {
                if (startY == -1) {
                    startY = j;
                }

            } else {
                if (startY != -1) {
                    endY = j;
                    break;
                }
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
                if (startX == -1) {
                    startX = i;
                }

            } else {
                if (startX != -1) {
                    endX = i;
                    break;
                }
            }
        }


        if (startY == -1) startY = 0;
        if (endY == -1) endY = image.getHeight();
        if (startX == -1) startX = 0;
        if (endX == -1) endX = image.getWidth();
        return image.getSubimage(startX, startY, endX - startX, endY - startY);
    }

    private static Color Transparent = new Color(0, 0, 0, 0);

    private static BufferedImage fillBackground(BufferedImage image) {
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                if (image.getRGB(i, j) == Transparent.getRGB())
                    image.setRGB(i, j, Color.WHITE.getRGB());
            }
        }
        return image;
    }

    private static FontMetrics fm = null;
    private static Font font = null;

    private static void initFontMetrics() {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
//        Font font = new Font("Kohinoor Devanagari", Font.PLAIN, 48);
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File(FONT_PATH)).deriveFont(48f);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        g2d.dispose();
    }

    private static void applyProperties(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);
        g2d.setColor(Color.BLACK);
        g2d.setBackground(Color.WHITE);
    }

}
