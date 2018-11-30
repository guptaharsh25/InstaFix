package ca.harshgupta.seg2105_project;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountCustomAdapter extends ArrayAdapter {
    private final Context context;
    private final String[] myKeys;
    private final String[] accountList;
    private final Integer[] dayList;
    private String rate;
    private DatabaseReference mServices, mAccounts;
    public TextView serviceRateText;
    public TextView serviceNameText;
    public TextView provider;
    public TextView rating;
    public TextView day;
    public TextView time;

    public AccountCustomAdapter(Context context, String[] serviceList, String[] accountList, Integer[] daysList){
        super(context, R.layout.account_layout, serviceList);
        this.context = context;
        this.myKeys = serviceList;
        this.accountList = accountList;
        this.dayList = daysList;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.account_layout, parent, false);
        System.out.println("-----" + myKeys.length + "-----" + accountList.length + "-----" + dayList.length + "-----");
        mAccounts = FirebaseDatabase.getInstance().getReference().child("Accounts");
        mServices = FirebaseDatabase.getInstance().getReference().child("Services");

        mAccounts.child(accountList[position]).child("FirstName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                provider = (TextView) rowView.findViewById(R.id.name);
                String name;
                try{
                    name = dataSnapshot.getValue().toString();
                } catch (NullPointerException e){
                    name = "";
                }
                provider.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        mAccounts.child(accountList[position]).child("LastName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                provider = (TextView) rowView.findViewById(R.id.name);
                String name;
                try{
                    name = dataSnapshot.getValue().toString();
                } catch (NullPointerException e){
                    name = "";
                }
                provider.setText(provider.getText() + " " + name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        //Add availability
        if(dayList[position]>=0) {
            mAccounts.child(accountList[position]).child("Availability").child(Integer.toString(dayList[position])).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    day = (TextView) rowView.findViewById(R.id.day);
                    time = (TextView) rowView.findViewById(R.id.timing);

                    String tempDay = "";
                    String tempStart = "N/A";
                    String tempEnd = "N/A";

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        if (postSnapshot.getKey().equals("Date")) {
                            tempDay = postSnapshot.getValue().toString().substring(0, 3);
                        } else if (postSnapshot.getKey().equals("Start Time")) {
                            tempStart = postSnapshot.getValue().toString();
                        } else if (postSnapshot.getKey().equals("End Time")) {
                            tempEnd = postSnapshot.getValue().toString();
                        }
                    }

                    if (!tempStart.contains("N/A") && !tempEnd.contains("N/A")) {
                        day.setText(tempDay);
                        time.setText(tempStart + " - " + tempEnd);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } else{
            day = (TextView) rowView.findViewById(R.id.day);
            time = (TextView) rowView.findViewById(R.id.timing);
            day.setText("");
            time.setText("");

        }

        mAccounts.child(accountList[position]).child("AverageRating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rating = (TextView) rowView.findViewById(R.id.rating);
                String rate;
                try{
                    rate = dataSnapshot.getValue(Double.class).toString();
                } catch (NullPointerException e){
                    rate = "-";
                }
                rating.setText(rate);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mServices.child(myKeys[position]).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                serviceNameText = (TextView) rowView.findViewById(R.id.serviceName);
                String name;
                try{
                    name = dataSnapshot.getValue(String.class);
                } catch (NullPointerException e){
                    name = "";
                }
                serviceNameText.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        mServices.child(myKeys[position]).child("rate").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                serviceRateText = (TextView) rowView.findViewById(R.id.serviceRate);
                String value;
                try {
                    value = dataSnapshot.getValue(Double.class).toString();
                } catch (NullPointerException e){
                    value = "";
                }
                serviceRateText.setText(value);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        return rowView;
    }
}
