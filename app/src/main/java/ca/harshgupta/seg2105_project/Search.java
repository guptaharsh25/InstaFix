package ca.harshgupta.seg2105_project;

import android.app.Dialog;
import android.app.SearchManager;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.util.Calendar;

public class Search extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mRootRef;
    private DatabaseReference mServicesRef;
    private DatabaseReference mUserRef;

    private ArrayList<String> keys;
    private ArrayList<String> userKeys;
    private String[] allKeys;
    private ArrayList<Integer> availabilityList;

    private AccountCustomAdapter accountAdapter;
    private ListView results;
    private SeekBar seekBar;

    private TextView day;
    private TextView start;
    private TextView end;


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
        availabilityList = new ArrayList<>();

        day = (TextView) findViewById(R.id.dayOfWeek);
        start = (TextView) findViewById(R.id.startTime);
        end = (TextView) findViewById(R.id.endTime);

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
                Integer[] daysArray = new Integer[availabilityList.size()];
                accountAdapter = new AccountCustomAdapter(Search.this, keys.toArray(keysArray), userKeys.toArray(userArray), availabilityList.toArray(daysArray));
                results = (ListView) findViewById(R.id.results);
                results.setAdapter(accountAdapter);
                accountAdapter.notifyDataSetChanged();
            }
        }, 500);
    }

    public void search(View view) {
        keys.clear();//Clear any previous history so results are not repeated
        userKeys.clear();
        availabilityList.clear();
        updateResultsList();

        final String serviceName = ((EditText) findViewById(R.id.txtSearch)).getText().toString().toLowerCase();
        final Double rate = (double) seekBar.getProgress();

        //Ask user for input if no inputs entered then display all services and providers

        if(allKeys!=null){
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
                                        day = (TextView) findViewById(R.id.dayOfWeek);
                                        start = (TextView) findViewById(R.id.startTime);
                                        end = (TextView) findViewById(R.id.endTime);

                                        String[] tempAvail = new String[3];
                                        tempAvail[0] = day.getText().toString();
                                        double initial = -1.0;
                                        double fin = -1.0;
                                        try{
                                            initial = Double.parseDouble(start.getText().toString());
                                            fin = Double.parseDouble(end.getText().toString());
                                        } catch (Exception e){}
                                        tempAvail[1] = Double.toString(initial);
                                        tempAvail[2] = Double.toString(fin);
                                        searchServicesProvided(post, key, rate, tempAvail);
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

    public void setAvailability(View view){
        LayoutInflater factory = LayoutInflater.from(this);
        view = factory.inflate(R.layout.add_availability_dialog, null);
        final AlertDialog addDialog = new AlertDialog.Builder(this).create();
        addDialog.setView(view);
        addDialog.show();

        final Availability availability = new Availability();
        //setContentView(R.layout.add_availability_dialog);

        addDialog.findViewById(R.id.btnSetStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(Search.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int iHour, int iMinute) {
                        //Get Time
                        //userInfo.child("Availability").child(type).child("Time").setValue(iHour+":"+iMinute);
                        availability.setTimeStart(String.format("%02d",iHour) + ":" + String.format("%02d",iMinute));
                        availability.setStartTimeDouble(iHour + (iMinute/100));
                        Button button = addDialog.findViewById(R.id.btnSetStart);
                        button.setText("Start: " + availability.getTimeStart());
                    }
                }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        addDialog.findViewById(R.id.btnSetEnd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(Search.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int iHour, int iMinute) {
                        //Get Time
                        //userInfo.child("Availability").child(type).child("Time").setValue(iHour+":"+iMinute);
                        availability.setTimeEnd(String.format("%02d",iHour) + ":" + String.format("%02d",iMinute));
                        availability.setEndTimeDouble(iHour + (iMinute/100));
                        Button button = addDialog.findViewById(R.id.btnSetEnd);
                        button.setText("End: " + availability.getTimeEnd());
                    }
                }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        addDialog.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(availability.getEndTimeDouble() < availability.getStartTimeDouble()){
                    Toast.makeText(Search.this, "The End Time is less than Start Time", Toast.LENGTH_LONG).show();
                } else{
                    Spinner spinner = addDialog.findViewById(R.id.spinner1);
                    availability.setDate(spinner.getSelectedItem().toString());
                    day.setText(availability.getDate());
                    start.setText(availability.getTimeStart());
                    end.setText(availability.getTimeEnd());
                    addDialog.dismiss();
                }
            }
        });

        addDialog.findViewById(R.id.btnDeleteAvailability).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day.setText("Date");
                start.setText("Start Time");
                end.setText("End Time");
            }
        });
    }

    private void searchRate(final String user, final String key, final double reqRate, final String[] avail){
        mUserRef.child(user).child("AverageRating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                    Double rating = dataSnapshot.getValue(Double.class);
                    //Look at availability
                    if(rating>=reqRate){
                        searchAvailability(user, key, avail[0], avail[1], avail[2]);
                    }
                } catch (Exception e){}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void searchServicesProvided(final String user, final String key, final double rate, final String[] avail){
        mUserRef.child(user).child("ProvidedServices").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot2 : dataSnapshot.getChildren()){
                    String post2 = postSnapshot2.getKey();
                    if(key.equals(post2)) {
                        /*Search for providers with rating > 0
                        If no rating is selected, min = 0 so all providers will display
                        */
                        searchRate(user, key, rate, avail);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void searchAvailability(final String user, final String key, final String date, final
    String tStart, final String tEnd){
        final double initial = Double.parseDouble(tStart);
        final double fin = Double.parseDouble(tEnd);
        System.out.println("-----"+date+"-----");
        if(date.equals("Day")){
            keys.add(key);
            userKeys.add(user);
            availabilityList.add(-1);
        }

        for(int i=0; i<=6; i++){
            final int pos = i;
            mUserRef.child(user).child("Availability").child(Integer.toString(i)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String day="";
                    double start = -1.0, end=-1.0;
                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        String value = postSnapshot.getValue().toString();
                        try{
                            if(postSnapshot.getKey().equals("Date")){
                                day = value;
                            } else if(postSnapshot.getKey().equals("Start Time")){
                                start = Double.parseDouble(value);
                            } else if(postSnapshot.getKey().equals("End Time")){
                                end = Double.parseDouble(value);
                            }

                        } catch (Exception e){}
                    }
                    if(day.equals(date)){
                        if(start>=initial && start<=fin){
                            keys.add(key);
                            userKeys.add(user);
                            availabilityList.add(pos);
                            return;
                        } else if(end>=initial && end<=fin){
                            keys.add(key);
                            userKeys.add(user);
                            availabilityList.add(pos);
                            return;
                        } else if(start<initial && end>=fin && start>=0){
                            keys.add(key);
                            userKeys.add(user);
                            availabilityList.add(pos);
                            return;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });
        }
    }

}
