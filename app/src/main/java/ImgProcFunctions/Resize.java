package ImgProcFunctions;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class Resize {

    private int width;
    private int height;

    private Context mainActivityContext;

    private Bitmap selectedImageBitmap;

    private ImageView img_view;

    public Resize(Context mainActivityContext) {
        this.mainActivityContext = mainActivityContext;
    }

    public void setSelectedImage(Bitmap selectedImageBitmap, ImageView img_view) {
        this.selectedImageBitmap = selectedImageBitmap;
        this.img_view = img_view;
    }

    public void setWidthAndHeight() {

        LinearLayout layout = new LinearLayout(mainActivityContext);
        layout.setOrientation(LinearLayout.VERTICAL);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mainActivityContext);
        alertDialog.setTitle("Resize");

        final EditText widthInput = new EditText(mainActivityContext);
        widthInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        widthInput.setHint("Width");
        layout.addView(widthInput);

        final EditText heightInput = new EditText(mainActivityContext);
        heightInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        heightInput.setHint("Height");
        layout.addView(heightInput);

        alertDialog.setView(layout);

        alertDialog.setPositiveButton("Apply", (dialogInterface, i) -> {
            setWidth(Integer.valueOf(widthInput.getText().toString()));
            setHeight(Integer.valueOf(heightInput.getText().toString()));
            resizeImage();
            img_view.setImageBitmap(selectedImageBitmap);
        });

        alertDialog.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());

        alertDialog.show();

    }

    private void resizeImage() {
        if (selectedImageBitmap != null) {
            Mat selectedImageMat = new Mat(selectedImageBitmap.getHeight(), selectedImageBitmap.getWidth(), CvType.CV_8UC3);
            Utils.bitmapToMat(selectedImageBitmap, selectedImageMat);

            Mat resized = new Mat();
            Size newSize = new Size(getWidth(), getHeight());
            Imgproc.resize(selectedImageMat, resized, newSize);

            selectedImageBitmap = Bitmap.createBitmap(resized.width(), resized.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(resized, selectedImageBitmap);
        }
    }

    private void setWidth(int width) {
        this.width = width;
    }

    private int getWidth() {
        return width;
    }

    private void setHeight(int height) {
        this.height = height;
    }

    private int getHeight() {
        return height;
    }
}
