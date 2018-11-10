package ca.harshgupta.seg2105_project;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUp extends AppCompatActivity {
    private EditText firstname, lastname, email, password, vpassword;
    private TextView error;
    private Button button;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private String radioValue = "";

    private DatabaseReference mRootRef;
    private DatabaseReference mAccountsRef;
    private DatabaseReference mNewUsernameRef;
    private DatabaseReference mAdminInitializedRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firstname = findViewById(R.id.editNameFirst);
        lastname = findViewById(R.id.editNameLast);
        email = findViewById(R.id.editEmailSignUp);
        password = findViewById(R.id.editPassSignUp);
        vpassword = findViewById(R.id.editPassVerifySignUp);
        error = findViewById(R.id.txtSignUpError);
        button = findViewById(R.id.btnSignUp);

        mAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAccountsRef = mRootRef.child("Accounts");
        mAdminInitializedRef = mRootRef.child("Admin_Initialized");

        radioGroup = (RadioGroup) findViewById(R.id.radioGroupSignUp);
        //Verifying Inputs
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedID = radioGroup.getCheckedRadioButtonId(); //get selected radio button

                if (TextUtils.isEmpty(firstname.getText().toString())){
                    error.setText("Please enter your first Name");
                } else if (TextUtils.isEmpty(lastname.getText().toString())){
                    error.setText("Please enter your last Name");
                } else if (!isValidEmail(email.getText().toString())) {
                    error.setText("Incorrect email and/or password");
                } else if (TextUtils.isEmpty(password.getText().toString())){
                    error.setText("Please enter the password");
                } else if (TextUtils.isEmpty(vpassword.getText().toString())) {
                    error.setText("Please verify your password");
                } else if (TextUtils.isEmpty(email.getText().toString())){
                    error.setText("Please enter your email");
                } else if (TextUtils.isEmpty(password.getText().toString())){
                    error.setText("Please enter a password");
                } else if (TextUtils.isEmpty(vpassword.getText().toString())){
                    error.setText("Please verify your password");
                } else if (!password.getText().toString().equals(vpassword.getText().toString())) {
                    error.setText("Your passwords do not match. Please try again");
                } else if(selectedID == -1){
                    error.setText("Please select eiher Client or Service Provider");
                } else {
                    radioButton = (RadioButton) findViewById(selectedID);  //find the radio button by returned id
                    radioValue = radioButton.getText().toString();
                    startSignUp();
                }
            }
        });
    }

    public void onStart(){
        super.onStart();
        mAdminInitializedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            boolean adminStatus = dataSnapshot.getValue(Boolean.class);
            if (adminStatus)
                findViewById(R.id.radioAdmin).setVisibility(View.GONE);
            else
                findViewById(R.id.radioAdmin).setVisibility(View.VISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public void startSignUp(){
        String emailSignUp = email.getText().toString();
        String passSignUp = password.getText().toString();

        mAuth.createUserWithEmailAndPassword(emailSignUp, passSignUp).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException weakPass) {
                                error.setText("The password is weak. You need atleast 6 characters. Please try again");
                            } catch (FirebaseAuthInvalidCredentialsException emailWrong) {
                                error.setText("Please enter a valid e-mail");
                            } catch (FirebaseAuthUserCollisionException alreadyExist) {
                                error.setText("Your email account already exists. Please enter another e-mail");
                            } catch (Exception e) {
                                //some other error
                                error.setText(e.getMessage());
                            }
                        } else{
                            signInWithNewAccount(email.getText().toString(),password.getText().toString());
                        }
                    }
                }
        );
    }

    private void signInWithNewAccount(final String getEmail, String pass){
        //Login with the newly created ID
        mAuth.signInWithEmailAndPassword(getEmail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    error.setText("There seems to be an error logging in to your new account");
                }
                else {
                    FirebaseUser user = mAuth.getCurrentUser();

                    //Update user profile
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(firstname.getText().toString() + " " + lastname.getText().toString())
                            .build();
                    user.updateProfile(profileUpdates);

                    // update database info with admin, client, first Name, last Name, email, Service Provider

                    //Create a child of the Users UID
                    mNewUsernameRef = mAccountsRef.child(user.getUid());
                    //Add Information of that UID
                    mNewUsernameRef.child("FirstName").setValue(firstname.getText().toString());
                    mNewUsernameRef.child("LastName").setValue(lastname.getText().toString());
                    mNewUsernameRef.child("Email").setValue(email.getText().toString());
                    mNewUsernameRef.child("Password").setValue(password.getText().toString());
                    if (radioValue.equals("Admin")){
                        mNewUsernameRef.child("UserType").setValue("Admin");
                    } else if(radioValue.equals("ServiceProvider")){
                        mNewUsernameRef.child("UserType").setValue("ServiceProvider");
                    } else if(radioValue.equals("Client")){
                        mNewUsernameRef.child("UserType").setValue("Client");
                    }

                    //Set if there is a admin in the database. Next run, there will be no admin
                    mAdminInitializedRef.setValue(radioValue.equals("Admin"));

                    //Welcome Page
                    Intent intentToSignIn = new Intent(getApplicationContext(), WelcomeActivity.class);
                    startActivityForResult(intentToSignIn,0);
                }
            }
        });
    }

    private boolean isValidEmail (String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}



