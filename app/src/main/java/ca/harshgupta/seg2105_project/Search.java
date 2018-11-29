package ca.harshgupta.seg2105_project;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Search extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mRootRef;
    private DatabaseReference mServicesRef;
    private DatabaseReference mUserRef;

    private ArrayList<String> keys;
    private ArrayList<String> userKeys;
    private String[] allKeys;

    private AccountCustomAdapter accountAdapter;
    private ListView results;
    private SeekBar seekBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        results = (ListView) findViewById(R.id.results);
        seekBar = (SeekBar) findViewById(R.id.rating);

        mAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mServicesRef = mRootRef.child("Services");
        mUserRef = mRootRef.child("Accounts");
        instantiateAllKeys();

        keys = new ArrayList<>();//List with positive search results
        userKeys = new ArrayList<>();

/*
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            search(query);
        }*/
    }

    //Get all keys under Users section in database
    private void instantiateAllKeys(){
        mServicesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                allKeys = new String[(int) dataSnapshot.getChildrenCount()];
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    allKeys[i] = postSnapShot.getKey();
                    i++;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        //serviceAdapter = new ServiceCustomAdapter(Search.this, allKeys);
        //serviceSPList = (ListView) findViewById(R.id.results);
        //serviceSPList.setAdapter(serviceAdapter);
        //serviceAdapter.notifyDataSetChanged();
    }

    //Display search results
    private void updateResultsList(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String[] keysArray = new String[keys.size()];
                String[] userArray = new String[userKeys.size()];
                accountAdapter = new AccountCustomAdapter(Search.this, keys.toArray(keysArray), userKeys.toArray(userArray));
                results = (ListView) findViewById(R.id.results);
                results.setAdapter(accountAdapter);
                accountAdapter.notifyDataSetChanged();
            }
        }, 500);
    }

    public void search(View view) {
        keys.clear();//Clear any previous history so results are not repeated
        userKeys.clear();
        updateResultsList();

        final String serviceName = ((EditText) findViewById(R.id.txtSearch)).getText().toString().toLowerCase();
        final Double rate = (double) seekBar.getProgress();

        //Ask user for input if no inputs entered then display all services and providers

        if(allKeys!=null){
            final int pos = 0;
            for(final String key: allKeys){
                //Search all services
                mServicesRef.child(key).child("name").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String query = dataSnapshot.getValue().toString(); //Value at key
                        //If name is searched and is a substring of the datasnapshot, add it to list of keys
                        if(query.toLowerCase().contains(serviceName)||serviceName.contains(query)){
                            mUserRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                                        final String post = postSnapshot.getKey(); //User
                                        //Search user's list of provided services to see if the query key matches
                                        searchServicesProvided(post, key, rate);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {}
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
            }
        }

        updateResultsList();
    }

    private void makeToast(String message){
        Toast.makeText(Search.this, message, Toast.LENGTH_SHORT).show();
    }

    private void searchRate(final String user, final String key, final double reqRate){
        mUserRef.child(user).child("AverageRating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                    Double rating = dataSnapshot.getValue(Double.class);
                    System.out.print(rating);
                    if(rating>=reqRate){
                        keys.add(key);
                        userKeys.add(user);
                    }
                } catch (Exception e){
                    if(dataSnapshot.getValue(Double.class)==null){
                        Double rating = 0.0;
                        if(rating>=reqRate){
                            keys.add(key);
                            userKeys.add(user);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void searchServicesProvided(final String user, final String key, final double rate){
        mUserRef.child(user).child("ProvidedServices").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot2 : dataSnapshot.getChildren()){
                    String post2 = postSnapshot2.getKey();
                    if(key.equals(post2)) {
                        /*Search for providers with rating > 0
                        If no rating is selected, min = 0 so all providers will display
                        */
                        searchRate(user, key, rate);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

}
