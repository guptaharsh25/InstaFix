package ca.harshgupta.seg2105_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.oob.SignUp;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onSignIn(View view){
        Intent intentToSignIn = new Intent(getApplicationContext(), SignIn.class);
        startActivityForResult(intentToSignIn,0);
    }
    /*
    public void onSignUp(View view){
        Intent intentToSignUp = new Intent(getApplicationContext(), SignUp.class);
        startActivityForResult(intentToSignUp, 0);
    }
    */

}