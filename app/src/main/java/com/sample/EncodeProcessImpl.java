package com.sample;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sample.stego_image.Formula;
import com.sample.stego_image.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

/**
 * Created by timian on 2018/11/16.
 */

public class EncodeProcessImpl extends AppCompatActivity implements View.OnClickListener {

    public static final int PICK_IMAGE = 2;
    private static int RESULT_LOAD_IMG = 1;
    private ProgressDialog progress;
    String status = "-";
    String fname = "";
    Double d = 0.0;
    int pixel = 25;
    static int MAX_COUNT = 160;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encode_process);

        Button bChooseImage = (Button) findViewById(R.id.bChooseImage);
        bChooseImage.setOnClickListener(this);
        Button bEncodeProcess = (Button) findViewById(R.id.bEncodeProcess);
        bEncodeProcess.setOnClickListener(this);

        EditText txtStatus = (EditText) findViewById(R.id.etTextEncode);
        final TextView lblCount = (TextView) findViewById(R.id.tv_char);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Encode Process");

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bChooseImage: //di klik menuju choose image
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                // Start the Intent
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), RESULT_LOAD_IMG);
                break;

            case R.id.bEncodeProcess: //di klik menuju encode proses
                Encodeprocessing();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.encode, menu); // untuk tampilan atas pojok kanan
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar Item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Hasil Proses Encoding")
                .setMessage("\n Status \t : " + status + " \n PSNR \t :  " + d + "\n\n Stego Image Name : \n" + fname + "\n")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        dialog.show();
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent) { //memilih dan memunculkan gambar yang kita pilih
        super.onActivityResult(paramInt1, paramInt2, paramIntent);
        if (paramInt1 == RESULT_LOAD_IMG && paramInt2 == RESULT_OK && paramIntent != null && paramIntent.getData() != null) {
            Uri uri = paramIntent.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ImageView imageView = (ImageView) findViewById(R.id.ivImageEncode);
                imageView.setImageBitmap(bitmap);
                Toast.makeText(this, "Image Selected", Toast.LENGTH_SHORT).show();
                status = "Image Selected";
            } catch (Exception e) {
                Toast.makeText(this, "Incorrect Image Selected", Toast.LENGTH_SHORT).show();
                status = "Incorrect Image Selected";
            }
        }
    }


    public void Encodeprocessing() {
        EditText txtPesan = (EditText) findViewById(R.id.etTextEncode);
        String text = txtPesan.getText().toString();

        if (text.replaceAll(" ", "") == "") {
            Toast.makeText(getApplicationContext(), "Please write a message", Toast.LENGTH_LONG).show();
            status = "Please write a message";
            return;
        }

        ImageView localImageView = (ImageView) findViewById(R.id.ivImageEncode);
        if (localImageView.getDrawable() == null) {
            Toast.makeText(getApplicationContext(), "Please attach an image", Toast.LENGTH_LONG).show();
            status = "Please attach an image";
            return;
        }

        Bitmap localBitmap = ((BitmapDrawable) localImageView.getDrawable()).getBitmap();
        Bitmap copy = localBitmap.copy(localBitmap.getConfig(), true);
        int pixel = copy.getPixel(0, 0);
        copy.setPixel(0, 0, -723208);
        int pixel2 = copy.getPixel(0, 0);

        if (localBitmap == null) {

            Toast.makeText(getApplicationContext(), "Please attach an image", Toast.LENGTH_LONG).show();
            status = "Please attach an image";
            return;
        }

        Bitmap Stego_Image = Utils.insertMessage(localBitmap, text); // masuk ke metthod insert message
        SaveImage(Stego_Image);
        Toast.makeText(getApplicationContext(), "Image Saved ", Toast.LENGTH_LONG).show();
        status = "encoding berhasil";
    }

    public void SaveImage(Bitmap paramBitmap) {

        File localFile1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Image-Stego");
        localFile1.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        fname = "Singgih-" + n + ".jpg";
        File localFile2 = new File(localFile1, fname);
        scanMedia(localFile2);

        if (localFile2.exists()) {
            localFile2.delete();
        }

        try {
            FileOutputStream out = new FileOutputStream(localFile2);
            paramBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void scanMedia(File paramFile) {
        sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(paramFile)));
    }
}
