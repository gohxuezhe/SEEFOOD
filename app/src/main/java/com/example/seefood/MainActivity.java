package com.example.seefood;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {
    RelativeLayout layout;
    View gradient;
    TextView get_started;
    TextView touch_to_seefood;
    Button camera_button;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPermission();

        layout = findViewById(R.id.main_activity);
        gradient = findViewById(R.id.gradient);
        get_started = findViewById(R.id.get_started);
        camera_button = findViewById(R.id.camera_button);
        touch_to_seefood = findViewById(R.id.touch_to_seefood);

        layout.setOnClickListener(view -> {
            final Animation out = new AlphaAnimation(1.0f, 0.0f);
            out.setDuration(500);
            gradient.setAnimation(out);
            gradient.setVisibility(INVISIBLE);

            get_started.setVisibility(INVISIBLE);
            camera_button.setVisibility(VISIBLE);
            touch_to_seefood.setVisibility(VISIBLE);
        });

        camera_button.setOnClickListener(view -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 10);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 10) {
            if (data != null) {
                bitmap = (Bitmap) data.getExtras().get("data");
                try {
                    // Write file
                    String filename = "bitmap.png";
                    FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    // Cleanup
                    stream.close();
                    bitmap.recycle();

                    // Pop intent
                    Intent intent = new Intent(this, ResultsActivity.class);
                    intent.putExtra("bitmap", filename);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 11) {
            if (grantResults.length > 0) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    getPermission();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 11);
        }
    }

}