package com.example.sharedcfc.ui.callinafavour;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.sharedcfc.DatabaseHelper;
import com.example.qrcodescanner.R;
import com.example.sharedcfc.ui.requests.RequestsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CallInAFavourFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private String spinnerItem;
    private String descriptionItem;
    private String[] userDetails;
    private EditText editTextDescription;
    private Button submitButton;
    private String firebaseUserToken;
    private DatabaseHelper databaseHelper;
    private Fragment fragment = null;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef = database.getReference("Users");
    private DatabaseReference requestsRef;
    private DatabaseReference newRequestRef;
    private String loggedInUserEmail;

    public static CallInAFavourFragment newInstance() {
        return new CallInAFavourFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_call_in_a_favour, container, false);
        databaseHelper = new DatabaseHelper(getActivity());
        userDetails = databaseHelper.fetchLocalInstance();
        loggedInUserEmail = userDetails[0];
        getFirebaseUserId();
        submitButton = (Button) v.findViewById(R.id.submit);
        editTextDescription = (EditText) v.findViewById(R.id.description1);
        String [] values = {"Type C Charger","MacBook 2017+ Charger","Broadband","Others"};
        Spinner spinner = (Spinner) v.findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, values);
        spinner.setPrompt("Select Option!");
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        submitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // do something
                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();
                descriptionItem=editTextDescription.getText().toString();
                editTextDescription.setText("");
                Log.d("Edittext",descriptionItem);
                Log.d("Spinner item", spinnerItem);
                HashMap<String, Object> userRequest = new HashMap<>();
                userRequest.put("RequesterEmail", userDetails[0]);
                userRequest.put("Requester", userDetails[2]);
                userRequest.put("RequestedItem",spinnerItem);
                userRequest.put("Description",descriptionItem);
                userRequest.put("TimeStamp",tsLong);
                userRequest.put("AccepterEmail","");
                userRequest.put("AccepterName","");
                userRequest.put("Status","Open");
                userRequest.put("Action","Incomplete");
                userRequest.put("AccepterMessage","");
                newRequestRef.setValue(userRequest);
                Toast.makeText(getActivity(),"Request Submitted",Toast.LENGTH_LONG).show();
//                fragment = new HomeFragment();
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                transaction.replace(R.id.nav_host_fragment, fragment);
//                transaction.addToBackStack(null);
//                transaction.commit();
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinnerItem=parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
    public void getFirebaseUserId(){
        Log.d("Firebase New", "inside getfirebaseuserid");
        usersRef.orderByChild("email").equalTo(loggedInUserEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("Firebase New", "inside getfirebaseuserid");
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Log.d("Firebase New", snapshot.getKey());
                    requestsRef = database.getReference("Requests/"+snapshot.getKey());
                    newRequestRef = requestsRef.push();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
