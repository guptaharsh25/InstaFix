package ca.harshgupta.seg2105_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    public void onSignOut (View view){
        FirebaseAuth.getInstance().signOut();
        Intent intentToSignOut = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(intentToSignOut,0);
    }
}
