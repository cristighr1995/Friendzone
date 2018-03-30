package com.cgherghina.friendzone.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.cgherghina.friendzone.R;

public class LoginFragment extends Fragment implements View.OnClickListener {
    private ImageButton button_loginWithFacebook;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        // Get Widgets refferences
        button_loginWithFacebook = v.findViewById(R.id.button_loginWithFacebook);

        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_loginWithFacebook:

                break;
            default:
                break;
        }
    }
}
