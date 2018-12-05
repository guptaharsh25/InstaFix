package ca.harshgupta.seg2105_project;

import android.app.Dialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class ClientHomeFragment2 extends Fragment {

    View myView;

    private FirebaseUser user;
    private DatabaseReference userInfo;

    private DatabaseReference mRootRef;
    private DatabaseReference mServicesRef;
    private DatabaseReference mUserRef;
    private DatabaseReference orderRef;

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

    private Button search;
    private Button calendar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_client_home_2, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userInfo = FirebaseDatabase.getInstance().getReference().child("Accounts")
                .child(user.getUid());

        results = (ListView) myView.findViewById(R.id.results);
        seekBar = (SeekBar) myView.findViewById(R.id.rating);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mServicesRef = mRootRef.child("Services");
        mUserRef = mRootRef.child("Accounts");
        instantiateAllKeys();

        orderRef = mRootRef.child("Appointment");

        keys = new ArrayList<>();//List with positive search results
        userKeys = new ArrayList<>();
        availabilityList = new ArrayList<>();

        day = (TextView) myView.findViewById(R.id.dayOfWeek);
        start = (TextView) myView.findViewById(R.id.startTime);
        end = (TextView) myView.findViewById(R.id.endTime);

        search = (Button) myView.findViewById(R.id.btnSearch);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });

        calendar = (Button) myView.findViewById(R.id.btnCalendar);
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAvailability();
            }
        });
        booking();
        return myView;
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
    }

    //Display search results
    private void updateResultsList(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String[] keysArray = new String[keys.size()];
                String[] userArray = new String[userKeys.size()];
                Integer[] daysArray = new Integer[availabilityList.size()];
                accountAdapter = new AccountCustomAdapter(myView.getContext(), keys.toArray(keysArray), userKeys.toArray(userArray), availabilityList.toArray(daysArray));
                results = (ListView) myView.findViewById(R.id.results);
                results.setAdapter(accountAdapter);
                accountAdapter.notifyDataSetChanged();
            }
        }, 500);
    }

    public void search() {
        keys.clear();//Clear any previous history so results are not repeated
        userKeys.clear();
        availabilityList.clear();
        updateResultsList();

        final String serviceName = ((EditText) myView.findViewById(R.id.txtSearch)).getText().toString().toLowerCase();
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
                                        day = (TextView) myView.findViewById(R.id.dayOfWeek);
                                        start = (TextView) myView.findViewById(R.id.startTime);
                                        end = (TextView) myView.findViewById(R.id.endTime);

                                        String[] tempAvail = new String[3];
                                        tempAvail[0] = day.getText().toString();
                                        double initial = -1.0;
                                        double fin = -1.0;
                                        try{
                                            initial = Double.parseDouble(start.getText().toString
                                                    ().substring(0,2) +
                                                    start.getText().toString().substring(4));
                                            fin = Double.parseDouble(end.getText().toString
                                                    ().substring(0,2) +
                                                    end.getText().toString().substring(4));
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

    public void setAvailability(){
        LayoutInflater factory = LayoutInflater.from(myView.getContext());
        View viewAvailability = factory.inflate(R.layout.add_availability_dialog, null);
        final AlertDialog addDialog = new AlertDialog.Builder(myView.getContext()).create();
        addDialog.setView(viewAvailability);
        addDialog.show();

        Button search = addDialog.findViewById(R.id.btnAdd);
        Button clear = addDialog.findViewById(R.id.btnDeleteAvailability);
        search.setText("Search");
        clear.setText("Clear");

        final Availability availability = new Availability();
        //setContentView(R.layout.add_availability_dialog);

        addDialog.findViewById(R.id.btnSetStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(myView.getContext(), new TimePickerDialog.OnTimeSetListener() {
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

                TimePickerDialog timePickerDialog = new TimePickerDialog(myView.getContext(), new TimePickerDialog.OnTimeSetListener() {
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
                    Toast.makeText(myView.getContext(), "The End Time is less than Start Time", Toast.LENGTH_LONG).show();
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
                day.setText("Day");
                start.setText("Start Time");
                end.setText("End Time");
                addDialog.dismiss();
            }
        });
    }

    private void searchRate(final String user, final String key, final double reqRate, final
    String[] avail){
        final String serviceName = ((EditText) myView.findViewById(R.id.txtSearch)).getText().toString().toLowerCase();
        Double rate = (double) seekBar.getProgress();
        mUserRef.child(user).child("AverageRating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                    Double rating = dataSnapshot.getValue(Double.class);
                    //Look at availability
                    if(rating>=reqRate){
                        searchAvailability(user, key, avail[0], avail[1], avail[2]);
                    }
                } catch (Exception e){
                    if(avail[0].equals("Day") && serviceName.isEmpty() && reqRate==0.0){
                        searchAvailability(user, key, avail[0], avail[1], avail[2]);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void searchServicesProvided(final String user, final String key, final double rate,
                                        final String[] avail){
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
        //Convert time to decimal
        final double initial = Double.parseDouble(tStart.substring(0,2) + "."
                + tStart.substring(4));
        final double fin = Double.parseDouble(tEnd.substring(0,2) + "." + tStart.substring(4));

        //If no availability searched
        if(date.equals("Day")){
            keys.add(key);
            userKeys.add(user);
            availabilityList.add(-1);
            return;
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
                                start = Double.parseDouble(value.substring(0,
                                        2)+"."+value.substring(3));
                            } else if(postSnapshot.getKey().equals("End Time")){
                                end = Double.parseDouble(value.substring(0,2)+"."+value.substring
                                        (3));
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

    private void booking(){
        if(keys!=null) {
            updateResultsList();
            String[] keysArray = new String[keys.size()];
            final String[] userArray = new String[userKeys.size()];
            Integer[] daysArray = new Integer[availabilityList.size()];
            accountAdapter = new AccountCustomAdapter(myView.getContext(), keys.toArray(keysArray), userKeys.toArray(userArray), availabilityList.toArray(daysArray));
            results = (ListView) myView.findViewById(R.id.results);
            results.setAdapter(accountAdapter);
            accountAdapter.notifyDataSetChanged();

            results.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    final String serviceName = ((TextView) view.findViewById(R.id.serviceName)).
                            getText().toString();
                    final String[] name = ((TextView) view.findViewById(R.id.name)).getText().
                            toString().split(" ");
                    System.out.println(name[0] +" "+name[1]);

                    final String dayOfWeek = ((TextView) view.findViewById(R.id.day)).getText()
                            .toString();
                    final String[] info = new String[8];

                    info[0] = user.getUid().toString();

                    //Do only when availability searched
                    if(!dayOfWeek.equals("")){
                        final String startTime = ((TextView) view.findViewById(R.id.timing)).
                                getText().toString().substring(0, 5);
                        final String endTime = ((TextView) view.findViewById(R.id.timing)).
                                getText().toString().substring(8);

                        //Lists for saving first, last names and keys
                        final ArrayList<String> first = new ArrayList<>();
                        final ArrayList<String> last = new ArrayList<>();
                        final ArrayList<String> providerKeys = new ArrayList<>();

                        //Search all users keys and save all first, last names and keys to list
                        for(final String providerKey : userKeys){
                            mUserRef.child(providerKey).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot post : dataSnapshot.getChildren()){
                                        providerKeys.add(providerKey);
                                        if(post.getKey().equals("FirstName")){
                                            first.add(post.getValue().toString());
                                        } else if(post.getKey().equals("LastName")) {
                                            last.add(post.getValue().toString());
                                            System.out.println(post.getValue().toString());
                                        }
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {}
                            });
                        }

                        //Find service key matching service name in listview
                        for(final String serviceKey : keys){
                            mServicesRef.child(serviceKey).child("name").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.getValue().toString().equals(serviceName)){
                                        //Save service key it match found
                                        info[2] = serviceKey;
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {}
                            });
                        }

                        //Save all info
                        info[3] = dayOfWeek+"day";
                        info[4] = startTime;
                        info[5] = endTime;
                        info[6] = "Pending";
                        info[7] = "-";

                        //Create dialog to ask user for booking
                        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                        alertDialog.setTitle("Booking");
                        alertDialog.setButton(Dialog.BUTTON_POSITIVE,"Book", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                createOrder(info);

                            }
                        });
                        alertDialog.setButton(Dialog.BUTTON_NEGATIVE,"Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        //Cause delay to load al data
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //If first and last name in list match name in listview, use that key
                                for(int j=0; j<first.size(); j++){
                                    System.out.println(first.get(j)+" "+last.get(j));
                                    if(first.get(j).equals(name[0]) && last.get(j).equals(name[1])){
                                        info[1] = providerKeys.get(j);
                                        System.out.println(info[1]+"*****");
                                    }
                                }
                                alertDialog.setMessage("Would you like to book the service, " +
                                        serviceName + " with " + name[0] + " " + name[1] + " on " +
                                        info[3] + " from " + info[4] + " - " + info[5] + "?");
                                alertDialog.show();
                            }
                        }, 800);

                    }
                }
            });
        }
    }

    private void createOrder(String[] info){
        //Create order key
        String orderID = orderRef.push().getKey();
        DatabaseReference order = orderRef.child(orderID);
        //Set values
        order.child("ClientID").setValue(info[0]);
        order.child("SPID").setValue(info[1]);
        order.child("Service").setValue(info[2]);
        order.child("Date").setValue(info[3]);
        order.child("StartTime").setValue(info[4]);
        order.child("EndTime").setValue(info[5]);
        order.child("OrderStatus").setValue(info[6]);
        order.child("Rating").setValue(info[7]);
        //Save order key under client and service provider
        mUserRef.child(info[0]).child("Orders").child(orderID).setValue(info[6]);
        mUserRef.child(info[1]).child("Orders").child(orderID).setValue(info[6]);
    }
}
