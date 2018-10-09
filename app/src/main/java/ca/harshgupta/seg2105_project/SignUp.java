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

public class SignUp extends AppCompatActivity {
    private EditText firstname, lastname, username, email, password, vpassword;
    private TextView error;
    private Button button;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firstname = findViewById(R.id.editNameFirst);
        lastname = findViewById(R.id.editNameLast);
        username = findViewById(R.id.editUsernameSignUp);
        email = findViewById(R.id.editEmailSignUp);
        password = findViewById(R.id.editPassSignUp);
        vpassword = findViewById(R.id.editPassVerifySignUp);
        error = findViewById(R.id.txtSignUpError);

        button = findViewById(R.id.btnSignUp);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener(){
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

                if (TextUtils.isEmpty(firstname.getText().toString())){
                    error.setText("Please enter your first Name");
                } else if (TextUtils.isEmpty(lastname.getText().toString())){
                    error.setText("Please enter your last Name");
                } else if (TextUtils.isEmpty(username.getText().toString())){
                    error.setText("Please enter the username");
                } else if (!isValidEmail(email.getText().toString())){
                    error.setText("Incorrect email and/or password");
                } else if (TextUtils.isEmpty(password.getText().toString())){
                    error.setText("Please enter the password");
                } else if (TextUtils.isEmpty(vpassword.getText().toString())) {
                    error.setText("Please verify your password");
                } else if (TextUtils.isEmpty(email.getText().toString())){
                    error.setText("Please enter your email");
                } else if ((password.getText().toString().length() < 6)||(password != vpassword)){
                    error.setText("Enter a valid password");
                } else {
                    startSignUp();
                }
            }
        });
    }

    private void startSignUp (){
        String email = username.getText().toString();
        String pass = password.getText().toString();
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    error.setText("Incorrect email and/or password");
                }
                else {
                    Intent intentToSignIn = new Intent(getApplicationContext(), WelcomeActivity.class);
                    startActivityForResult(intentToSignIn,0);
                }
            }
        });
    }

    private boolean isValidEmail (String email){
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
/*
if pass  = verify pass
    good. then
        mauth.signin(email,pass)
            if!succesfull then good
                mauth.create(email,pass)
                sign in(email pass)
            else
                error. account alrdy exists
            go back to sign in page.*/


