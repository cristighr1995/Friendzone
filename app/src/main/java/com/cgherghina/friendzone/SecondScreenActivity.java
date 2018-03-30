package com.cgherghina.friendzone;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class SecondScreenActivity extends AppCompatActivity {

    private FragmentTransaction transaction;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_screen);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.action_feed:
                                //selectedFragment = ItemOneFragment.newInstance();
                                break;
                            case R.id.action_my_location:
                                //selectedFragment = ItemTwoFragment.newInstance();
                                break;
                            case R.id.action_messenger:
                                //selectedFragment = ItemThreeFragment.newInstance();
                                break;
                        }

                        transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.frameLayout_secondActivity, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });

        // start with my location
        Fragment locationFragment = new LocationFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout_secondActivity, locationFragment);
        transaction.commit();
    }
}
