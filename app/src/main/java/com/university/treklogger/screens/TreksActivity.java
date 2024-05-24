package com.university.treklogger.screens;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.RoundedCornerTreatment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;
import com.university.treklogger.R;
import com.university.treklogger.components.BottomBar;

import java.util.Objects;

public class TreksActivity extends Fragment {
    
    private LinearLayout treksList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_treks, container, false);
        treksList = view.findViewById(R.id.treksList);
        fetchTreks();
        return view;
    }

    private void fetchTreks(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("TREKS", "Fetching treks");
        db.collection("treks")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            Log.d("TREKS", document.getId() + " => " + document.getData());
                            // add a view to the layout
                            CardView trekCard = new CardView(TreksActivity.this.getContext());
                            trekCard.setLayoutParams(
                                    new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    )
                            );
                            trekCard.setRadius(24);
                            trekCard.setCardElevation(8);
                            trekCard.setUseCompatPadding(true);
                            trekCard.setBackgroundColor(getResources().getColor(R.color.transparent));
                            trekCard.setPadding(16, 16, 16, 16);
                            trekCard.setOnClickListener(v -> {
                                // load fragment but with extras
                                loadFragment(new TrekDetailsActivity(document.getId()));
                            });

                            ShapeableImageView shapeableImageView = new ShapeableImageView(TreksActivity.this.getContext());
                            shapeableImageView.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    500
                            ));
                            shapeableImageView.setAdjustViewBounds(true);
                            shapeableImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                            float cornerSize = 18 * getResources().getDisplayMetrics().density;

                            shapeableImageView.setShapeAppearanceModel(shapeableImageView.getShapeAppearanceModel()
                                    .toBuilder()
                                    .setBottomRightCorner(new RoundedCornerTreatment(cornerSize))
                                    .setTopRightCorner(new RoundedCornerTreatment(cornerSize))
                                    .build());
                            shapeableImageView.setForeground(getResources().getDrawable(R.drawable.image_overlay));

                            Glide.with(TreksActivity.this.getContext()).load(document.getString("coverImage")).into(shapeableImageView);
                            trekCard.addView(shapeableImageView);

                            TextView trekTitle = new TextView(TreksActivity.this.getContext());
                            trekTitle.setText(document.getString("title"));
                            trekTitle.setTextSize(20);
                            trekTitle.setTypeface(trekTitle.getTypeface(), Typeface.BOLD);
                            trekTitle.setTextColor(getResources().getColor(R.color.white));
                            trekTitle.setPadding(16, 16, 16, 16);
                            trekCard.addView(trekTitle);

                            treksList.addView(trekCard);
                            TextView spacer = new TextView(TreksActivity.this.getContext());
                            spacer.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    20
                            ));
                            spacer.setBackgroundColor(getResources().getColor(R.color.transparent));
                            treksList.addView(spacer);

                        }
                    } else {
                        Log.d("TREKS", "Error getting documents: ", task.getException());
                    }
                });


    }

    private void loadFragment(Fragment fragment) {
        // Create a FragmentManager
        FragmentManager fm = getParentFragmentManager();
        // Create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // Replace the FrameLayout specified by the id with the new Fragment
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit(); // save the changes
    }
}
