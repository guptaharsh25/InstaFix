package ca.harshgupta.seg2105_project;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ServiceProviderInfo extends AppCompatActivity {

    private EditText address, phone, company, description;
    private CheckBox license;
    private TextView error;
    private Button next;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mRootRef;
    private DatabaseReference mAccountsRef;
    private DatabaseReference mServiceProviderRef;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_info);

        address = findViewById(R.id.editAddress);
        phone = findViewById(R.id.editPhone);
        company = findViewById(R.id.editCompany);
        description = findViewById(R.id.editDescription);

        license = findViewById(R.id.checkLicensed);
        error = findViewById(R.id.txtErrorAlert);
        next = findViewById(R.id.btnNext);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAccountsRef = mRootRef.child("Accounts");
        mServiceProviderRef = mAccountsRef.child(user.getUid());
    }

    public void addInfo(View view) {
        if (TextUtils.isEmpty(address.getText().toString())){
            error.setText("Please enter the Address");
        } else if (TextUtils.isEmpty(phone.getText().toString())){
            error.setText("Please enter the Phone Number");
        } else if (TextUtils.isEmpty(company.getText().toString())) {
            error.setText("Please enter the Company Name");
        } else {
            if (TextUtils.isEmpty(description.getText().toString())) {
                description.setText("null");
            }
            try{
                Long.parseLong(phone.getText().toString());
                final AlertDialog alertDialog = new AlertDialog.Builder(ServiceProviderInfo.this).create();
                alertDialog.setTitle("Proceed?");
                alertDialog.setMessage("Address: " + address.getText().toString() + "\nPhone Number: " + phone.getText().toString()
                        + "\nCompany Name: " + company.getText().toString() + "\nLicense: " + license.isChecked());

                alertDialog.setButton(Dialog.BUTTON1, "Next", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            mServiceProviderRef.child("CompanyInfo").child("Address").setValue(address.getText().toString());
                            mServiceProviderRef.child("CompanyInfo").child("Phone").setValue(Long.parseLong(phone.getText().toString()));
                            mServiceProviderRef.child("CompanyInfo").child("Company").setValue(company.getText().toString());
                            mServiceProviderRef.child("CompanyInfo").child("Description").setValue(description.getText().toString());
                            mServiceProviderRef.child("CompanyInfo").child("Licence").setValue(license.isChecked());

                            Intent intentToSignIn = new Intent(getApplicationContext(), WelcomeActivity.class);
                            startActivityForResult(intentToSignIn, 0);
                        } catch (Exception e){
                            e.printStackTrace();
                        } }
                });

                alertDialog.setButton(Dialog.BUTTON2, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.cancel(); }
                });
                alertDialog.show();

            } catch (NumberFormatException e){
                error.setText("Incorrect Phone Number format");
            }
        }
    }
}
