package com.cgherghina.friendzone.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cgherghina.friendzone.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

public class LoginFragment extends Fragment implements View.OnClickListener {
    private LoginButton button_loginWithFacebook;
    private CallbackManager callbackManager;
    private static final String EMAIL = "email";

    private boolean loggedIn = false;

    public void registerFacebookManager(View v) {
        callbackManager = CallbackManager.Factory.create();

        button_loginWithFacebook = (LoginButton) v.findViewById(R.id.button_loginWithFacebook);
        button_loginWithFacebook.setReadPermissions(Arrays.asList(EMAIL));
        button_loginWithFacebook.setFragment(this);

        // Callback registration
        button_loginWithFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Toast.makeText(getActivity().getApplicationContext(), "Loged in with facebook", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loggedIn = AccessToken.getCurrentAccessToken() == null;
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        registerFacebookManager(v);

        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
