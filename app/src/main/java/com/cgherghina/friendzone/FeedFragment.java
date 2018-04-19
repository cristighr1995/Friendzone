package com.cgherghina.friendzone;


import android.os.Bundle;
import android.app.Fragment;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment {

    private ListView listView_feed;
    private FirebaseListAdapter feed_listAdapter;

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
                ((TextView)v.findViewById(R.id.textView_feed_distance)).setText(model.latitude);
                ((TextView)v.findViewById(R.id.textView_feed_last_update)).setText(model.hour);
            }
        };

        listView_feed.setAdapter(feed_listAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_feed, container, false);

        listView_feed = v.findViewById(R.id.listView_feed);
        setListAdapter();

        getFriendsLocation();

        return v;
    }

}
