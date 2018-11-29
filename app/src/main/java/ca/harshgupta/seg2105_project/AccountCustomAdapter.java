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

public class AccountCustomAdapter extends ArrayAdapter {
    private final Context context;
    private final String[] myKeys;
    private String rate;
    private DatabaseReference mServices, mAccounts;
    public TextView serviceRateText;
    public TextView serviceNameText;

    public AccountCustomAdapter(Context context, String[] serviceList){
        super(context, R.layout.account_layout, serviceList);
        this.context = context;
        this.myKeys = serviceList;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.account_layout, parent, false);

        mAccounts = FirebaseDatabase.getInstance().getReference().child("Accounts");
        mServices = FirebaseDatabase.getInstance().getReference().child("Services");

        mAccounts.child(myKeys[position]).child("ProvidedServices").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    final String post = postSnapshot.getValue(String.class);

                    mServices.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                                String post2 = postSnapshot.getValue(String.class);
                                if(post.equals(post2)){
                                    serviceNameText = (TextView) rowView.findViewById(R.id.serviceName);
                                    try{
                                        String name = dataSnapshot.getValue(String.class);
                                        serviceNameText.setText(name);
                                    } catch (NullPointerException e){
                                        String name = "";
                                        serviceNameText.setText(name);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        mServices.child(myKeys[position]).child("rate").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                serviceRateText = (TextView) rowView.findViewById(R.id.serviceRate);
                try {
                    String value = dataSnapshot.getValue(Double.class).toString();
                    serviceRateText.setText(value);
                } catch (NullPointerException e){
                    String value = "";
                    serviceRateText.setText(value);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        return rowView;
    }
}
