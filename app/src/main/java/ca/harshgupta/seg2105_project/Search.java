package ca.harshgupta.seg2105_project;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private String[] allKeys;
    Button search;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private ServiceCustomAdapter serviceAdapter;

    private Button addServices;
    private ListView serviceSPList;
    private TextView listTypeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        makeToast("Search.class");
        serviceSPList = (ListView) findViewById(R.id.results);

        mAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mServicesRef = mRootRef.child("Services");
        mUserRef = mRootRef.child("Accounts");
        instantiateAllKeys();

        search = (Button) findViewById(R.id.btnSearch);
        keys = new ArrayList<>();//List with positive search results

/*
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            search(query);
        }*/
    }

    //Get all keys under Users section in database
    private void instantiateAllKeys(){
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                allKeys = new String[(int) dataSnapshot.getChildrenCount()];
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    allKeys[i] = postSnapShot.getKey();
                    i++;
                }
                //updateServicesList();
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
    private void updateList(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                makeToast(keys.toString());
                String[] keysArray = new String[keys.size()];
                serviceAdapter = new ServiceCustomAdapter(Search.this, keys.toArray(keysArray));
                serviceSPList = (ListView) findViewById(R.id.results);
                serviceSPList.setAdapter(serviceAdapter);
                serviceAdapter.notifyDataSetChanged();
            }
        }, 1000);

    }

    public void search(View view) {
        final String name = ((EditText) findViewById(R.id.txtSearch)).getText().toString().toLowerCase();
        final Double rate;
        Double tempRate = null;
        try {
            tempRate = Double.parseDouble(((EditText) findViewById(R.id.rating)).getText().toString());
        } catch (Exception e){
            tempRate=0.0;
        } finally {
            rate = tempRate;
        }

        //Ask user for input if no inputs entered or entered incorrectly
        if(rate==0.0 && name.equals("")){
            makeToast("Please Enter At Least 1 Valid Search Query");
        }

        if(allKeys!=null){

            //search.setOnClickListener();
            for(final String key: allKeys){
                //Search queries if user is service provider
                mUserRef.child(key).child("UserType").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(0==0){//dataSnapshot.getValue().toString().equals("ServiceProvider")){
                            //Search first name
                            mUserRef.child(key).child("FirstName").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String query = dataSnapshot.getValue().toString(); //Value at key
                                    //If name is searched and is a substring of the datasnapshot, add it to list of keys
                                    if(query.toLowerCase().contains(name)||name.contains(query)){
                                        keys.add(key);
                                        makeToast("MATCH FOUND");
                                    }

                                }
                                public void onCancelled(@NonNull DatabaseError databaseError) {}

                            });

                            //Search last name
                            mUserRef.child(key).child("LastName").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String query = dataSnapshot.getValue().toString(); //Value at key
                                    //If name is searched and is a substring of the datasnapshot, add it to list of keys
                                    if(name!=null && query.contains(name)){
                                        keys.add(key);
                                    }

                                }

                                public void onCancelled(@NonNull DatabaseError databaseError) {}
                            });

                            //Search rate
                            /*mUserRef.child(key).child("AverageRating").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Double query = Double.parseDouble(dataSnapshot.getValue().toString()); //Value at key
                                    //If name is searched and is a substring of the datasnapshot, add it to list of keys
                                    if(rate!=null && query>=rate){
                                        keys.add(key);
                                    }

                                }

                                public void onCancelled(@NonNull DatabaseError databaseError) {}
                            });*/
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });

            }
        }
        updateList();
    }

    private void makeToast(String message){
        Toast.makeText(Search.this, message, Toast.LENGTH_SHORT).show();
    }

}
