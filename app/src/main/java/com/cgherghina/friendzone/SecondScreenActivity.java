package com.cgherghina.friendzone;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.login.LoginManager;

public class SecondScreenActivity extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private FragmentTransaction transaction;

    private Profile currentProfile;

    private RelativeLayout layout_feed;
    private RelativeLayout layout_my_location;
    private RelativeLayout layout_messenger;
    private RelativeLayout layout_find;

    private RelativeLayout lastLayoutClicked;

    public static FloatingActionButton button_chat_writeMessage;

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private boolean mAlreadyStartedLocationService = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_screen);

        currentProfile = Profile.getCurrentProfile();
        Toast.makeText(this, currentProfile.getFirstName(), Toast.LENGTH_LONG).show();
        String userId = currentProfile.getId();

        Log.e("user_id", currentProfile.getId());

        layout_feed = findViewById(R.id.layout_feed);
        layout_feed.setOnClickListener(this);
        layout_my_location = findViewById(R.id.layout_my_location);
        layout_my_location.setOnClickListener(this);
        layout_messenger = findViewById(R.id.layout_messenger);
        layout_messenger.setOnClickListener(this);
        //layout_find = findViewById(R.id.layout_find);
        //layout_find.setOnClickListener(this);

        button_chat_writeMessage = findViewById(R.id.button_chat_writeMessage);
        button_chat_writeMessage.setOnClickListener(this);

        // start with my location
        startFragment(new LocationFragment());

        lastLayoutClicked = layout_my_location;
        setBackgroundFocus(lastLayoutClicked);


    }

    private void startFragment(Fragment fragment) {
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout_secondActivity, fragment);
        transaction.commit();
    }

    private void setBackgroundFocus(View view) {
        RelativeLayout rl = (RelativeLayout) view;

        if (rl.getId() == R.id.layout_messenger) {
            button_chat_writeMessage.setVisibility(View.VISIBLE);
            button_chat_writeMessage.setEnabled(true);
        }
        else {
            button_chat_writeMessage.setVisibility(View.GONE);
            button_chat_writeMessage.setEnabled(false);
        }

        // reset last clicked to default
        lastLayoutClicked.setBackgroundColor(getResources().getColor(R.color.colorAppBackground));

        // focus current image view
        rl.setBackgroundColor(getResources().getColor(R.color.colorFocusedMenuBackground));

        lastLayoutClicked = rl;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_feed:
                setBackgroundFocus(view);
                startFragment(new FeedFragment());
                break;

            case R.id.layout_my_location:
                setBackgroundFocus(view);
                startFragment(new LocationFragment());
                break;

            case R.id.layout_messenger:
                setBackgroundFocus(view);
                startFragment(new ChatFragment());

                break;

            /*case R.id.layout_find:
                setBackgroundFocus(view);
                break;*/

            case R.id.button_chat_writeMessage:
                startFragment(new WriteMessageFragment());
                button_chat_writeMessage.setVisibility(View.GONE);
                button_chat_writeMessage.setEnabled(false);
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.app_bar_popup_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.button_popUp) {
            showPopUpMenu(findViewById(id));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showPopUpMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.app_bar_settings_items);
        popup.show();
    }

    private void logOutFromFacebook() {
        /** Log out the current user */
        LoginManager.getInstance().logOut();

        /** Stop the location monitoring service */
        Intent myService = new Intent(SecondScreenActivity.this, LocationMonitoringService.class);
        stopService(myService);

        /** Go back to the login activity */
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.button_logOutFacebook:
                logOutFromFacebook();
                return true;

            default:
                return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // stop the service
        getApplicationContext().stopService(new Intent(getApplicationContext(), LocationMonitoringService.class));
    }
}
