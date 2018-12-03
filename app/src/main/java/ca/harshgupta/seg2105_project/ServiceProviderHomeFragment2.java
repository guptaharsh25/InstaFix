package ca.harshgupta.seg2105_project;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ServiceProviderHomeFragment2 extends Fragment {

    View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_service_provider_home_2, container, false);
        return myView;
    }

    /*
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
     */

}
