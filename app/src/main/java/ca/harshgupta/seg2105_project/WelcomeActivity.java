package ca.harshgupta.seg2105_project;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.util.Calendar;

public class WelcomeActivity extends AppCompatActivity {
    private FirebaseUser user;
    private DatabaseReference userInfo;

    private Button addAvailability;
    private Button setStartTime;
    private Button setEndTime;
    private Button dialogAddAvailability;

    private TextView startText;
    private TextView endText;
    private TextView availabilityText;

    private AvailabilityCustomAdapter availabilityAdapter;
    private ListView listAvailabilities;

    private final String[] keysAvailability = {"0","1","2","3","4","5","6"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.availability_list_layout);
        listAvailabilities = (ListView) findViewById(R.id.listAvailabilites);
        setContentView(R.layout.activity_welcome);

        addAvailability = findViewById(R.id.btnAddAvailability);
        availabilityText = findViewById(R.id.headerAvailabilities);

        user = FirebaseAuth.getInstance().getCurrentUser();
        final TextView welcomeText = (TextView) findViewById(R.id.txtWelcome);
        final TextView roleText = (TextView) findViewById(R.id.txtRole);
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
                if(userType.equals("Client")){
                    Intent intentClientHome = new Intent(getApplicationContext(), ClientHome.class);
                    startActivityForResult(intentClientHome,0);
                } else if (!userType.equals("ServiceProvider")){
                    //btnTimeStart.setVisibility(View.GONE);
                    addAvailability.setVisibility(View.GONE);
                    availabilityText.setVisibility(View.GONE);
                    listAvailabilities.setVisibility(View.GONE);

                } else if(userType.equals("Client")){
                    Intent intentClientHome = new Intent(getApplicationContext(), ClientHome.class);
                    startActivityForResult(intentClientHome,0);
                }

                else if (userType.equals("ServiceProvider")){
                    addAvailability.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError);
            }
        });
        setAvailabilityAdapter();
    }

    public void onStart (){
        super.onStart();
        userInfo.child("Availability").child(Integer.toString(0)).child("Date").setValue("Sunday");
        userInfo.child("Availability").child(Integer.toString(1)).child("Date").setValue("Monday");
        userInfo.child("Availability").child(Integer.toString(2)).child("Date").setValue("Tuesday");
        userInfo.child("Availability").child(Integer.toString(3)).child("Date").setValue("Wednesday");
        userInfo.child("Availability").child(Integer.toString(4)).child("Date").setValue("Thursday");
        userInfo.child("Availability").child(Integer.toString(5)).child("Date").setValue("Friday");
        userInfo.child("Availability").child(Integer.toString(6)).child("Date").setValue("Saturday");

    }
    public void onSignOut (View view){
        FirebaseAuth.getInstance().signOut();
        Intent intentToSignOut = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(intentToSignOut,0);
    }

    public void openServices(View view){
        userInfo.child("UserType").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userType = dataSnapshot.getValue().toString();
                if(userType.equals("Admin")){
                    Intent intentToSignIn = new Intent(getApplicationContext(), AdminActivity.class);
                    startActivityForResult(intentToSignIn,0);
                } else if(userType.equals("ServiceProvider")){
                    Intent intentSPServices = new Intent(getApplicationContext(), ServiceProviderActivity.class);
                    startActivityForResult(intentSPServices,0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError);
            }
        });
    }


    public void addAvailability (View view) {
        LayoutInflater factory = LayoutInflater.from(this);
        view = factory.inflate(R.layout.add_availability_dialog, null);
        final AlertDialog addDialog = new AlertDialog.Builder(this).create();
        addDialog.setView(view);
        addDialog.show();

        final Availability availability = new Availability();
        //setContentView(R.layout.add_availability_dialog);

        //setStartTime = (Button) findViewById(R.id.btnSetStart);
        addDialog.findViewById(R.id.btnSetStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(WelcomeActivity.this, new TimePickerDialog.OnTimeSetListener() {
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

                TimePickerDialog timePickerDialog = new TimePickerDialog(WelcomeActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                    Toast.makeText(WelcomeActivity.this, "The End Time is less than Start Time", Toast.LENGTH_LONG).show();
                } else{
                    Spinner spinner = addDialog.findViewById(R.id.spinner1);
                    availability.setDate(spinner.getSelectedItem().toString());
                    addAvailabilityFirebase(availability);
                    addDialog.dismiss();
                }
            }
        });

        addDialog.findViewById(R.id.btnDeleteAvailability).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner spinner = addDialog.findViewById(R.id.spinner1);
                availability.setDate(spinner.getSelectedItem().toString());

                final AlertDialog.Builder confirmation = new AlertDialog.Builder(WelcomeActivity.this);
                confirmation.setMessage("Remove Availabilities for: " + availability.getDate()).setTitle("Remove Availability");
                confirmation.setCancelable(true);

                confirmation.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeAvailabilityFirebase(availability);
                        addDialog.dismiss();
                    }});
                confirmation.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) { }});
                confirmation.show();
            }
        });

    }

    public void removeAvailabilityFirebase(Availability availability){
        userInfo.child("Availability").child(Integer.toString(availability.getKey())).child("Start Time").setValue("N/A");
        userInfo.child("Availability").child(Integer.toString(availability.getKey())).child("End Time").setValue("N/A");
        setAvailabilityAdapter();
    }

    public void addAvailabilityFirebase (Availability availability){
        userInfo.child("Availability").child(Integer.toString(availability.getKey())).child("Start Time").setValue(availability.getTimeStart());
        userInfo.child("Availability").child(Integer.toString(availability.getKey())).child("End Time").setValue(availability.getTimeEnd());
        setAvailabilityAdapter();
    }

    public void setAvailabilityAdapter(){
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                availabilityAdapter = new AvailabilityCustomAdapter(WelcomeActivity.this, keysAvailability);
                listAvailabilities = (ListView) findViewById(R.id.listAvailabilites);
                listAvailabilities.setAdapter(availabilityAdapter);
                availabilityAdapter.notifyDataSetChanged();
            }
        },500); //1000ms = 1sec
    }
}
