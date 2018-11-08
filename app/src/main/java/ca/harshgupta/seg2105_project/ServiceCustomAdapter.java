package ca.harshgupta.seg2105_project;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ServiceCustomAdapter extends ArrayAdapter{
    private final Context context;
    private String[] myKeys;

    public ServiceCustomAdapter(Context context, String[] serviceList){
        super(context, R.layout.service_layout, serviceList);
        this.context = context;
        this.myKeys = serviceList;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.service_layout, parent, false);

        TextView serviceNameText = (TextView) rowView.findViewById(R.id.serviceName);
        final TextView serviceRateText = (TextView) rowView.findViewById(R.id.serviceRate);

        DatabaseReference mServices = FirebaseDatabase.getInstance().getReference().child("Services");

        serviceNameText.setText(myKeys[position]);

        mServices.child(myKeys[position]).child("rate").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                serviceRateText.setText(dataSnapshot.getValue(Integer.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        return rowView;
    }
}