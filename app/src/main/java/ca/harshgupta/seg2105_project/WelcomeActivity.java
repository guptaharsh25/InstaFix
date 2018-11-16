package ca.harshgupta.seg2105_project;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Calendar;

public class WelcomeActivity extends AppCompatActivity {
    FirebaseUser user;
    DatabaseReference userInfo;
    private Button btnTimeStart;
    private Button btnTimeEnd;

    private TextView startText;
    private TextView endText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        btnTimeStart = findViewById(R.id.btnSetAvailabilityStart);
        btnTimeEnd = findViewById(R.id.btnSetAvailabilityEnd);

        startText = findViewById(R.id.txtStartTime);
        endText = findViewById(R.id.txtEndTime);

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
                    btnTimeStart.setVisibility(View.GONE);
                    btnTimeEnd.setVisibility(View.GONE);
                }
                else if (userType.equals("ServiceProvider")){
                    setTextAvailability("Start", startText);
                    setTextAvailability("End", endText);
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

    public void setAvailabilityStart(View view){
        setAvailability("Start");
        setTextAvailability("Start", startText);

    }
    public void setAvailabilityEnd(View view){
        setAvailability("End");
        setTextAvailability("End", endText);
    }

    public void setTextAvailability(final String type, final TextView textViewSetter){
        userInfo.child("Availability").child(type).child("Date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                final String date = ("Date: " + dataSnapshot1.getValue(String.class));
                userInfo.child("Availability").child(type).child("Time").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                        textViewSetter.setText(date + ", Time: " + dataSnapshot2.getValue(String.class));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                }); }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public void setAvailability(final String type){
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int iYear, int iMonth, int iDay) {
                //Get selected year date month
                userInfo.child("Availability").child(type).child("Date").setValue(iYear+":"+iMonth+":"+iDay);
            }
        }, year, month, day);
        datePickerDialog.show();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int iHour, int iMinute) {
                //Get Time
                userInfo.child("Availability").child(type).child("Time").setValue(iHour+":"+iMinute);
            }
        }, hour, minute, true);
        timePickerDialog.show();
    }
}
