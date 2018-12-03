package ca.harshgupta.seg2105_project;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ClientAvailabilityHomeCustomAdapter extends ArrayAdapter{
    private final Context context;
    private final String[] myKeys;
    private DatabaseReference mAppointments, mAccounts, mServices;
    private TextView dateText, serviceText, spText, timeText, spIDText, orderIDText;

    public ClientAvailabilityHomeCustomAdapter(Context context, String[] AppointmentList){
        super(context, R.layout.client_availability_home_list_layout, AppointmentList);
        this.context = context;
        this.myKeys = AppointmentList;
    }

    @NonNull
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.client_availability_home_list_layout, parent, false);
        mAppointments = FirebaseDatabase.getInstance().getReference().child("Appointment");
        mAccounts = FirebaseDatabase.getInstance().getReference().child("Accounts");
        mServices = FirebaseDatabase.getInstance().getReference().child("Services");
        setValues(position, rowView, "Date");
        setValues(position, rowView, "Service");
        setValues(position, rowView, "SPID"); //Does IdSP and Company Name
        setValues(position, rowView, "time");

        orderIDText = (TextView) rowView.findViewById(R.id.textClientAvailabilityHomeIdOrder);  //ID order doesnt need snapshot
        orderIDText.setText(myKeys[position]);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() { } },1000); //1000ms = 1sec
        return rowView;
    }

    private void setValues(int position, final View rowView, final String info){
        mAppointments.child(myKeys[position]).child(info).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    switch (info) {
                        case "Date":
                            dateText = (TextView) rowView.findViewById(R.id.textClientAvailabilityHomeDate);
                            final String outputDate = dataSnapshot.getValue(String.class);
                            dateText.setText(outputDate);
                            break;
                        case "time":
                            timeText = (TextView) rowView.findViewById(R.id.textClientAvailabilityHomeTime);
                            String outputTime = dataSnapshot.getValue(String.class);
                            timeText.setText(outputTime);
                            break;
                        case "SPID":
                            spIDText = (TextView) rowView.findViewById(R.id.textClientAvailabilityHomeIdSP);
                            String outputSPID = dataSnapshot.getValue(String.class);
                            spIDText.setText(outputSPID);

                            spText = (TextView) rowView.findViewById(R.id.textClientAvailabilityHomeSP);
                            mAccounts.child(dataSnapshot.getValue(String.class)).child("CompanyInfo")
                                    .child("Company").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                    if (dataSnapshot2.getValue() != null && dataSnapshot.getValue() != null) {
                                        String outputSP = dataSnapshot2.getValue(String.class);
                                        spText.setText(outputSP);
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                            break;
                        case "Service":
                            serviceText = (TextView) rowView.findViewById(R.id.textClientAvailabilityHomeService);
                            mServices.child(dataSnapshot.getValue(String.class)).child("name")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot3) {
                                            if (dataSnapshot3.getValue() != null && dataSnapshot.getValue() != null) {
                                                String outputService = dataSnapshot3.getValue(String.class);
                                                System.out.println(outputService);
                                                serviceText.setText(outputService);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                            break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
}


