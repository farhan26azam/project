package com.university.treklogger.screens;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.university.treklogger.BuildConfig;
import com.university.treklogger.R;
import com.university.treklogger.entities.Point;
import com.university.treklogger.entities.Trek;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.SSLEngineResult;

public class AddTrekActivity extends AppCompatActivity {

    private static final int AUTOCOMPLETE_REQUEST_CODE = 3;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_POINT_IMAGE_REQUEST = 2;
    private EditText trekTitleEditText;
    private EditText trekDescriptionEditText;
    private EditText pointNameEditText;
    private Button addPointButton;
    private Button addPointNameButton;
    private Button saveTrekButton;
    private ImageView coverImageView;
    private ImageView pointImageView;
    private Uri coverImageUri;
    private Uri pointImageUri;
    private List<Uri> pointImageUris;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private String trekId;
    private List<Point> points;

    LinearLayout pointsList;

    private Trek trek = new Trek();

    Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String apiKey = BuildConfig.PLACES_API_KEY;

        if(TextUtils.isEmpty(apiKey)){
            throw new RuntimeException("Please set your API key in the gradle.properties file");
        }

        Places.initialize(getApplicationContext(), apiKey);

        PlacesClient placesClient = Places.createClient(this);

        setContentView(R.layout.activity_add_trek);

        trekTitleEditText = findViewById(R.id.trek_title_edit_text);
        trekDescriptionEditText = findViewById(R.id.trek_description_edit_text);
        addPointNameButton = findViewById(R.id.add_point_name_button);
        pointNameEditText = findViewById(R.id.point_name_edit_text);
        addPointButton = findViewById(R.id.add_point_button);
        saveTrekButton = findViewById(R.id.save_trek_button);
        coverImageView = findViewById(R.id.cover_image_view);
        pointImageView = findViewById(R.id.point_image_view);
        pointsList = findViewById(R.id.pointsList);

        addPointNameButton.setOnClickListener(view -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        pointImageUris = new ArrayList<>();
        points = new ArrayList<>();

        coverImageView.setOnClickListener(view -> openCoverImageChooser());

        pointImageView.setOnClickListener(view -> openPointImageChooser());

        addPointButton.setOnClickListener(view -> addPoint());

        saveTrekButton.setOnClickListener(view -> saveTrek());
    }

    private void openCoverImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openPointImageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_POINT_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Inside onActivityResult method
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            coverImageUri = data.getData();
            coverImageView.setImageURI(coverImageUri);
            coverImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            // set fixed size for image view
            coverImageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 500));
        }
        else if(requestCode == PICK_POINT_IMAGE_REQUEST  && resultCode == RESULT_OK && data != null && data.getData() != null){
            pointImageUri = data.getData();
            pointImageView.setImageURI(pointImageUri);
        }

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                place = Autocomplete.getPlaceFromIntent(data);
                pointNameEditText.setText(place.getName());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("AddTrekActivity", status.getStatusMessage());
            }else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    // Inside addPoint method
    private void addPoint() {
        String title = trekTitleEditText.getText().toString().trim();
        String name = pointNameEditText.getText().toString().trim();
        double latitude = Objects.requireNonNull(place.getLatLng()).latitude;
        double longitude = Objects.requireNonNull(place.getLatLng()).longitude;

        String imageUri = null; // This will be set later

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter point name", Toast.LENGTH_SHORT).show();
            return;
        }

        // If there's a point image URI for this point, set it
        if (pointImageUri!=null) {
            // Get the image URI for this point (assuming it was added in the onActivityResult method)
            imageUri = pointImageUri.toString();
        }

        // Create a new point with the provided data
        Point point = new Point(latitude, longitude, imageUri, name);

        // Add the point to the list


        TextView pointTV = new TextView(this);
        pointTV.setText(name);
        pointTV.setPadding(16,16,16,16);
        pointTV.setTextSize(24);
        pointTV.setBackgroundColor(getResources().getColor(R.color.theme_pink));
        pointTV.setTextColor(getResources().getColor(R.color.white));

        pointsList.addView(pointTV);
        TextView spacing = new TextView(this);
        spacing.setBackgroundColor(getResources().getColor(R.color.transparent));

        pointsList.addView(spacing);

        StorageReference imageRef = storageRef.child("points/" + title.replace(" ", "_") + name + ".jpg");
        Uri pointImageUri  = Uri.parse(imageUri);
        imageRef.putFile(pointImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        point.setImageUrl(downloadUrl); // Save the global URL to your point object
                        Toast.makeText(AddTrekActivity.this, "Point image uploaded successfully", Toast.LENGTH_LONG).show();
                        Log.d("AddTrekActivity", "Image URL: " + downloadUrl);
                    }).addOnFailureListener(e -> {
                        Toast.makeText(AddTrekActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                        Log.e("AddTrekActivity", "Failed to get download URL", e);
                    });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(AddTrekActivity.this, "Failed to upload point image", Toast.LENGTH_SHORT).show();
                    Log.e("AddTrekActivity", "Failed to upload point image", e);
                });

        points.add(point);

        // Clear input fields
        pointNameEditText.setText("");
        pointImageView.setImageDrawable(getDrawable(R.drawable.ic_launcher_foreground));
    }


    private void saveTrek() {
        String title = trekTitleEditText.getText().toString().trim();
        String description = trekDescriptionEditText.getText().toString().trim();
String coverImage;
        if (title.isEmpty() || description.isEmpty() || coverImageUri == null || points.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        // Upload cover image
        StorageReference coverRef = storageRef.child("treks/" + title.replace(" ", "_") + ".jpg");
        coverRef.putFile(coverImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Cover image uploaded successfully, now upload points images
                    coverRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        trek.setCoverImageUrl(downloadUrl);
                        Toast.makeText(AddTrekActivity.this, "Cover image uploaded successfully", Toast.LENGTH_LONG).show();
                        Log.d("AddTrekActivity", "Image URL: " + uri.toString());
                    }).addOnFailureListener(e -> {
                        Toast.makeText(AddTrekActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                        Log.e("AddTrekActivity", "Failed to get download URL", e);
                    });
//                    uploadPointImages();
                    saveTrekToFirestore();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(AddTrekActivity.this, "Failed to upload cover image", Toast.LENGTH_SHORT).show();
                    Log.e("AddTrekActivity", "Failed to upload cover image", e);
                });
    }

    private void saveTrekToFirestore() {
//        Trek trek = new Trek(trekTitleEditText.getText().toString().trim(),
//                trekDescriptionEditText.getText().toString().trim(),
//                coverImageUri.toString(), points);

        Log.d("***TREK", trek.toString());

        trek.setTitle(trekTitleEditText.getText().toString().trim());
        trek.setDescription(trekDescriptionEditText.getText().toString().trim());
        trek.setPoints(points);

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

