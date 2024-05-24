package com.university.treklogger.components;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.university.treklogger.MainActivity;
import com.university.treklogger.R;
import com.university.treklogger.screens.PeopleActivity;
import com.university.treklogger.screens.TreksActivity;

// create a bottom bar class that can be used as bottom bar  on other activities, the layout is in r.layout.bottom_bar

public class BottomBar extends View {
    private Context context;
    private View view;

    FloatingActionButton home, treks, startTrek, notifications, settings;

    public BottomBar(Context context, View view) {
        super(context);
        this.context = context;
        this.view = view;
        home = view.findViewById(R.id.home);
        treks = view.findViewById(R.id.locations);
        startTrek = view.findViewById(R.id.navigate);
        notifications = view.findViewById(R.id.notifications);
        settings = view.findViewById(R.id.settings);

        home.setOnClickListener(v -> {
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        });

        treks.setOnClickListener(v -> {
            Intent intent = new Intent(context, TreksActivity.class);
            context.startActivity(intent);
        });

        startTrek.setOnClickListener(v -> {
            Intent intent = new Intent(context, TreksActivity.class);
            context.startActivity(intent);
        });

        notifications.setOnClickListener(v -> {
            Intent intent = new Intent(context, PeopleActivity.class);
            context.startActivity(intent);
        });
    }

    public void setBottomBar() {
        view.findViewById(R.id.bottom_bar).setVisibility(View.VISIBLE);

    }
}
