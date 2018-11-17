package ca.harshgupta.seg2105_project;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class WelcomeActivity extends AppCompatActivity {
    FirebaseUser user;
    DatabaseReference userInfo;
    private Button addAvailability;
    private Button setStartTime;
    private Button setEndTime;
    private Button dialogAddAvailability;

    private TextView startText;
    private TextView endText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        addAvailability = findViewById(R.id.btnAddAvailability);

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
                if (!userType.equals("ServiceProvider")){
                    //btnTimeStart.setVisibility(View.GONE);
                    addAvailability.setVisibility(View.GONE);
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
                        availability.setTimeStart(iHour + ":" + iMinute);
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
                        availability.setTimeEnd(iHour + ":" + iMinute);
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
                Spinner spinner = addDialog.findViewById(R.id.spinner1);
                availability.setDate(spinner.getSelectedItem().toString());
                addAvailabilityFirebase(availability);
                addDialog.dismiss();

            }
        });

    }

    public void addAvailabilityFirebase (Availability availability){
        userInfo.child("Availability").child(Integer.toString(availability.getKey())).child("Date").setValue(availability.getDate());
        userInfo.child("Availability").child(Integer.toString(availability.getKey())).child("Start Time").setValue(availability.getTimeStart());
        userInfo.child("Availability").child(Integer.toString(availability.getKey())).child("End Time").setValue(availability.getTimeEnd());
    }
}
