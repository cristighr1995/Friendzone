package com.cgherghina.friendzone;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.Profile;

public class SecondScreenActivity extends AppCompatActivity implements View.OnClickListener {

    private FragmentTransaction transaction;

    private Profile currentProfile;

    private ImageView imageView_feed;
    private ImageView imageView_my_location;
    private ImageView imageView_messenger;
    private ImageView imageView_find;

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private boolean mAlreadyStartedLocationService = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_screen);

        currentProfile = Profile.getCurrentProfile();
        Toast.makeText(this, currentProfile.getFirstName(), Toast.LENGTH_LONG).show();

        imageView_feed = findViewById(R.id.imageView_feed);
        imageView_feed.setOnClickListener(this);
        imageView_my_location = findViewById(R.id.imageView_my_location);
        imageView_my_location.setOnClickListener(this);
        imageView_messenger = findViewById(R.id.imageView_messenger);
        imageView_messenger.setOnClickListener(this);
        imageView_find = findViewById(R.id.imageView_find);
        imageView_find.setOnClickListener(this);

        // start with my location
        Fragment locationFragment = new LocationFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout_secondActivity, locationFragment);
        transaction.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageView_feed:
                break;
            case R.id.imageView_my_location:
                break;
            case R.id.imageView_messenger:
                break;
            case R.id.imageView_find:
                break;
            default:
                break;
        }
    }
}
