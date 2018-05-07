package com.cgherghina.friendzone;


import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.Profile;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class PrivateMessageFragment extends Fragment {

    private String currentUser;
    private String receiver;

    private ListView listView_private_message;
    private EditText editText_private_message;
    private Button button_private_message_send;
    private TextView textView_private_message_title_friend;

    private FirebaseListAdapter chat_listAdapter;

    public PrivateMessageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            currentUser = bundle.getString("currentUser");
            receiver = bundle.getString("receiver");
        }
    }

    private void setListAdapter() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("messages").child(currentUser).child(receiver)
                .limitToLast(50);

        chat_listAdapter = new FirebaseListAdapter<Message>(getActivity(), Message.class, R.layout.private_chat_item, query) {
            @Override
            protected void populateView(View v, Message model, int position) {
                ((TextView)v.findViewById(R.id.textView_private_receiver)).setText(model.sender);

                ((TextView)v.findViewById(R.id.textView_private_message)).setText(model.messageText);

                ((TextView)v.findViewById(R.id.textView_private_hour)).setText(model.hour);
            }
        };

        listView_private_message.setAdapter(chat_listAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_private_message, container, false);

        listView_private_message = v.findViewById(R.id.listView_private_message);
        editText_private_message = v.findViewById(R.id.editText_private_message);
        button_private_message_send = v.findViewById(R.id.button_private_message_send);

        textView_private_message_title_friend = v.findViewById(R.id.textView_private_message_title_friend);
        textView_private_message_title_friend.setText(receiver);

        setListAdapter();

        button_private_message_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageToSend = editText_private_message.getText().toString();

                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String date = sdf.format(Calendar.getInstance().getTime());

                /** Used for updating the conversation item used to retrieve the last messages */
                mDatabase.child("last_messages").child(currentUser).child(receiver)
                        .setValue(new ConversationItem(receiver, messageToSend, date));

                mDatabase.child("last_messages").child(receiver).child(currentUser)
                        .setValue(new ConversationItem(currentUser, messageToSend, date));

                mDatabase.child("messages").child(currentUser).child(receiver)
                        .push().setValue(new Message(currentUser, messageToSend, date));

                mDatabase.child("messages").child(receiver).child(currentUser)
                        .push().setValue(new Message(currentUser, messageToSend, date));

                // clear the edit text
                editText_private_message.setText("");
            }
        });

        return v;
    }

}
