package ca.harshgupta.seg2105_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Node;

public class ServiceCustomAdapter extends ArrayAdapter{
    private final Context context;
    private final String[] myKeys;

    public ServiceCustomAdapter(Context context, String[] serviceList, Node[] keys){
        super(context, R.layout.service_layout, serviceList);
        this.context = context;
        this.myKeys = serviceList;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.service_layout, parent, false);

        TextView serviceNameText = (TextView) rowView.findViewById(R.id.serviceName);
        TextView serviceRateText = (TextView) rowView.findViewById(R.id.serviceRate);

        String serviceName = FirebaseDatabase.getInstance().getReference().child("Accounts")
                .child(myKeys[position]).child("name").toString();
        Double serviceRate = Double.parseDouble(FirebaseDatabase.getInstance().getReference().child("Services").child(myKeys[position]).child("rate").toString());

        serviceNameText.setText(serviceName);

        serviceRateText.setText(serviceRate.toString());

        return rowView;
    }
}