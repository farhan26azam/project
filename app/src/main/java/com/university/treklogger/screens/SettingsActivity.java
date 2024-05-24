package com.university.treklogger.screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.university.treklogger.R;
import com.university.treklogger.authentication.LoginActivity;

public class SettingsActivity extends Fragment {

    private Button logOut;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_settings, container, false);
        logOut = view.findViewById(R.id.logOutBtn);

        logOut.setOnClickListener(
                v -> logOut()
        );
        return view;
    }

    private void logOut(){
        // remove user from shared preferences
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        editor.commit();
        editor = getActivity().getSharedPreferences("name", AppCompatActivity.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        editor.commit();
        editor = getActivity().getSharedPreferences("email", AppCompatActivity.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        editor.commit();
        editor = getActivity().getSharedPreferences("photo", AppCompatActivity.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        editor.commit();

        // navigate to login activity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }
}
