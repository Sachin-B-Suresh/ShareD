package com.example.sharedcfc.ui.myrequests;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.sharedcfc.DatabaseHelper;
import com.example.qrcodescanner.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyRequestsFragment extends Fragment {
    private DatabaseHelper databaseHelper;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference requestsRef = database.getReference("Requests");
    private DatabaseReference requestMessageRef;
    private String loggedInUserEmail, loggedInUserName;

    public static MyRequestsFragment newInstance() {
        return new MyRequestsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        databaseHelper= new DatabaseHelper(getActivity());
        loggedInUserEmail = databaseHelper.getEmail();
        loggedInUserName = databaseHelper.getName();
        View view =inflater.inflate(R.layout.fragment_requests, container, false);
        final RecyclerView recyclerView =view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        requestsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<String> name_array=new ArrayList<String>();
                final List<String> requested_item_array=new ArrayList<String>();
                final List<String> description_array=new ArrayList<String>();
                final List<String> requests_user_key=new ArrayList<String>();
                final List<String> accepter_array=new ArrayList<String>();
                final List<String> status_array=new ArrayList<String>();
                final List<String> requests_message_key=new ArrayList<String>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    requestMessageRef = database.getReference("Requests/"+snapshot.getKey());
                    Log.d("Hello ", requestMessageRef.toString());
                    for(DataSnapshot msnapshot : snapshot.getChildren()){
                        Log.d("Hello ", msnapshot.child("RequesterEmail").getValue().toString());
                        if(loggedInUserEmail.equals(msnapshot.child("RequesterEmail").getValue().toString())){
                            Log.d("firebase data",msnapshot.child("Requester").getValue().toString());
                            name_array.add(msnapshot.child("Requester").getValue().toString());
                            requested_item_array.add(msnapshot.child("RequestedItem").getValue().toString());
                            description_array.add(msnapshot.child("Description").getValue().toString());
                            requests_message_key.add(msnapshot.getKey());
                            accepter_array.add(msnapshot.child("AccepterName").getValue().toString());
                            status_array.add(msnapshot.child("Status").getValue().toString());
                            requests_user_key.add(snapshot.getKey());
                        }
                    }
                }
                //Reverse to make the firebase query in descending order
                Collections.reverse(name_array);
                Collections.reverse(requested_item_array);
                Collections.reverse(description_array);
                Collections.reverse(requests_user_key);
                Collections.reverse(requests_message_key);
                Collections.reverse(accepter_array);
                Collections.reverse(status_array);
                recyclerView.setAdapter(new MyRequestsFragment.RecyclerViewAdapter(name_array,requested_item_array,description_array,requests_user_key,accepter_array,status_array,requests_message_key));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        private TextView nameTextView;
        private TextView emailTextView;
        private TextView descriptionTextView;
        private TextView statusTextView;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        public RecyclerViewHolder(LayoutInflater inflater, ViewGroup container){
            super(inflater.inflate(R.layout.card_view, container,false));
            cardView = (CardView) itemView.findViewById(R.id.card_container);
            nameTextView = (TextView) itemView.findViewById(R.id.text_holder);
            emailTextView = (TextView) itemView.findViewById(R.id.email_holder);
            descriptionTextView = (TextView) itemView.findViewById(R.id.description_holder);
            statusTextView= (TextView) itemView.findViewById(R.id.status_container);
        }
    }

    private  class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder>{
        List<String> name_array=new ArrayList<String>();
        List<String> requested_item_array=new ArrayList<String>();
        List<String> description_array=new ArrayList<String>();
        List<String> requests_user_key=new ArrayList<String>();
        List<String> accepter_array=new ArrayList<String>();
        List<String> status_array=new ArrayList<String>();
        List<String> requests_message_key=new ArrayList<String>();
        public RecyclerViewAdapter(List<String> name_array, List<String> requested_item_array, List<String> description_array,
                                   List<String> requests_user_key, List<String> accepter_array, List<String> status_array,
                                   List<String> requests_message_key) {
            this.name_array=name_array;
            this.requested_item_array=requested_item_array;
            this.description_array=description_array;
            this.requests_user_key=requests_user_key;
            this.accepter_array=accepter_array;
            this.status_array=status_array;
            this.requests_message_key=requests_message_key;
        }

        @NonNull
        @Override
        public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new RecyclerViewHolder(inflater,parent);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewHolder holder, final int position) {
            holder.nameTextView.setText(name_array.get(position));
            holder.emailTextView.setText("Item: " + requested_item_array.get(position));
            holder.descriptionTextView.setText("Description: " + description_array.get(position));
            holder.statusTextView.setText("Status: " +status_array.get(position));
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cardViewOnClick(position,requests_user_key.get(position), requests_message_key.get(position),accepter_array.get(position),status_array.get(position));
                }
            });
        }

        @Override
        public int getItemCount() {
            return name_array.size();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
    }

    public void AcceptRequest(){
    }

    public void cardViewOnClick(final int position, final String userKey,final String messageKey, final String accepter, final String status){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //updateFirebase(position, childKey);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        if(status.equals("Active")){
            AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
            builder.setMessage(accepter + " has accepted to help!").setNegativeButton("Ok", dialogClickListener).show();
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
            builder.setMessage("Request is still open!").setNegativeButton("Ok", dialogClickListener).show();

        }
    }
//    public void updateFirebase(int position, String childKey){
//        HashMap<String, Object> update_request_array = new HashMap<>();
//        update_request_array.put("AccepterEmail", loggedInUserEmail);
//        update_request_array.put("Status", "Active");
//        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put("/" + childKey + "/AccepterEmail", loggedInUserEmail);
//        childUpdates.put("/" + childKey + "/Status", "Active");
//        requestsRef.updateChildren(childUpdates);
//    }
}
