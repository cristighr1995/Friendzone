package com.cgherghina.friendzone;


import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.Profile;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private ListView listView_chat;
    private FirebaseListAdapter chat_listAdapter;

    public ChatFragment() {
        // Required empty public constructor
    }

    private void setListAdapter() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("last_messages").child(Profile.getCurrentProfile().getName())
                .limitToLast(50);

        chat_listAdapter = new FirebaseListAdapter<ConversationItem>(getActivity(), ConversationItem.class, R.layout.chat_item, query) {
            @Override
            protected void populateView(View v, ConversationItem model, int position) {
                ((TextView)v.findViewById(R.id.textView_chat_receiver)).setText(model.receiver);

                ((TextView)v.findViewById(R.id.textView_chat_lastMessage)).setText(model.lastMessageText);

                ((TextView)v.findViewById(R.id.textView_chat_lastHour)).setText(model.lastHour);
            }
        };

        listView_chat.setAdapter(chat_listAdapter);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chat, container, false);

        listView_chat = v.findViewById(R.id.listView_chat);
        setListAdapter();

        listView_chat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // selected item
                String receiver = ((TextView) view.findViewById(R.id.textView_chat_receiver)).getText().toString();

                PrivateMessageFragment pm = new PrivateMessageFragment();
                Bundle bundle = new Bundle();
                bundle.putString("currentUser", Profile.getCurrentProfile().getName());
                bundle.putString("receiver", receiver);
                pm.setArguments(bundle);

                getActivity().getFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout_secondActivity, pm)
                        .commit();

                SecondScreenActivity.button_chat_writeMessage.setVisibility(View.GONE);
                SecondScreenActivity.button_chat_writeMessage.setEnabled(false);
            }
        });

        return v;
    }
}
