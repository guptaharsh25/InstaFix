package ca.harshgupta.seg2105_project;

import android.content.Context;
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
    private DatabaseReference mAppointments, mAccounts;
    public TextView dateText, serviceText, spText, timeText;

    public ClientAvailabilityHomeCustomAdapter(Context context, String[] AppointmentList){
        super(context, R.layout.client_availability_home_list_layout, AppointmentList);
        this.context = context;
        this.myKeys = AppointmentList;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.client_availability_home_list_layout, parent, false);

        mAppointments = FirebaseDatabase.getInstance().getReference().child("Appointment");
        mAccounts = FirebaseDatabase.getInstance().getReference().child("Accounts");
        setValues(position, rowView, "Date");
        setValues(position, rowView, "Service");
        setValues(position, rowView, "time");
        setValues(position, rowView, "SPID");

        return rowView;
    }

    public void setValues(int position, final View rowView, final String info){
        mAppointments.child(myKeys[position]).child(info).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(info.equals("Date")){
                    dateText = (TextView) rowView.findViewById(R.id.textClientAvailabilityHomeDate);
                    dateText.setText(dataSnapshot.getValue(String.class));
                } else if(info.equals("Service")) {
                    serviceText = (TextView) rowView.findViewById(R.id.textClientAvailabilityHomeService);
                    serviceText.setText(dataSnapshot.getValue(String.class));
                } else if(info.equals("time")) {
                    timeText = (TextView) rowView.findViewById(R.id.textClientAvailabilityHomeTime);
                    timeText.setText(dataSnapshot.getValue(String.class));
                } else if(info.equals("SPID")) {
                    spText = (TextView) rowView.findViewById(R.id.textClientAvailabilityHomeService);
                    mAccounts.child(dataSnapshot.getValue(String.class)).child("CompanyInfo")
                            .child("Company").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                            spText.setText(dataSnapshot2.getValue(String.class)); }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
}


