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
                setErrorMsg(9);
                if (TextUtils.isEmpty(username.getText().toString())){
                    setErrorMsg(3);
                }
                else if (TextUtils.isEmpty(password.getText().toString())){
                    setErrorMsg(4);
                }
                else if (!isValidEmail(username.getText().toString())){
                    setErrorMsg(0);
                }
                else if (password.getText().toString().length() < 6){
                    setErrorMsg(1);
                }
                else {
                    startSignIn();
                }
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

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    setErrorMsg(2);
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

    /* 0 = Enter a valid Email
       1 = Enter a valid Password
       2 = Incorrect email and/or Password
       3 = Field is empty (username)
       4 = Field is empty (password)
       9 = CLEAR

     */
    private void setErrorMsg (int code){
        final TextView mainErrorField = (TextView) findViewById(R.id.txtError);
        final TextView usernameErrorField = (TextView) findViewById(R.id.txtUsernameError);
        final TextView passwordErrorField = (TextView) findViewById(R.id.txtPasswordError);

        if (code == 0){
            usernameErrorField.setText("Enter a valid email");
        }
        else if (code == 1) {
            passwordErrorField.setText("Enter a valid password");
        }
        else if (code == 2) {
            mainErrorField.setText("Incorrect email and/or Password");
        }
        else if (code == 3) {
            usernameErrorField.setText("Field is empty");
        }
        else if (code == 4) {
            passwordErrorField.setText("Field is empty");
        }
        else if (code == 9){
            mainErrorField.setText("");
            usernameErrorField.setText("");
            passwordErrorField.setText("");
        }
    }
}
