package ImgProcFunctions;

import android.graphics.Bitmap;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class Rotate {

    private Bitmap selectedImageBitmap;
    private Bitmap rotatedImageBitmap;

    private ImageView img_view;

    public Rotate(ImageView img_view) {
        this.img_view = img_view;
    }

    public void setSelectedImageBitmap(Bitmap selectedImageBitmap) {
        this.selectedImageBitmap = selectedImageBitmap;
    }

    public void rotateLeft() {
        if (selectedImageBitmap != null) {
            Mat selectedImageMat = new Mat(selectedImageBitmap.getHeight(), selectedImageBitmap.getWidth(), CvType.CV_8UC3);
            Utils.bitmapToMat(selectedImageBitmap, selectedImageMat);

            // Mat rotated = Mat.zeros(selectedImageBitmap.getWidth(), selectedImageBitmap.getHeight(), CvType.CV_8UC3);
            Mat rotated = new Mat();
            Mat M = Imgproc.getRotationMatrix2D(new Point(selectedImageMat.cols()/2, selectedImageMat.rows()/2), 90, 1.0);
            Imgproc.warpAffine(selectedImageMat, rotated, M, new Size(selectedImageMat.width(), selectedImageMat.height()));

            rotatedImageBitmap = Bitmap.createBitmap(rotated.width(), rotated.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(rotated, rotatedImageBitmap);

            img_view.setImageBitmap(rotatedImageBitmap);
        }
    }

    public void rotateRight() {
        if (selectedImageBitmap != null) {
            Mat selectedImageMat = new Mat(selectedImageBitmap.getHeight(), selectedImageBitmap.getWidth(), CvType.CV_8UC3);
            Utils.bitmapToMat(selectedImageBitmap, selectedImageMat);

            // Mat rotated = Mat.zeros(selectedImageBitmap.getWidth(), selectedImageBitmap.getHeight(), CvType.CV_8UC3);
            Mat rotated = new Mat();
            Mat M = Imgproc.getRotationMatrix2D(new Point(selectedImageMat.cols()/2, selectedImageMat.rows()/2), 270, 1.0);
            Imgproc.warpAffine(selectedImageMat, rotated, M, new Size(selectedImageMat.width(), selectedImageMat.height()));

            rotatedImageBitmap = Bitmap.createBitmap(rotated.width(), rotated.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(rotated, rotatedImageBitmap);

            img_view.setImageBitmap(rotatedImageBitmap);
        }
    }
}
