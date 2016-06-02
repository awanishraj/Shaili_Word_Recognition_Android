package in.ac.iitm.shaili.ImageProcessing;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicConvolve3x3;

//import android.renderscript.Allocation;
//import android.renderscript.Element;
//import android.renderscript.RenderScript;
//import android.renderscript.ScriptIntrinsicConvolve3x3;
//import android.renderscript.Element;

/**
 * Created by Ahammad on 27/05/15.
 */
public class ImageSharpener {

    final static int KERNAL_WIDTH = 3;
    final static int KERNAL_HEIGHT = 3;


    public static Bitmap sharpenBitmap(Bitmap bitmap, float[] coefficients, Context context) {


        Bitmap result = Bitmap.createBitmap(
                bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());

        RenderScript renderScript = RenderScript.create(context);

        Allocation input = Allocation.createFromBitmap(renderScript, bitmap);
        Allocation output = Allocation.createFromBitmap(renderScript, result);

        ScriptIntrinsicConvolve3x3 convolution = ScriptIntrinsicConvolve3x3
                .create(renderScript, Element.U8_4(renderScript));
        convolution.setInput(input);
        convolution.setCoefficients(coefficients);
        convolution.forEach(output);

        output.copyTo(result);
        renderScript.destroy();
        return result;
    }


}
