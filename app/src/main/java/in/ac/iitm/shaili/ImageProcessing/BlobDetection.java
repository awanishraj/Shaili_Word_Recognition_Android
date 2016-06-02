package in.ac.iitm.shaili.ImageProcessing;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import in.ac.iitm.shaili.Helpers.BitmapWriter;

/**
 * Created by Awanish Raj on 09/05/16.
 */
public class BlobDetection {

    private static final String LOG_TAG = "BlobDetection";

    public static Bitmap drawBoundingBoxes(Bitmap output, List<BlobBox> boxes) {
        int width = output.getWidth();
        int height = output.getHeight();

        for (BlobDetection.BlobBox box : boxes) {
            for (int i = box.minX; i <= box.maxX; i++) {
                if (box.minY > 0)
                    output.setPixel(i, box.minY - 1, Color.GREEN);
                if (box.maxY < height - 1)
                    output.setPixel(i, box.maxY + 1, Color.GREEN);
            }
            for (int i = box.minY; i <= box.maxY; i++) {
                if (box.minX > 0)
                    output.setPixel(box.minX - 1, i, Color.GREEN);
                if (box.maxX < width - 1)
                    output.setPixel(box.maxX + 1, i, Color.GREEN);
            }
        }
        return output;
    }

    public static ArrayList<String> clipBlobsFromBitmap(Bitmap image, LocalizationListener mListener) {
        ArrayList<String> filePaths = new ArrayList<>();
        List<BlobBox> boxes = extractMono(image, mListener);

        for (BlobBox box : boxes) {
            Bitmap clip = box.getClippedBitmap(image);
            File file = BitmapWriter.write(clip, "temp/BOX", "" + boxes.indexOf(box));
            filePaths.add(file.getAbsolutePath());
        }

        drawBoundingBoxes(image, boxes);

        return filePaths;
    }

    public static List<BlobBox> extractMono(Bitmap image, LocalizationListener mListener) {

        int[][] labels = new int[image.getWidth()][image.getHeight()];
        int labelCounter = 0;

        List<Label> labelsList = new ArrayList<>();

        int[] neighbouringLabels = new int[4];

        List<Set<Integer>> equivalence = new ArrayList<>();


        /**
         * Looping through the pixels
         */
        final int width = image.getWidth();
        final int height = image.getHeight();


        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {

                labels[i][j] = 0;
                /**
                 * Foreground pixel
                 */
                if (image.getPixel(i, j) == Color.BLACK) {
                    int minLabel = Integer.MAX_VALUE;
//                    int minBlobNumber = Integer.MAX_VALUE;

                    /**
                     * Getting labels for neighbours
                     */
                    neighbouringLabels[0] = (i != 0 && j != 0) ? labels[i - 1][j - 1] : 0;
                    neighbouringLabels[1] = (j != 0) ? labels[i][j - 1] : 0;
                    neighbouringLabels[2] = (i != width - 1 && j != 0) ? labels[i + 1][j - 1] : 0;
                    neighbouringLabels[3] = (i != 0) ? labels[i - 1][j] : 0;

                    for (int label : neighbouringLabels) {
                        if (label != 0) {
                            if (minLabel > label) minLabel = label;
                        }
                    }

                    /**
                     * No neighbour is labelled
                     */
                    if (minLabel == Integer.MAX_VALUE) {

                        /**
                         * Assign label to pixel
                         */
                        labelCounter++;
                        labels[i][j] = labelCounter;

                        /**
                         * Add new label to list
                         */
                        Label newLabel = new Label(labelCounter);
                        newLabel.addPoint(i, j);
                        labelsList.add(newLabel);
                        equivalence.add(new HashSet<Integer>());
                    }
                    /**
                     * Neighbour is labelled
                     */
                    else {
                        labels[i][j] = minLabel;
                        labelsList.get(minLabel - 1).addPoint(i, j);

                        for (int label : neighbouringLabels) {
                            if (label != 0) {
                                int minBlobNumber = labelsList.get(minLabel - 1).blobNumber;
                                int currBlobNumber = labelsList.get(label - 1).blobNumber;

                                if (minBlobNumber != currBlobNumber) {

                                    int min = minBlobNumber;
                                    int max = currBlobNumber;

                                    if (min > max) {
                                        min = currBlobNumber;
                                        max = minBlobNumber;
                                    }
                                    equivalence.get(min).add(max);
                                }
                            }
                        }
                    }
                }

            }
            if (mListener != null) {
                ArrayList<Label> tempLabels = new ArrayList<>(labelsList);
                List<Set<Integer>> tempEq = new ArrayList<>(equivalence);
                for (int i = 1; i < tempEq.size(); i++) {
                    for (int val : tempEq.get(i)) {
                        tempLabels.get(val - 1).blobNumber = tempLabels.get(i - 1).blobNumber;
                    }
                }
                mListener.onBoxesUpdated(getBlobs(tempLabels));
            }

        }

//        for (int i = 0; i < labelsList.size(); i++) {
//            for (int j = i+1; j < labelsList.size(); j++) {
//                Label one = labelsList.get(i);
//                Label two = labelsList.get(j);
//                if (one.blobNumber != two.blobNumber && (one.box.overlaps(two.box) || two.box.overlaps(one.box))) {
//                    Log.e(LOG_TAG, "Box over lap found: " + one.blobNumber + ", " + two.blobNumber);
//                    one.box.printCoords();
//                    two.box.printCoords();
//                    if (one.blobNumber < two.blobNumber)
//                        equivalence.get(one.blobNumber).add(two.blobNumber);
//                    else
//                        equivalence.get(two.blobNumber).add(one.blobNumber);
//                }
//            }
//        }

//        for (int i = 1; i < equivalence.size(); i++) {
//
//            for (int val : equivalence.get(i)) {
//                labelsList.get(val - 1).blobNumber = labelsList.get(i - 1).blobNumber;
//            }
//        }


