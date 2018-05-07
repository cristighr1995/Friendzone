package com.cgherghina.friendzone;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class WriteMessageFragment extends Fragment {

    private Spinner spinner_chat_choose_friend;
    private EditText editText_chat_message;
    private Button button_chat_send_first;

    public WriteMessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_write_message, container, false);

        editText_chat_message = v.findViewById(R.id.editText_chat_message);
        button_chat_send_first = v.findViewById(R.id.button_chat_send_first);
        button_chat_send_first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentUsername = Profile.getCurrentProfile().getName();
                String receiver = spinner_chat_choose_friend.getSelectedItem().toString();
                String messageToSend = editText_chat_message.getText().toString();

                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String date = sdf.format(Calendar.getInstance().getTime());

                /** Used for updating the conversation item used to retrieve the last messages */
                mDatabase.child("last_messages").child(currentUsername).child(receiver)
                        .setValue(new ConversationItem(receiver, messageToSend, date));

                mDatabase.child("last_messages").child(receiver).child(currentUsername)
                        .setValue(new ConversationItem(currentUsername, messageToSend, date));

                mDatabase.child("messages").child(currentUsername).child(receiver)
                        .push().setValue(new Message(currentUsername, messageToSend, date));

                mDatabase.child("messages").child(receiver).child(currentUsername)
                        .push().setValue(new Message(currentUsername, messageToSend, date));

                PrivateMessageFragment pm = new PrivateMessageFragment();
                Bundle bundle = new Bundle();
                bundle.putString("currentUser", currentUsername);
                bundle.putString("receiver", receiver);
                pm.setArguments(bundle);

                getActivity().getFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout_secondActivity, pm)
                        .commit();
            }
        });

        spinner_chat_choose_friend = v.findViewById(R.id.spinner_chat_choose_friend);
        final List<String> friendsNames = new ArrayList<>();

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(v.getContext(),
                android.R.layout.simple_spinner_dropdown_item, friendsNames);

        //set the spinners adapter to the previously created one.
        spinner_chat_choose_friend.setAdapter(adapter);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


        String currentUser = Profile.getCurrentProfile().getName();

        mDatabase.child("friends").child(currentUser).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get the current user data
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();

                    // get data from database
                    Map<String, Object> map = (Map<String, Object>) postSnapshot.getValue();

                    String username = (String) map.get("username");
                    /*String latitude = (String) map.get("latitude");
                    String longitude = (String) map.get("longitude");
                    String date = (String) map.get("hour");*/

                    friendsNames.add(username);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

        return v;
    }

}
