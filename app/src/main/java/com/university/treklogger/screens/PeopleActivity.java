package com.university.treklogger.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.university.treklogger.R;

public class PeopleActivity extends Fragment {

    private LinearLayout userListLayout;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_people, container, false);

        userListLayout = rootView.findViewById(R.id.user_list_layout);
        db = FirebaseFirestore.getInstance();

        // Load users from Firestore
        loadUsers();

        return rootView;
    }

    private void loadUsers() {
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String username = document.getString("name");
                            addUserToLayout(username);
                        }
                    } else {
                        // Handle errors
                    }
                });
    }

    private void addUserToLayout(String username) {
        // Create a new TextView for each user
        TextView textView = new TextView(requireContext());
        textView.setText(username);
        textView.setTextSize(24);
        textView.setPadding(16, 16, 16, 16);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        textView.setBackgroundColor(getResources().getColor(R.color.theme_pink));
        textView.setTextColor(getResources().getColor(R.color.white));
        // You can customize TextView properties here (e.g., text size, padding, etc.)

        // Add TextView to the user list layout
        userListLayout.addView(textView);
    }
}
