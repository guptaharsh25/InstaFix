package ca.harshgupta.seg2105_project;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mRootRef;
    private DatabaseReference mServicesRef;

    private ArrayList<String> serviceNames;
    private String[] keys;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private ServiceCustomAdapter adapter;

    private Button add;
    private ListView serviceList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_layout);
        serviceList = (ListView) findViewById(R.id.serviceList);
        add = findViewById(R.id.btnAdd);
        setContentView(R.layout.activity_admin);

        mAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mServicesRef = mRootRef.child("Services");
        instantiateKeys();
        updateList();
    }

    public void instantiateKeys(){
        mServicesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                keys = new String[(int) dataSnapshot.getChildrenCount()];
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    keys[i] = postSnapShot.getKey();
                    i++;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    public void onStart(){
        super.onStart();
    }

    public void onResume(){
        super.onResume();
    }

    public void updateList(){
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                serviceList = (ListView) findViewById(R.id.serviceList);
                if (keys!=null){
                    adapter = new ServiceCustomAdapter(AdminActivity.this, keys);
                    serviceList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    serviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            final String selectedListItem = ((TextView) view.findViewById(R.id.serviceName)).getText().toString();
                            for(final String key: keys){
                                mServicesRef.child(key).child("name").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.getValue()!=null && dataSnapshot.getValue(String.class).equals(selectedListItem)){
                                            final String selectedItemKey = key;
                                            //Toast.makeText(AdminActivity.this,selectedItemKey,Toast.LENGTH_LONG).show();

                                            AlertDialog alertDialog = new AlertDialog.Builder(AdminActivity.this).create();

                                            alertDialog.setTitle("Service");
                                            alertDialog.setMessage("Select what you would like to do with the service: " + selectedListItem);

                                            alertDialog.setButton(Dialog.BUTTON1, "Remove", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) { removeService(selectedItemKey); }
                                            });

                                            alertDialog.setButton(Dialog.BUTTON2, "Edit", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) { editService(selectedItemKey); }
                                            });

                                            alertDialog.show();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                                });
                            }

                        }
                    });
                }
            }
        },500); //1000ms = 1sec
    }

    public void removeService(String service){
        mServicesRef.child(service).removeValue();
        ServiceCustomAdapter tempAdapter = new ServiceCustomAdapter(AdminActivity.this, new String[0]);
        serviceList.setAdapter(tempAdapter);
        instantiateKeys();
        updateList();
    }

    public void editService(final String service){
        final DatabaseReference mEdit = mServicesRef.child(service);

        final AlertDialog.Builder serviceAdd = new AlertDialog.Builder(this);
        serviceAdd.setTitle("Edit Service");

        final EditText getServiceName = new EditText(this);
        final EditText getServiceRate = new EditText(this);
        mEdit.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getServiceName.setText(dataSnapshot.getValue(String.class)); }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        mEdit.child("rate").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getServiceRate.setText(dataSnapshot.getValue(Double.class).toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        getServiceName.setHint("Service Name");
        getServiceRate.setHint("Rate");


        getServiceName.setInputType(InputType.TYPE_CLASS_TEXT);
        getServiceRate.setInputType(InputType.TYPE_CLASS_TEXT);

        LinearLayout linLayout = new LinearLayout(this);
        linLayout.setOrientation(LinearLayout.VERTICAL);

        linLayout.addView(getServiceName);
        linLayout.addView(getServiceRate);

        serviceAdd.setView(linLayout);

        serviceAdd.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String newService = getServiceName.getText().toString();
                String newRate = getServiceRate.getText().toString();

                mEdit.child("rate").setValue(Double.parseDouble(newRate));
                mEdit.child("name").setValue(newService);

            }
        });

        serviceAdd.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { dialog.cancel(); }
        });

        serviceAdd.show();
        updateList();

    }

    public void addService(final View view){
        final String[] serviceName = {""};
        final double[] serviceRate = {0};

        final AlertDialog.Builder serviceAdd = new AlertDialog.Builder(this);
        serviceAdd.setTitle("Add New Service");

        final EditText getServiceName = new EditText(this);
        final EditText getServiceRate = new EditText(this);
        getServiceName.setHint("Service Name");
        getServiceRate.setHint("Rate");

        getServiceName.setInputType(InputType.TYPE_CLASS_TEXT);
        getServiceRate.setInputType(InputType.TYPE_CLASS_TEXT);

        LinearLayout linLayout = new LinearLayout(this);
        linLayout.setOrientation(LinearLayout.VERTICAL);

        linLayout.addView(getServiceName);
        linLayout.addView(getServiceRate);

        serviceAdd.setView(linLayout);

        updateList();
        serviceAdd.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String newService = getServiceName.getText().toString();
                Context context = getApplicationContext();
                CharSequence text = "Method working";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                boolean duplicateFound = false;
                if (keys!=null){
                    for(int i=0; i< keys.length; i++){
                        String name = FirebaseDatabase.getInstance().getReference().child("Services").child(keys[i]).child("name").toString();
                        if(serviceName[0].equals(name)){
                            duplicateFound = true;
                        }
                    }
                }

                if(duplicateFound){
                    CharSequence textDuplicateService = "Please Enter a Valid Service Rate";

                    Toast toastService = Toast.makeText(context, textDuplicateService, duration);
                    toastService.show();
                } else {
                    serviceName[0] = newService;
                }

                try{
                    double newRate = Double.parseDouble(getServiceRate.getText().toString());
                    serviceRate[0] = newRate;
                } catch (Exception exception){
                    CharSequence textValidRate = "Please Enter a Valid Service Rate";
                    Toast toastValidRate = Toast.makeText(context, textValidRate, duration);
                    toastValidRate.show();

                    addService(view);
                }

                if(!serviceName[0].equals("") || serviceRate[0]!=0) {
                    String serviceID = mServicesRef.push().getKey();
                    mServicesRef.child(serviceID);
                    mServicesRef.child(serviceID).child("name").setValue(serviceName[0]);
                    mServicesRef.child(serviceID).child("rate").setValue(serviceRate[0]);
                    mServicesRef.child(serviceID).child("user").setValue(user.getDisplayName());
                    updateList();
                }
            }
        });

        serviceAdd.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog ad = serviceAdd.create();
        ad.show();
        updateList();

        if(!serviceName[0].equals("") || serviceRate[0]!=0) {
            mServicesRef.child("name").setValue(serviceName[0]);
            mServicesRef.child("rate").setValue(serviceRate[0]);
        }
    }

    private void collectServiceNames(Map<String,Object> services) {

        serviceNames = new ArrayList<>();

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : services.entrySet()){

            //Get user map
            Map singleService = (Map) entry.getValue();
            //Get phone field and append to list
            serviceNames.add((String) singleService.get("name"));
        }
    }
}