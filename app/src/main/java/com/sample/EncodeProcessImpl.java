package com.sample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sample.stego_image.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

/**
 * Created by timian on 2018/11/16.
 */

public class EncodeProcessImpl extends AppCompatActivity implements View.OnClickListener {

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
            case R.id.bChooseImage:
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), 1);
                break;

            case R.id.bEncodeProcess:
                Encodeprocessing();
                break;
        }
    }


    @Override
    protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent) { //memilih dan memunculkan gambar yang kita pilih
        super.onActivityResult(paramInt1, paramInt2, paramIntent);
        if (paramInt1 == 1 && paramInt2 == RESULT_OK && paramIntent != null && paramIntent.getData() != null) {
            Uri uri = paramIntent.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ImageView imageView = (ImageView) findViewById(R.id.ivImageEncode);
                imageView.setImageBitmap(bitmap);
                Toast.makeText(this, "Image Selected", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Toast.makeText(this, "Incorrect Image Selected", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void Encodeprocessing() {
        EditText txtPesan = (EditText) findViewById(R.id.etTextEncode);
        String text = txtPesan.getText().toString();

        if (text.replaceAll(" ", "") == "") {
            Toast.makeText(getApplicationContext(), "Please write a message", Toast.LENGTH_LONG).show();
            return;
        }

        ImageView localImageView = (ImageView) findViewById(R.id.ivImageEncode);
        if (localImageView.getDrawable() == null) {
            Toast.makeText(getApplicationContext(), "Please attach an image", Toast.LENGTH_LONG).show();
            return;
        }

        Bitmap localBitmap = ((BitmapDrawable) localImageView.getDrawable()).getBitmap();
        if (localBitmap == null) {
            Toast.makeText(getApplicationContext(), "Please attach an image", Toast.LENGTH_LONG).show();
            return;
        }

        Bitmap encodeBitmap = Utils.insertMessage2(localBitmap, text);
        saveImage(encodeBitmap);
        Toast.makeText(getApplicationContext(), "Image Saved ", Toast.LENGTH_LONG).show();
    }

    public void saveImage(Bitmap paramBitmap) {

        File localFile1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Image-Stego");
        localFile1.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "saved-" + n + ".jpg";
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
