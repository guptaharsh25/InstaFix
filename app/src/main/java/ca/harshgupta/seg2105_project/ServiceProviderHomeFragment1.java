package ca.harshgupta.seg2105_project;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class ServiceProviderHomeFragment1 extends Fragment {
    private FirebaseUser user;
    private DatabaseReference userInfo;

    private TextView availabilityText;
    private ListView listAvailabilities;

    View myView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_service_provider_home, container, false);
        return myView;
    }

    public void onStart (){
        super.onStart();

        availabilityText = myView.findViewById(R.id.headerAvailabilities2);

        listAvailabilities = (ListView) myView.findViewById(R.id.listAvailabilites2);

        final TextView welcomeText = (TextView) myView.findViewById(R.id.txtWelcome2);
        final TextView roleText = (TextView) myView.findViewById(R.id.txtRole2);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userInfo = FirebaseDatabase.getInstance().getReference().child("Accounts")
                .child(user.getUid());

        userInfo.child("FirstName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.getValue().toString();
                welcomeText.setText("Welcome " + userName + "!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError);
            }
        });

        userInfo.child("UserType").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userType = dataSnapshot.getValue().toString();
                roleText.setText("You are logged in as " + userType);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError);
            }
        });

        userInfo.child("Availability").child(Integer.toString(0)).child("Date").setValue("Sunday");
        userInfo.child("Availability").child(Integer.toString(1)).child("Date").setValue("Monday");
        userInfo.child("Availability").child(Integer.toString(2)).child("Date").setValue("Tuesday");
        userInfo.child("Availability").child(Integer.toString(3)).child("Date").setValue("Wednesday");
        userInfo.child("Availability").child(Integer.toString(4)).child("Date").setValue("Thursday");
        userInfo.child("Availability").child(Integer.toString(5)).child("Date").setValue("Friday");
        userInfo.child("Availability").child(Integer.toString(6)).child("Date").setValue("Saturday");

    }
}

