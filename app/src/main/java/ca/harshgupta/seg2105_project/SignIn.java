package ca.harshgupta.seg2105_project;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ca.harshgupta.seg2105_project.user_data_packets.SignInData;


public class SignIn extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();
    }

    public SignInData pullLoginData(View view){
        EditText userNameInput = (EditText) findViewById(R.id.editUserSignIn);
        EditText passwordInput = (EditText) findViewById(R.id.editPassSignIn);

        return new SignInData(userNameInput.getText().toString(), passwordInput.getText().toString());
    }

    public void onSignInButtonClick(View view){
        SignInData loginPacket = pullLoginData(view);

        //check if this is an e-mail or username
        /*
        if(android.util.Patterns.EMAIL_ADDRESS.matcher(loginPacket.getUsername()).matches())                 {
            mAuth.signInWithEmailAndPassword(loginPacket.getUsername(), loginPacket.getPassword())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                //updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }

                            // ...
                        }
                    });
        }
        else{

        }
        */

    }
}
