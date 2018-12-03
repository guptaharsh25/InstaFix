package ca.harshgupta.seg2105_project;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.logging.Handler;

public class ServiceProviderHomeFragment2 extends Fragment {
    private FirebaseUser user;
    private DatabaseReference userInfo;

    private Button addAvailability;
    private Button removeAvailability;
    private Button startTime;
    private Button endTime;
    private TextView setAvailability;
    private TextView select;
    private Spinner spin;

    View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_service_provider_home_2, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userInfo = FirebaseDatabase.getInstance().getReference().child("Accounts").child(user.getUid());
        setAvailability = myView.findViewById(R.id.setAvailability2);
        select = myView.findViewById(R.id.textView2);
        spin = myView.findViewById(R.id.spinner2);

        final Availability availability = new Availability();

        addAvailability = myView.findViewById(R.id.btnAdd3);
        addAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(availability.getEndTimeDouble() < availability.getStartTimeDouble()){
                    Toast.makeText(getActivity(), "The End Time is less than Start Time", Toast.LENGTH_LONG).show();
                } else{
                    Spinner spinner = spin;
                    availability.setDate(spinner.getSelectedItem().toString());
                    addAvailabilityFirebase(availability);
                }
            }
        });

        removeAvailability = myView.findViewById(R.id.btnDeleteAvailability2);
        removeAvailability.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Spinner spinner = spin;
                availability.setDate(spinner.getSelectedItem().toString());

                final AlertDialog.Builder confirmation = new AlertDialog.Builder(getActivity());
                confirmation.setMessage("Remove Availabilities for: " + availability.getDate()).setTitle("Remove Availability");
                confirmation.setCancelable(true);

                confirmation.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeAvailabilityFirebase(availability);
                    }});
                confirmation.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) { }});
                confirmation.show();
            }
        });

        startTime = myView.findViewById(R.id.btnSetStart2);
        startTime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int iHour, int iMinute) {
                        //Get Time
                        //userInfo.child("Availability").child(type).child("Time").setValue(iHour+":"+iMinute);
                        availability.setTimeStart(String.format("%02d",iHour) + ":" + String.format("%02d",iMinute));
                        availability.setStartTimeDouble(iHour + (iMinute/100));
                        Button button = startTime;
                        button.setText("Start: " + availability.getTimeStart());
                    }
                }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        endTime = myView.findViewById(R.id.btnSetEnd2);
        endTime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int iHour, int iMinute) {
                        //Get Time
                        //userInfo.child("Availability").child(type).child("Time").setValue(iHour+":"+iMinute);
                        availability.setTimeEnd(String.format("%02d",iHour) + ":" + String.format("%02d",iMinute));
                        availability.setEndTimeDouble(iHour + (iMinute/100));
                        Button button = endTime;
                        button.setText("End: " + availability.getTimeEnd());
                    }
                }, hour, minute, true);
                timePickerDialog.show();
            }
        });
        return myView;
    }


    /*public void openServices(View view){
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
    }*/


    /*public void addAvailability (View view) {

        findViewById(R.id.btnSetStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
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

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
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
                    Toast.makeText(getActivity(), "The End Time is less than Start Time", Toast.LENGTH_LONG).show();
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

                final AlertDialog.Builder confirmation = new AlertDialog.Builder(getActivity());
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

    }*/

    public void removeAvailabilityFirebase(Availability availability){
        userInfo.child("Availability").child(Integer.toString(availability.getKey())).child("Start Time").setValue("N/A");
        userInfo.child("Availability").child(Integer.toString(availability.getKey())).child("End Time").setValue("N/A");
        //setAvailabilityAdapter();
    }

    public void addAvailabilityFirebase (Availability availability){
        userInfo.child("Availability").child(Integer.toString(availability.getKey())).child("Start Time").setValue(availability.getTimeStart());
        userInfo.child("Availability").child(Integer.toString(availability.getKey())).child("End Time").setValue(availability.getTimeEnd());
        //setAvailabilityAdapter();
    }

    /*public void setAvailabilityAdapter(){
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                availabilityAdapter = new AvailabilityCustomAdapter(getActivity(), keysAvailability);
                listAvailabilities = (ListView) myView.findViewById(R.id.listAvailabilites);
                listAvailabilities.setAdapter(availabilityAdapter);
                availabilityAdapter.notifyDataSetChanged();
            }
        },500); //1000ms = 1sec
    }*/

}
