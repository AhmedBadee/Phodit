package ImgProcFunctions;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

public class SaveToGallery {

    private Bitmap selectedImageBitmap;
    private String savedImageName;

    public SaveToGallery() {

    }

    public void setImageToSave(Bitmap selectedImageBitmap) {
        this.selectedImageBitmap = selectedImageBitmap;
    }

    public void save(String imageName) {
        String path = Environment.getExternalStorageDirectory().toString();
        FileOutputStream outputStream;
        File directory = new File(path + "/Phodit");

        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                Log.e("Directory Creation", "Can't Create");
            } else {
                Log.e("Directory Creation", "Successfully Created");
            }
        } else {
            Log.e("Directory Creation", "Exists");
        }

        String fileName = imageName + "_phodit.jpg";
        File file = new File(directory.getAbsolutePath() + File.separator + fileName);

        try {
            outputStream = new FileOutputStream(file);

            selectedImageBitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        savedImageName = file.toString();
    }

    public String getSavedImageName() {
        return savedImageName;
    }
}
