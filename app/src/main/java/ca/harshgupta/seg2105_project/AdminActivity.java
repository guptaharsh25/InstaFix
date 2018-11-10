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

        mServicesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                keys = new String[(int) dataSnapshot.getChildrenCount()];
                System.out.println(keys[0]);
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    keys[i] = postSnapShot.getKey();
                    i++;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        updateList();
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

                            AlertDialog alertDialog = new AlertDialog.Builder(AdminActivity.this).create();

                            alertDialog.setTitle("Service");
                            alertDialog.setMessage("Select what you would like to do with the service: " + selectedListItem);

                            alertDialog.setButton(Dialog.BUTTON1, "Remove", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) { removeService(selectedListItem); }
                            });

                            /*alertDialog.setButton(Dialog.BUTTON2, "Edit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) { editService(selectedListItem); }
                            });*/

                            alertDialog.show();
                        }
                    });
                }
            }
        },500); //1000ms = 1sec
    }

    public void removeService(String service){
        mServicesRef.child(service).removeValue();
        adapter.notifyDataSetChanged();
        updateList();
    }

    public void editService(final View view){
        final double[] serviceRate = {0};
        //final DatabaseReference mEdit = mServicesRef.child(service);

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

        serviceAdd.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String getService = getServiceName.getText().toString();
                Context context = getApplicationContext();
                double newRate = Double.parseDouble(getServiceRate.getText().toString());

                int duration = Toast.LENGTH_LONG;
                boolean duplicateFound = false;
                System.out.println(keys[0]);
                if (keys!=null){
                    for(int i=0; i< keys.length; i++){
                        String name = FirebaseDatabase.getInstance().getReference().child("Services").child(keys[i]).child("name").toString();
                        if(getService.equals(name)){
                            duplicateFound = true;
                        }
                    }
                }
                if(!duplicateFound){
                    CharSequence textDuplicateService = "Please Enter an Existing Service to Edit";

                    Toast toastService = Toast.makeText(context, textDuplicateService, duration);
                    toastService.show();
                    editService(view);
                } else {
                    if(newRate!=0){
                        mServicesRef.child(getService).child("rate").setValue(serviceRate[0]);
                    } else {
                        CharSequence textRate = "Please Enter a Valid Service Rate (Has to be greater than 0)";

                        Toast toastRate = Toast.makeText(context, textRate, duration);
                        toastRate.show();
                        editService(view);
                    }
                }

            }
        });

        serviceAdd.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { dialog.cancel(); }
        });

        serviceAdd.show();

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
                int duration = Toast.LENGTH_LONG;
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
                    CharSequence textDuplicateService = "Service Exits: Please Enter a New Service";

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
                    mServicesRef.child(serviceName[0]);
                    mServicesRef.child(serviceName[0]).child("rate").setValue(serviceRate[0]);
                    mServicesRef.child(serviceName[0]).child("user").setValue(user.getDisplayName());
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
