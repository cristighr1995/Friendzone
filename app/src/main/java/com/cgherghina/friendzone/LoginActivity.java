package com.cgherghina.friendzone;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cgherghina.friendzone.Fragments.LoginFragment;

public class LoginActivity extends AppCompatActivity {

    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Fragment chooseLoginFragment = new LoginFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout_login, chooseLoginFragment);
        transaction.commit();
    }
}
