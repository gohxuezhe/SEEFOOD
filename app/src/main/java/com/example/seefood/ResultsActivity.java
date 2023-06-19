package com.example.seefood;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;

public class ResultsActivity extends AppCompatActivity {
    Bitmap bitmap;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        String filename = getIntent().getStringExtra("bitmap");
        try {
            FileInputStream stream = this.openFileInput(filename);
            bitmap = BitmapFactory.decodeStream(stream);
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageView = findViewById(R.id.picture_taken);

        imageView.setImageBitmap(bitmap);
    }
}
