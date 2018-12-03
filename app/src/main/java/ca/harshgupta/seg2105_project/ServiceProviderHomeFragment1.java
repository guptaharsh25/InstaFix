package ca.harshgupta.seg2105_project;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ServiceProviderHomeFragment1 extends Fragment {
    private FirebaseUser user;
    private DatabaseReference userInfo;
    private DatabaseReference mAvailable;

    private ListAdapter availabilityAdapter;

    private TextView availabilityText;
    private ListView listAvailabilities;
    private DatabaseReference mUser;

    private List<String> keys;
    private String[] keyArray;
    //private final String[] keysAvailability = {"0","1","2","3","4","5","6"};
    View myView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_service_provider_home, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        keys = new ArrayList<String>();

        mAvailable = FirebaseDatabase.getInstance().getReference().child("Accounts").child(user.getUid()).child("Availability");
        listAvailabilities = (ListView) myView.findViewById(R.id.listAvailabilites2);
        instantiateKeys();
        //setAvailabilityAdapter();
        return myView;
    }

    public void onStart (){
        super.onStart();

        availabilityText = myView.findViewById(R.id.headerAvailabilities2);

        final TextView welcomeText = (TextView) myView.findViewById(R.id.txtWelcome2);
        final TextView roleText = (TextView) myView.findViewById(R.id.txtRole2);
        final TextView avgRateText = (TextView) myView.findViewById(R.id.txtAverageRate);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userInfo = FirebaseDatabase.getInstance().getReference().child("Accounts")
                .child(user.getUid());

        userInfo.child("FirstName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.getValue().toString();
                welcomeText.setText("Welcome " + userName + "!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError);
            }
        });

        userInfo.child("UserType").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userType = dataSnapshot.getValue().toString();
                roleText.setText("You are logged in as " + userType);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError);
            }
        });

        userInfo.child("AverageRating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userType = dataSnapshot.getValue().toString();
                avgRateText.setText("Average rate of service is " + userType);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError);
            }
        });
    }

    private void instantiateKeys(){
        keys.clear();
        mAvailable.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot uniqueKeySnapShot: dataSnapshot.getChildren()){
                    keys.add(uniqueKeySnapShot.getKey());
                }
                setServiceProviderAvailabilityAdapter();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        /*
        mAvailable.child("1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot uniqueKeySnapShot: dataSnapshot.getChildren()){
                    keys.add(uniqueKeySnapShot.getKey());
                }
                setServiceProviderAvailabilityAdapter();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        mAvailable.child("2").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot uniqueKeySnapShot: dataSnapshot.getChildren()){
                    keys.add(uniqueKeySnapShot.getKey());
                }
                setServiceProviderAvailabilityAdapter();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        keys.clear();
        keys.clear();
        keys.clear();
        mAvailable.child("3").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot uniqueKeySnapShot: dataSnapshot.getChildren()){
                    keys.add(uniqueKeySnapShot.getKey());
                }
                setServiceProviderAvailabilityAdapter();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        keys.clear();
        mAvailable.child("4").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot uniqueKeySnapShot: dataSnapshot.getChildren()){
                    keys.add(uniqueKeySnapShot.getKey());
                }
                setServiceProviderAvailabilityAdapter();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        keys.clear();
        mAvailable.child("5").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot uniqueKeySnapShot: dataSnapshot.getChildren()){
                    keys.add(uniqueKeySnapShot.getKey());
                }
                setServiceProviderAvailabilityAdapter();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        keys.clear();
        mAvailable.child("6").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot uniqueKeySnapShot: dataSnapshot.getChildren()){
                    keys.add(uniqueKeySnapShot.getKey());
                }
                setServiceProviderAvailabilityAdapter();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });*/
    }

    private void setServiceProviderAvailabilityAdapter () {
        keyArray = new String[keys.size()];
        keyArray = keys.toArray(keyArray);
        new ServiceProviderHomeFragment1.addAvailabilityAsyncTask().execute();
    }

    private class addAvailabilityAsyncTask extends AsyncTask<Void, String, String> {
        @Override
        protected void onPreExecute() {
            availabilityAdapter = new AvailabilityCustomAdapter(getActivity(), keyArray);
            listAvailabilities.setAdapter(availabilityAdapter);
        }
        @Override
        protected String doInBackground(Void... voids) {
            for(String info : keyArray){
                publishProgress(info);
            }
            return "All the Information was Loaded Succesfully";
        }
        @Override
        protected void onProgressUpdate(String... values){
        }
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
        }
    }
    /*public void setAvailabilityAdapter(){
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                availabilityAdapter = new AvailabilityCustomAdapter(getActivity(), keysAvailability);
                listAvailabilities = (ListView) myView.findViewById(R.id.listAvailabilites);
                listAvailabilities.setAdapter(availabilityAdapter);
                //availabilityAdapter.notifyDataSetChanged();
            }
        },500); //1000ms = 1sec
    }*/
}

