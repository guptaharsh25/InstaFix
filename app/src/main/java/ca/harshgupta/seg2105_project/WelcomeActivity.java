package ca.harshgupta.seg2105_project;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final TextView welcomeText = (TextView) findViewById(R.id.txtWelcome);
        final TextView roleText = (TextView) findViewById(R.id.txtRole);
        DatabaseReference userInfo = FirebaseDatabase.getInstance().getReference().child("Accounts")
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
    }

    public void onSignOut (View view){
        FirebaseAuth.getInstance().signOut();
        Intent intentToSignOut = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(intentToSignOut,0);
    }
}
