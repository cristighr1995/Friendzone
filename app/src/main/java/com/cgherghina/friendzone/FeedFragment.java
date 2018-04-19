package com.cgherghina.friendzone;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment {

    private ListView listView_feed;
    private FirebaseListAdapter feed_listAdapter;
    private String currentUser_latitude, currentUser_longitude;

    public FeedFragment() {
        // Required empty public constructor
    }

    private void readAndUpdateFriendsDatabase(final Set<String> friends) {
        FirebaseDatabase.getInstance().getReference("locations").getRef()
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User currentUser = null;
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                // get the current user data
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();

                    if (!key.equals(Profile.getCurrentProfile().getName()))
                        continue;

                    // get data from database
                    Map<String, Object> map = (Map<String, Object>) postSnapshot.getValue();

                    String username = (String) map.get("username");
                    String latitude = (String) map.get("latitude");
                    String longitude = (String) map.get("longitude");
                    String date = (String) map.get("hour");

                    currentUser = new User(username, latitude, longitude, date);
                }

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    if (!friends.contains(key))
                        continue;

                    // get data from database
                    Map<String, Object> map = (Map<String, Object>) postSnapshot.getValue();

                    String username = (String) map.get("username");
                    String latitude = (String) map.get("latitude");
                    String longitude = (String) map.get("longitude");
                    String date = (String) map.get("hour");

                    User u = new User(username, latitude, longitude, date);

                    mDatabase.child("friends").child(currentUser.username).child(u.username).setValue(u);
                    mDatabase.child("friends").child(u.username).child(currentUser.username).setValue(currentUser);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("GraphRequestFriends", "Failed to read from database", error.toException());
            }
        });
    }

    private void getFriendsLocation() {
        GraphRequest request = GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + Profile.getCurrentProfile().getId() +  "/friends",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        JSONObject graphObject = response.getJSONObject();
                        Set<String> friends = new HashSet<>();

                        try {
                            JSONArray data = graphObject.getJSONArray("data");

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject user = data.getJSONObject(i);

                                String friendName = user.getString("name");
                                friends.add(friendName);
                            }
                        }
                        catch (Exception e) {}

                        // after retrieving friends from Facebook API, get location from database
                        readAndUpdateFriendsDatabase(friends);
                    }
                });

        // need to execute it async
        request.executeAsync();
    }

    /**
     * Return the distance in meters between two geo coordinates
     * */
    public double distanceBetween(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return earthRadius * c;
    }

    private String getLastTime(String hour) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        try {
            Date d1 = format.parse(hour);

            //in milliseconds
            long diff = System.currentTimeMillis() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            if (diffDays != 0)
                return "" + diffDays + " days ago";
            if (diffHours != 0)
                return "" + diffHours + " hours ago";
            if (diffMinutes != 0)
                return "" + diffMinutes + " minutes ago";
            if (diffSeconds != 0)
                return "" + diffSeconds + " seconds ago";
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Set list view adapter to retrieve friends location from database
     */
    private void setListAdapter() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("friends").child(Profile.getCurrentProfile().getName())
                .limitToLast(50);

        feed_listAdapter = new FirebaseListAdapter<User>(getActivity(), User.class, R.layout.feed_item, query) {
            @Override
            protected void populateView(View v, User model, int position) {
                ((TextView)v.findViewById(R.id.textView_feed_username)).setText(model.username);

                if (currentUser_latitude != null && currentUser_longitude != null) {
                    double lat1 = Double.parseDouble(currentUser_latitude);
                    double lng1 = Double.parseDouble(currentUser_longitude);
                    double lat2 = Double.parseDouble(model.latitude);
                    double lng2 = Double.parseDouble(model.longitude);

                    double distanceInMeters = distanceBetween(lat1, lng1, lat2, lng2);

                    ((TextView) v.findViewById(R.id.textView_feed_distance)).setText("aprox " + String.format("%.2f", distanceInMeters) + " meters");
                }
                else {
                    ((TextView) v.findViewById(R.id.textView_feed_distance)).setText("exact position " + model.latitude + " " + model.longitude);
                }

                String lastTime = getLastTime(model.hour);

                ((TextView)v.findViewById(R.id.textView_feed_last_update)).setText(lastTime);
            }
        };

        listView_feed.setAdapter(feed_listAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_feed, container, false);

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        try {
                            String latitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LATITUDE);
                            String longitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LONGITUDE);

                            if (latitude != null && longitude != null) {
                                currentUser_latitude = latitude;
                                currentUser_longitude = longitude;

                                feed_listAdapter.notifyDataSetChanged();
                            }
                        }
                        catch(Exception e) {}
                    }
                }, new IntentFilter(LocationMonitoringService.ACTION_LOCATION_BROADCAST)
        );

        listView_feed = v.findViewById(R.id.listView_feed);
        setListAdapter();

        getFriendsLocation();

        return v;
    }

}
