package com.example.seefood;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.seefood.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileInputStream;
import java.io.IOException;

public class ResultsActivity extends AppCompatActivity {
    RelativeLayout layout;
    Bitmap bitmap;
    ImageView imageView;
    ImageView hotdog, not_hotdog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        layout = findViewById(R.id.results_activity);
        layout.setOnClickListener(view -> {
            finish();
        });

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
        hotdog = findViewById(R.id.hotdog);
        not_hotdog = findViewById(R.id.not_hotdog);

        switch (runMLModel()) {
            case 0:
                hotdog.setVisibility(View.VISIBLE);
                not_hotdog.setVisibility(View.INVISIBLE);
            case 1:
                hotdog.setVisibility(View.INVISIBLE);
                not_hotdog.setVisibility(View.VISIBLE);
            default:
                hotdog.setVisibility(View.INVISIBLE);
                not_hotdog.setVisibility(View.VISIBLE);
        }
    }

    private int runMLModel() {
        try {
            Model model = Model.newInstance(this);

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.UINT8);
            bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
            inputFeature0.loadBuffer(TensorImage.fromBitmap(bitmap).getBuffer());

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            // Releases model resources if no longer used.
            model.close();

            return getMax(outputFeature0.getFloatArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1;
    }

    private int getMax(float[] arr) {
        int max = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > arr[max]) {
                max = i;
            }
        }
        return max;
    }
}
