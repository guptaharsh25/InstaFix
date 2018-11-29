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

public class AppointmentClientCustomAdapter extends ArrayAdapter {
    private final Context context;
    private final String[] myKeys;
    private String rate;
    private DatabaseReference mServices;
    public TextView serviceRateText;
    public TextView serviceNameText;

    public AppointmentClientCustomAdapter(Context context, String[] serviceList){
        super(context, R.layout.fragment_client_home, serviceList);
        this.context = context;
        this.myKeys = serviceList;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.fragment_client_home, parent, false);


        mServices = FirebaseDatabase.getInstance().getReference().child("Services");


        return rowView;
    }

}
