package com.sample;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
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
import com.sample.stego_image.decode_process;

/**
 * Created by timian on 2018/11/16.
 */

public class DecodeProcessImpl extends AppCompatActivity implements View.OnClickListener {

    private void Decodeprocessing() {


        if (((ImageView) findViewById(R.id.ivImageDecode)).getDrawable() == null) {
            Toast.makeText(getApplicationContext(), "Please attach an stegano image", Toast.LENGTH_LONG).show();
            return;
        } else {

            ImageView ivImageResult = (ImageView) findViewById(R.id.ivImageDecode);
            Bitmap bi3 = ((BitmapDrawable) ivImageResult.getDrawable()).getBitmap();
            Bitmap bi2 = bi3.copy(Bitmap.Config.ARGB_8888, true);

            String hasilExtract = Utils.extractMessage(bi2);

            EditText txtResult = (EditText) findViewById(R.id.etTextDecode);
            txtResult.setText(hasilExtract);
            Toast.makeText(getApplicationContext(), "Decode Finished", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent) {
        super.onActivityResult(paramInt1, paramInt2, paramIntent);
        if (paramInt1 == 1 && paramInt2 == RESULT_OK && paramIntent != null && paramIntent.getData() != null) {
            Uri uri = paramIntent.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ImageView imageView = (ImageView) findViewById(R.id.ivImageDecode);
                imageView.setImageBitmap(bitmap);
                Toast.makeText(this, "Image Selected", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Incorrect Image Selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode_process);

        Button bChooseImage2 = (Button) findViewById(R.id.bChooseImage2);
        bChooseImage2.setOnClickListener(this);
        Button bDecodeProcess = (Button) findViewById(R.id.bDecodeProcess);
        bDecodeProcess.setOnClickListener(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Decode Process");

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bChooseImage2:
                Intent galleryIntent2 = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent2.setType("image/*");
                startActivityForResult(Intent.createChooser(galleryIntent2, "Select Image"), 1);
                EditText txtResult = (EditText) findViewById(R.id.etTextDecode);
                txtResult.setText("");
                TextView textView3 = (TextView) findViewById(R.id.textView2);
                textView3.setText("");
                break;
            case R.id.bDecodeProcess:
                Decodeprocessing();
                TextView textView2 = (TextView) findViewById(R.id.textView2);
                textView2.setText("input message");
                break;
        }
    }
}
