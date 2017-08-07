package ImgProcFunctions;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class BlackAndWhite {

    private Bitmap selectedImageBitmap;
    private Bitmap bitmapImageToShow;

    public BlackAndWhite() {
    }

    public void setSelectedImage(Bitmap selectedImageBitmap) {
        this.selectedImageBitmap = selectedImageBitmap;
    }

    public void applyConversion() {

        if (selectedImageBitmap != null) {
            Mat selectedImageMat = new Mat(selectedImageBitmap.getHeight(), selectedImageBitmap.getWidth(), CvType.CV_8UC3);
            Utils.bitmapToMat(selectedImageBitmap, selectedImageMat);
            Imgproc.cvtColor(selectedImageMat, selectedImageMat, Imgproc.COLOR_BGR2GRAY);

            Log.e("Color Conversion", "Converted");
            bitmapImageToShow = Bitmap.createBitmap(selectedImageMat.width(), selectedImageMat.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(selectedImageMat, bitmapImageToShow);
        }
    }

    public Bitmap getBitmapImageToShow() {
        return bitmapImageToShow;
    }
}
