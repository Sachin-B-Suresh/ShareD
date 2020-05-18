package com.example.qrcodescanner.ui.requests;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProviders;

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

import com.example.qrcodescanner.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RequestsFragment extends Fragment {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference requestsRef = database.getReference("Requests");
    List<String> name_array=new ArrayList<String>();
    List<String> requested_item_array=new ArrayList<String>();
    List<String> description_array=new ArrayList<String>();


    public static RequestsFragment newInstance() {
        return new RequestsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_requests, container, false);
        final RecyclerView recyclerView =view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        requestsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Log.d("firebase data",snapshot.child("Name").getValue().toString());
                    name_array.add(snapshot.child("Name").getValue().toString());
                    requested_item_array.add(snapshot.child("RequestedItem").getValue().toString());
                    description_array.add(snapshot.child("Description").getValue().toString());
                    Log.d("Content of list", String.valueOf(name_array.size()));
                }
                recyclerView.setAdapter(new RecyclerViewAdapter());
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

         public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        public RecyclerViewHolder(LayoutInflater inflater, ViewGroup container){
             super(inflater.inflate(R.layout.card_view, container,false));
             cardView = (CardView) itemView.findViewById(R.id.card_container);
            nameTextView = (TextView) itemView.findViewById(R.id.text_holder);
            emailTextView = (TextView) itemView.findViewById(R.id.email_holder);
            descriptionTextView = (TextView) itemView.findViewById(R.id.description_holder);
        }
    }
    private  class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder>{


        @NonNull
        @Override
        public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new RecyclerViewHolder(inflater,parent);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
            holder.nameTextView.setText("Name: " + name_array.get(position));
            holder.emailTextView.setText("Requested Item: " + requested_item_array.get(position));
            holder.descriptionTextView.setText("Description: " + description_array.get(position));
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

}
