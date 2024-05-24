package com.university.treklogger.screens;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.university.treklogger.R;
import com.university.treklogger.entities.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TrekDetailsActivity extends Fragment {

    private String coverImage, trekMainTitle, trekMainDescription;
    private List<Point> points = new ArrayList<>();

    private ImageView coverImageView;
    private TextView trekTitle, trekDescription;
    LinearLayout pointsList;

    private final String trekId;

    public TrekDetailsActivity(String trekId) {
        this.trekId = trekId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_trek_details, container, false);
        coverImageView = view.findViewById(R.id.trekCoverImage);
        trekTitle = view.findViewById(R.id.trekMainTitle);
        trekDescription = view.findViewById(R.id.trekMainDescription);
        pointsList = view.findViewById(R.id.pointsList);

        loadTrekDetails();

        return view;
    }

    private void loadTrekDetails() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("treks")
                .document(trekId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Load cover image
                            coverImage = document.getString("coverImage");
                            if (coverImage != null) {
                                coverImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                Glide.with(this).load(coverImage).into(coverImageView);
                            }

                            // Load trek title and description
                            trekMainTitle = document.getString("title");
                            trekMainDescription = document.getString("description");
                            trekTitle.setText(trekMainTitle);
                            trekDescription.setText(trekMainDescription);

                            // Load points associated with the trek
                            List<DocumentReference> pointsRefs = (List<DocumentReference>) document.get("points");
                            if (pointsRefs != null) {
                                loadPoints(pointsRefs);
                            }
                        }
                    } else {
                        Log.e("Firestore", "Failed to load trek details", task.getException());
                    }
                });
    }

    private void loadPoints(List<DocumentReference> pointsRefs) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        for (DocumentReference pointRef : pointsRefs) {
            pointRef.get().addOnCompleteListener(pointTask -> {
                if (pointTask.isSuccessful() && pointTask.getResult() != null) {
                    DocumentSnapshot pointDocument = pointTask.getResult();
                    if (pointDocument.exists()) {
                        // Create Point object from Firestore document
                        Point point = new Point(
                                Objects.requireNonNull(pointDocument.getGeoPoint("coordinates")).getLatitude(),
                                Objects.requireNonNull(pointDocument.getGeoPoint("coordinates")).getLongitude(),
                                pointDocument.getString("image"),
                                pointDocument.getString("name")
                        );

                        LinearLayout singlePointLayout = new LinearLayout(getContext());
                        singlePointLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        ));
                        singlePointLayout.setOrientation(LinearLayout.HORIZONTAL);

                        ImageView pointIcon = new ImageView(getContext());
                        pointIcon.setLayoutParams(new LinearLayout.LayoutParams(
                                100,
                                100
                        ));
                        pointIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        // get drawable points.png from resources
                        pointIcon.setImageResource(R.drawable.points);
                        singlePointLayout.addView(pointIcon);

                        // Add point to list
                        TextView pointTextView = new TextView(getContext());
                        pointTextView.setText(point.getName());
                        pointTextView.setTextSize(20);
                        pointTextView.setPadding(16, 16, 16, 16);
                        pointTextView.setTextColor(getResources().getColor(R.color.dark_gray));

                        singlePointLayout.addView(pointTextView);
                        pointsList.addView(singlePointLayout);

                        points.add(point);
                        Log.d("Point", "Loaded point: " + point.getName());
                        Log.d("Point***", "Images: " + point.getImage());
                    }
                } else {
                    Log.e("Firestore", "Failed to load point details", pointTask.getException());
                }
            });
        }
    }
}
