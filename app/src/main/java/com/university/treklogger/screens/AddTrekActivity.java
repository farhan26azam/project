package com.university.treklogger.screens;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.university.treklogger.R;
import com.university.treklogger.entities.Point;
import com.university.treklogger.entities.Trek;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddTrekActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText trekTitleEditText;
    private EditText trekDescriptionEditText;
    private EditText pointNameEditText;
    private EditText latitudeEditText;
    private EditText longitudeEditText;
    private Button addPointButton;
    private Button saveTrekButton;
    private ImageView coverImageView;
    private Uri coverImageUri;
    private List<Uri> pointImageUris;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private String trekId;
    private List<Point> points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trek);

        trekTitleEditText = findViewById(R.id.trek_title_edit_text);
        trekDescriptionEditText = findViewById(R.id.trek_description_edit_text);
        pointNameEditText = findViewById(R.id.point_name_edit_text);
        latitudeEditText = findViewById(R.id.latitude_edit_text);
        longitudeEditText = findViewById(R.id.longitude_edit_text);
        addPointButton = findViewById(R.id.add_point_button);
        saveTrekButton = findViewById(R.id.save_trek_button);
        coverImageView = findViewById(R.id.cover_image_view);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        pointImageUris = new ArrayList<>();
        points = new ArrayList<>();

        coverImageView.setOnClickListener(view -> openFileChooser());

        addPointButton.setOnClickListener(view -> addPoint());

        saveTrekButton.setOnClickListener(view -> saveTrek());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Inside onActivityResult method
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (coverImageView.isFocused()) {
                // This is the cover image selection
                coverImageUri = data.getData();
                coverImageView.setImageURI(coverImageUri);
            } else {
                // This is the point image selection
                Uri pointImageUri = data.getData();
                pointImageUris.add(pointImageUri);
                // Show selected image for point
                // (You need to implement this part in your UI)
            }
        }
    }

    // Inside addPoint method
    private void addPoint() {
        String name = pointNameEditText.getText().toString().trim();
        double latitude = Double.parseDouble(latitudeEditText.getText().toString().trim());
        double longitude = Double.parseDouble(longitudeEditText.getText().toString().trim());
        String imageUri = null; // This will be set later

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter point name", Toast.LENGTH_SHORT).show();
            return;
        }

        // If there's a point image URI for this point, set it
        if (!pointImageUris.isEmpty()) {
            // Get the image URI for this point (assuming it was added in the onActivityResult method)
            imageUri = pointImageUris.get(pointImageUris.size() - 1).toString();
        }

        // Create a new point with the provided data
        Point point = new Point(latitude, longitude, imageUri, name);

        // Add the point to the list
        points.add(point);

        // Clear input fields
        pointNameEditText.setText("");
        latitudeEditText.setText("");
        longitudeEditText.setText("");
    }


    private void saveTrek() {
        String title = trekTitleEditText.getText().toString().trim();
        String description = trekDescriptionEditText.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || coverImageUri == null || points.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        // Upload cover image
        StorageReference coverRef = storageRef.child("treks/" + trekId + "/cover.jpg");
        coverRef.putFile(coverImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Cover image uploaded successfully, now upload points images
                    uploadPointImages();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(AddTrekActivity.this, "Failed to upload cover image", Toast.LENGTH_SHORT).show();
                    Log.e("AddTrekActivity", "Failed to upload cover image", e);
                });
    }

    private void uploadPointImages() {
        // Upload images of each point
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            Uri imageUri = pointImageUris.get(i);
            if (imageUri != null) {
                StorageReference imageRef = storageRef.child("treks/" + trekId + "/points/point" + i + ".jpg");
                int finalI = i;
                imageRef.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            // Point image uploaded successfully
                            point.setImageUrl(imageRef.getPath());
                            if (finalI == points.size() - 1) {
                                // All images uploaded, now save trek to Firestore
                                saveTrekToFirestore();
                            }
                        })
                        .addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Toast.makeText(AddTrekActivity.this, "Failed to upload point image", Toast.LENGTH_SHORT).show();
                            Log.e("AddTrekActivity", "Failed to upload point image", e);
                        });
            } else {
                if (i == points.size() - 1) {
                    // All images uploaded, now save trek to Firestore
                    saveTrekToFirestore();
                }
            }
        }
    }

    private void saveTrekToFirestore() {
        Trek trek = new Trek(trekTitleEditText.getText().toString().trim(),
                trekDescriptionEditText.getText().toString().trim(),
                coverImageUri.toString(), points);

        db.collection("treks")
                .add(trek)
                .addOnSuccessListener(documentReference -> {
                    progressDialog.dismiss();
                    Toast.makeText(AddTrekActivity.this, "Trek added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(AddTrekActivity.this, "Failed to add trek", Toast.LENGTH_SHORT).show();
                    Log.e("AddTrekActivity", "Failed to add trek", e);
                });
    }
}

