package com.university.treklogger;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.RoundedCornerTreatment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.university.treklogger.screens.AddTrekActivity;
import com.university.treklogger.screens.MapsActivity;
import com.university.treklogger.screens.PeopleActivity;
import com.university.treklogger.screens.SettingsActivity;
import com.university.treklogger.screens.TrekDetailsActivity;
import com.university.treklogger.screens.TreksActivity;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;

    TextView userGreetings;

    ConstraintLayout frameLayout;
    LinearLayout treksLayout;
    ImageButton home, treks, startTrek, trekkers, settings;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        frameLayout = findViewById(R.id.frame_layout);

        home = findViewById(R.id.home);
        treks = findViewById(R.id.locations);
        startTrek = findViewById(R.id.navigate);
        trekkers = findViewById(R.id.trekkers);
        settings = findViewById(R.id.settings);
        toolbar = findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.menu);

        toolbar.setNavigationOnClickListener(v -> {
            frameLayout.removeAllViews();
            loadFragment(new SettingsActivity());
        });

        home.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        treks.setOnClickListener(v -> {
            frameLayout.removeAllViews();
            loadFragment(new TreksActivity());
        });

        startTrek.setOnClickListener(v -> {
            frameLayout.removeAllViews();
//            loadFragment(new TrekkingActivity());
            Intent intent = new Intent(this, AddTrekActivity.class);
            startActivity(intent);
        });

        trekkers.setOnClickListener(v -> {
//            frameLayout.removeAllViews();
//            loadFragment(new PeopleActivity());
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        });

        settings.setOnClickListener(v -> {
            frameLayout.removeAllViews();
            loadFragment(new SettingsActivity());
        });


        treksLayout = findViewById(R.id.treksLayout);

        auth = FirebaseAuth.getInstance();

        userGreetings = findViewById(R.id.userGreeting);

        userGreetings.setText("Welcome, " + auth.getCurrentUser().getDisplayName().substring(0, auth.getCurrentUser().getDisplayName().indexOf(" ")) + "!");

        fetchTreks();
    }

    private void fetchTreks(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("treks")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        treksLayout.removeAllViews();
                        for (DocumentSnapshot document : task.getResult()) {
                            CardView trekCard = new CardView(this);
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

                            ShapeableImageView shapeableImageView = new ShapeableImageView(this);
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

                            Glide.with(this).load(document.getString("coverImage")).into(shapeableImageView);
                            trekCard.addView(shapeableImageView);

                            TextView trekTitle = new TextView(this);
                            trekTitle.setText(document.getString("title"));
                            trekTitle.setTextSize(20);
                            trekTitle.setTypeface(trekTitle.getTypeface(), Typeface.BOLD);
                            trekTitle.setTextColor(getResources().getColor(R.color.white));
                            trekTitle.setPadding(16, 16, 16, 16);
                            trekCard.addView(trekTitle);

                            trekCard.setOnClickListener(v -> {
                                frameLayout.removeAllViews();
                                loadFragment(new TrekDetailsActivity(document.getId()));
                            });

                            treksLayout.addView(trekCard);
                            TextView spacer = new TextView(this);
                            spacer.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    20
                            ));
                            spacer.setBackgroundColor(getResources().getColor(R.color.transparent));
                            treksLayout.addView(spacer);

                        }
                    } else {
                        Log.d("TREKS", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void loadFragment(Fragment fragment) {
        // Create a FragmentManager
        FragmentManager fm = getSupportFragmentManager();
        // Create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // Replace the FrameLayout specified by the id with the new Fragment
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit(); // save the changes
    }
}