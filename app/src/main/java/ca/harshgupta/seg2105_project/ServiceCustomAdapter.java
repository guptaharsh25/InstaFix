package ca.harshgupta.seg2105_project;

import android.annotation.SuppressLint;
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

import org.w3c.dom.Node;

import java.util.ArrayList;

import ca.harshgupta.seg2105_project.user_data_packets.Service;

public class ServiceCustomAdapter extends ArrayAdapter{
    private final Context context;
    private String rate;
    private DatabaseReference mServices;
    public TextView serviceRateText;
    private ArrayList<Service> services;

    public ServiceCustomAdapter(Context context, ArrayList services){
        super(context, R.layout.service_layout, services);
        this.context = context;
        this.services = services;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listViewItem = inflater.inflate(R.layout.service_layout, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.serviceName);
        TextView textViewRate = (TextView) listViewItem.findViewById(R.id.serviceRate);

        Service service = services.get(position);
        textViewName.setText(service.getName());
        textViewRate.setText(String.valueOf(service.getRate()));
        return listViewItem;
    }

//    public View getView(int position, View convertView, ViewGroup parent){
//
//        final View rowView = inflater.inflate(R.layout.service_layout, parent, false);
//
//        TextView serviceNameText = (TextView) rowView.findViewById(R.id.serviceName);
//        mServices = FirebaseDatabase.getInstance().getReference().child("Services");
//        serviceNameText.setText(mServices.child(myKeys[position]).getKey());
//        mServices.child(myKeys[position]).child("rate").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                serviceRateText = (TextView) rowView.findViewById(R.id.serviceRate);
//                //if (dataSnapshot.getValue() != null) {
//                    String value = dataSnapshot.getValue().toString();
//                    serviceRateText.setText(value);
//                //}
//
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) { }
//        });
//        return rowView;
//    }
}