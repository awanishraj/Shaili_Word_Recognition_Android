package in.ac.iitm.shaili.Helpers;


/**
 * Created by Ahammad on 28/05/15.
 */
public class ConvoMatrices {

    //Convolution matrix for sharpening image
    private static final float[] BASE_MATRIX = {
            0, -1, 0,
            -1, 5, -1,
            0, -1, 0
    };

    public static float[] getConvolutionMatrix(int k) {
        float[] matrix = BASE_MATRIX;
        if (k == 4) {
            return matrix;
        } else {
            float outerNumber = (-1.0f) / (k - 4);
            float centerNumber = (1.0f * k) / (k - 4);
            matrix[1] = outerNumber;
            matrix[3] = outerNumber;
            matrix[5] = outerNumber;
            matrix[7] = outerNumber;
            matrix[4] = centerNumber;
            return matrix;
        }

    }
}
