package com.spells.phodit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import ImgProcFunctions.BlackAndWhite;
import ImgProcFunctions.Resize;
import ImgProcFunctions.SaveToGallery;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button chooseImgBtn;
    private ImageView img_view;
    private TextView loading_textview;
    private ProgressBar saving_progress;

    private MenuItem save;
    private MenuItem blackAndWhite;
    private MenuItem resize;

    private LinearLayout progressLayout;

    private String imageName;
    private Bitmap selectedImageBitmap;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    static { System.loadLibrary("opencv_java3"); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chooseImgBtn     = (Button) findViewById(R.id.choose_img_btn);
        chooseImgBtn.setOnClickListener(this);

        img_view         = (ImageView) findViewById(R.id.img_view);

        loading_textview = (TextView) findViewById(R.id.loading_textview);

        saving_progress  = (ProgressBar) findViewById(R.id.saving_progress);

        progressLayout   = (LinearLayout) findViewById(R.id.progress_layout);

        verifyStoragePermissions(MainActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.img_proc_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        save = menu.findItem(R.id.save_to_gallery);
        blackAndWhite = menu.findItem(R.id.black_and_white);
        resize = menu.findItem(R.id.resize);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemSelected = item.getItemId();

        switch (itemSelected) {
            case R.id.save_to_gallery:
                saveImgToGallery();
                break;
            case R.id.black_and_white:
                blackWhite();
                break;
            case R.id.resize:
                resize();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        Intent chooseImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(chooseImage, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            Uri selectedImage = data.getData();

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                File imageFile = new File(picturePath);
                imageName = imageFile.getName();
                imageName = imageName.substring(0, imageName.indexOf("."));
                Log.e("Image Name", imageName);
                cursor.close();
            }

            try {
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                Log.e("Bitmap Creation", "Created");
                img_view.setImageBitmap(selectedImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            updateUI();
        }
    }

    private void blackWhite() {
        new BlackAndWhiteTask().execute();
    }

    private void saveImgToGallery() {
        new SavingTask().execute();
    }

    private void resize() {
        new ResizeTask().execute();
    }

    private void updateUI() {
        chooseImgBtn.setVisibility(View.GONE);

        save.setEnabled(true);
        blackAndWhite.setEnabled(true);
        resize.setEnabled(true);
    }

    private class SavingTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loading_textview.setVisibility(View.VISIBLE);
            loading_textview.setText(R.string.saving);
            progressLayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {

            String savedImageName;

            SaveToGallery saveToGallery = new SaveToGallery();
            saveToGallery.setImageToSave(selectedImageBitmap);
            saveToGallery.save(imageName);
            savedImageName = saveToGallery.getSavedImageName();

            MediaScannerConnection.scanFile(getApplicationContext(), new String[]{savedImageName}, null,
                    (path1, uri) -> Log.e("Scan path", path1));

            return savedImageName;
        }

        @Override
        protected void onPostExecute(String path) {
            super.onPostExecute(path);
            saving_progress.setVisibility(View.INVISIBLE);
            loading_textview.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Saved to: " + path, Toast.LENGTH_LONG).show();
        }
    }

    private class BlackAndWhiteTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loading_textview.setVisibility(View.VISIBLE);
            loading_textview.setText(R.string.working);
            progressLayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            BlackAndWhite blackAndWhiteFunction = new BlackAndWhite();
            blackAndWhiteFunction.setSelectedImage(selectedImageBitmap);
            blackAndWhiteFunction.applyConversion();
            selectedImageBitmap = blackAndWhiteFunction.getBitmapImageToShow();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            img_view.setImageBitmap(selectedImageBitmap);

            progressLayout.setVisibility(View.INVISIBLE);
            loading_textview.setVisibility(View.INVISIBLE);
        }
    }

    private class ResizeTask extends AsyncTask<Void, Void, Void> {

        Resize resize = new Resize(MainActivity.this);
        boolean sizeInserted = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            resize.setSelectedImage(selectedImageBitmap);
            sizeInserted = resize.setWidthAndHeight();

            /* loading_textview.setVisibility(View.VISIBLE);
            loading_textview.setText(R.string.working);
            progressLayout.setVisibility(View.VISIBLE); */
        }

        @Override
        protected Void doInBackground(Void... voids) {

            while (sizeInserted) {
                resize.resizeImage();
                selectedImageBitmap = resize.getSelectedImageBitmap();
                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            img_view.setImageBitmap(selectedImageBitmap);

            progressLayout.setVisibility(View.INVISIBLE);
            loading_textview.setVisibility(View.INVISIBLE);
        }
    }

    private static void verifyStoragePermissions(AppCompatActivity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
}
