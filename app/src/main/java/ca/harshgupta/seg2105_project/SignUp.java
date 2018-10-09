package ca.harshgupta.seg2105_project;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class SignUp extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener
    private EditText username;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    public void createAccount(){
        username = findViewById(R.id.editEmailSignUp);
        password = findViewById(R.id.editPassSignUp);

        String user = username.getText().toString();
        String pass = password.getText().toString();

        mAuth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()){
                            try{
                                throw task.getException();
                            }
                            catch (FirebaseAuthWeakPasswordException weakPass){
                                //password is weak
                            }
                            catch (FirebaseAuthInvalidCredentialsException malformedEmail){
                                //malformed email
                            }
                            catch(FirebaseAuthUserCollisionException alreadyExist){
                                //email already exists
                            }
                            catch(Exception e){
                                //some other error
                                //e.getMessage()
                            }

                    }
                }
        )
        }
    }
}


