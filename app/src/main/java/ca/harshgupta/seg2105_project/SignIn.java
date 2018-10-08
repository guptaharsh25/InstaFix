package ca.harshgupta.seg2105_project;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;



public class SignIn extends AppCompatActivity {


    private EditText username;
    private EditText password;
    private Button button;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();

        username = findViewById(R.id.editUser);
        password = findViewById(R.id.editPass);

        button = findViewById(R.id.btnSignIn1);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser()!= null){
                    Intent intentToSignIn = new Intent(getApplicationContext(), WelcomeActivity.class);
                    startActivityForResult(intentToSignIn,0);
                }
            }
        };

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSignIn();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void onSignUp(View view){
        Intent intentToSignUp = new Intent(getApplicationContext(), SignUp.class);
        startActivityForResult(intentToSignUp, 0);
    }

    private void startSignIn (){
        String email = username.getText().toString();
        String pass = password.getText().toString();
        final TextView errorField = (TextView) findViewById(R.id.txtError);

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)){
            errorField.setText("Field is Empty");
            errorField.setVisibility(View.VISIBLE);
        }
        else {
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    errorField.setVisibility(View.VISIBLE);
                    errorField.setText("Username and/or Password is incorrect");
                }
                else {
                    Intent intentToSignIn = new Intent(getApplicationContext(), WelcomeActivity.class);
                    startActivityForResult(intentToSignIn,0);
                }
            }
        });
        }


    }

}
