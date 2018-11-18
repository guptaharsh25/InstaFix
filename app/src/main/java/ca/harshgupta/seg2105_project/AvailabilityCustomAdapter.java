package ca.harshgupta.seg2105_project;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AvailabilityCustomAdapter extends ArrayAdapter {
    private final Context context;
    private final String[] myKeys = {"0","1","2","3","4","5","6"};

    private DatabaseReference mAvailabilities;
    private DatabaseReference mUserRef;
    private FirebaseUser user;

    public TextView dayText;
    public TextView startTimeText;
    public TextView endTimeText;

    public AvailabilityCustomAdapter(Context context, String[] availabilityList){
        super(context, R.layout.availability_list_layout, availabilityList);
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.availability_list_layout, parent, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Accounts").child(user.getUid());
        mAvailabilities = mUserRef.child("Availability");

        mAvailabilities.child(myKeys[position]).child("Date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dayText = (TextView) rowView.findViewById(R.id.textDay);
                try{
                    String name = dataSnapshot.getValue(String.class);
                    if (name != null)
                        dayText.setText(name);
                    else
                        dayText.setText("N/A");
                } catch (NullPointerException e){
                    System.out.println(e.getStackTrace());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        mAvailabilities.child(myKeys[position]).child("Start Time").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                startTimeText = (TextView) rowView.findViewById(R.id.textStartDate);
                try{
                    String name = dataSnapshot.getValue(String.class);
                    if (name != null)
                        startTimeText.setText(name);
                    else
                        startTimeText.setText("N/A");
                } catch (NullPointerException e){
                    System.out.println(e.getStackTrace());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        mAvailabilities.child(myKeys[position]).child("End Time").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                endTimeText = (TextView) rowView.findViewById(R.id.textEndDate);
                try{
                    String name = dataSnapshot.getValue(String.class);
                    if (name != null)
                        endTimeText.setText(" -    " + name);
                    else
                        endTimeText.setText("N/A");
                } catch (NullPointerException e){
                    System.out.println(e.getStackTrace());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        return rowView;
    }


}