        for (int i = 1; i < equivalence.size(); i++) {

            for (int val : equivalence.get(i)) {
                labelsList.get(val - 1).blobNumber = labelsList.get(i - 1).blobNumber;
            }
        }

        return getBlobs(labelsList);

    }

    public interface LocalizationListener {
        void onBoxesUpdated(List<BlobBox> boxes);
    }

    private static boolean mergeBlobs(List<BlobBox> boxes) {
        List<BlobBox> merged = new ArrayList<>();

        merged.addAll(boxes);

        List<Set<Integer>> equivalence = new ArrayList<>();

        boolean noOverlap = true;

        for (int i = 0; i < boxes.size(); i++) {
            equivalence.add(new HashSet<Integer>());
            BlobBox one = boxes.get(i);
            for (int j = i + 1; j < boxes.size(); j++) {
                BlobBox two = boxes.get(j);
                if (two.overlaps(one) || one.overlaps(two)) {
                    equivalence.get(i).add(j);
                    noOverlap = false;
                }
            }
        }

        Set<Integer> dirty = new HashSet<>();


        for (int i = equivalence.size() - 1; i >= 0; i--) {
            for (Integer ger : equivalence.get(i)) {
                merged.get(i).assessBox(boxes.get(ger));
                dirty.add(ger);
            }
        }

        boxes.clear();

        for (int i = 0; i < merged.size(); i++) {
            if (!dirty.contains(i)) {
                boxes.add(merged.get(i));
            }
        }


        return noOverlap;
    }


    private static List<BlobBox> getBlobs(List<Label> labels) {
        List<BlobBox> blobs = new ArrayList<>();

        Collections.sort(labels, new Comparator<Label>() {
            @Override
            public int compare(Label lhs, Label rhs) {
                return lhs.blobNumber - rhs.blobNumber;
            }
        });

        int lastBlob = -1;
        for (int i = 0; i < labels.size(); i++) {
            if (labels.get(i).box.area() < 2) {
                continue;
            }
            if (labels.get(i).blobNumber != lastBlob) {
                blobs.add(new BlobBox());
                lastBlob = labels.get(i).blobNumber;
            }
            blobs.get(blobs.size() - 1).assessBox(labels.get(i).box);
        }

//        blobs =
        while (!mergeBlobs(blobs)) ;

        if (blobs.size() == 0) return blobs;

        List<BlobBox> sortedList = new ArrayList<>();
        List<BlobBox> subList = new ArrayList<>();
        int startMaxY = blobs.get(0).maxY;
        for (BlobBox box : blobs) {
            if (box.minY < startMaxY) {
                subList.add(box);
            } else {
                Collections.sort(subList, new Comparator<BlobBox>() {
                    @Override
                    public int compare(BlobBox lhs, BlobBox rhs) {
                        return lhs.minX - rhs.minX;
                    }
                });
                sortedList.addAll(subList);
                subList = new ArrayList<>();
                startMaxY = box.maxY;
                subList.add(box);
            }
        }

        Collections.sort(subList, new Comparator<BlobBox>() {
            @Override
            public int compare(BlobBox lhs, BlobBox rhs) {
                return lhs.minX - rhs.minX;
            }
        });
        sortedList.addAll(subList);

        return sortedList;
    }

    public static class BlobBox {
        public int minX = Integer.MAX_VALUE;
        public int minY = Integer.MAX_VALUE;
        public int maxX = Integer.MIN_VALUE;
        public int maxY = Integer.MIN_VALUE;

        public int mass = 0;

        public void assessPoint(int x, int y) {
            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
            mass++;
        }

        public void assessBox(BlobBox box) {
            if (minX > box.minX) minX = box.minX;
            if (minY > box.minY) minY = box.minY;
            if (maxX < box.maxX) maxX = box.maxX;
            if (maxY < box.maxY) maxY = box.maxY;
            mass += box.mass;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            BlobBox blobBox = (BlobBox) o;

            if (minX != blobBox.minX) return false;
            if (minY != blobBox.minY) return false;
            if (maxX != blobBox.maxX) return false;
            return maxY == blobBox.maxY;

        }

        @Override
        public int hashCode() {
            int result = minX;
            result = 31 * result + minY;
            result = 31 * result + maxX;
            result = 31 * result + maxY;
            return result;
        }

        public Bitmap getClippedBitmap(Bitmap input) {
            return Bitmap.createBitmap(input, minX, minY, maxX - minX + 1, maxY - minY + 1);
        }

        @Override
        public String toString() {
            return "Width = " + (maxX - minX + 1) + ", Height = " + (maxY - minY + 1) + ", Mass = " + mass;
        }

        public void printCoords() {
            Log.e(LOG_TAG, "Box: (" + minX + ", " + minY + "), (" + minX + ", " + maxY + "), (" + maxX + ", " + minY + "), (" + maxX + ", " + maxY + ")");
        }

        public int getHeight() {
            return maxY - minY;
        }

        public boolean overlaps(BlobBox box) {
            // Box coords (minX,minY) (minX, maxY) (maxX, minY) (maxX, maxY)
            int yBuffer = 4;
            int xBuffer = 3;
            return (
                    (box.minX - xBuffer <= minX && minX <= box.maxX + xBuffer && box.minY - yBuffer <= minY && minY <= box.maxY + yBuffer)
                            || (box.minX - xBuffer <= minX && minX <= box.maxX + xBuffer && box.minY - yBuffer <= maxY && maxY <= box.maxY + yBuffer)
                            || (box.minX - xBuffer <= maxX && maxX <= box.maxX + xBuffer && box.minY - yBuffer <= minY && minY <= box.maxY + yBuffer)
                            || (box.minX - xBuffer <= maxX && maxX <= box.maxX + xBuffer && box.minY - yBuffer <= maxY && maxY <= box.maxY + yBuffer));

        }


        public int area() {
            return (maxX - minX) * (maxY - minY);
        }
    }

    private static class Label {
        int blobNumber;
        BlobBox box = new BlobBox();

        public Label(int blobNumber) {
            this.blobNumber = blobNumber;
        }

        public void addPoint(int x, int y) {
            box.assessPoint(x, y);
        }

        @Override
        public String toString() {
            return "Label{" +
                    "blobNumber=" + blobNumber +
                    '}';
        }
    }


}
