package ca.harshgupta.seg2105_project;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ServiceProviderHomeFragment3 extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser userInfo;
    private ServiceCustomAdapter serviceAdapter;

    private DatabaseReference mRootRef;
    private DatabaseReference mServicesRef;
    private DatabaseReference mUserRef;

    private String[] keys;
    private String[] allKeys;
    private int listSet;

    private Button addServices;
    private ListView serviceSPList;
    private TextView listTypeText;

    View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_service_provider_home_3, container, false);

        mAuth = FirebaseAuth.getInstance();
        userInfo = FirebaseAuth.getInstance().getCurrentUser();
        mRootRef = FirebaseDatabase.getInstance().getReference();

        mServicesRef = mRootRef.child("Services");
        mUserRef = mRootRef.child("Accounts").child(userInfo.getUid());

        serviceSPList = (ListView) myView.findViewById(R.id.listSPServices2);
        addServices = myView.findViewById(R.id.btnSPAdd2);
        listTypeText = myView.findViewById(R.id.textSPServices3);

        listSet = 1;

        //instantiateAllKeys();
        setKeys(1);
        setAdapterServices();
        removeServices();
        addServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addService();
            }
        });

        return myView;
    }

    private void instantiateKeys(DatabaseReference reference){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                keys = new String[(int) dataSnapshot.getChildrenCount()];
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    keys[i] = postSnapShot.getKey();
                    i++;
                }
                removeServices();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void instantiateAllKeys(){
        mServicesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                allKeys = new String[(int) dataSnapshot.getChildrenCount()];
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    allKeys[i] = postSnapShot.getKey();
                    i++;
                }
                updateServicesList();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    public void setAdapterServices(){
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if(listSet==0){
                    serviceAdapter = new ServiceCustomAdapter(getActivity(), allKeys);
                } else{
                    serviceAdapter = new ServiceCustomAdapter(getActivity(), keys);
                }
                serviceSPList = (ListView) myView.findViewById(R.id.listSPServices2);
                serviceSPList.setAdapter(serviceAdapter);
                serviceAdapter.notifyDataSetChanged();
            }
        },500); //1000ms = 1sec
    }

    public void setKeys(int type){
        //type = 0 all services, type = 1 provided services
        this.listSet = type;
        if(type == 0){
            instantiateAllKeys();
        }else if(type == 1){
            instantiateKeys(mUserRef.child("ProvidedServices"));
        }
    }

    public void addService(){
        if(listSet == 1){
            setKeys(0);
            setAdapterServices();
            addServices.setText("Back");
            listTypeText.setText("Click the service to add: ");
            updateServicesList();
        }else if(listSet == 0){
            setKeys(1);
            setAdapterServices();
            addServices.setText("Add");
            listTypeText.setText("Your Services: ");
            removeServices();
        }
    }

    public void updateServicesList(){
        if(allKeys!=null){
            serviceAdapter = new ServiceCustomAdapter(getActivity(), allKeys);
            serviceSPList = (ListView) myView.findViewById(R.id.listSPServices2);
            serviceSPList.setAdapter(serviceAdapter);
            serviceAdapter.notifyDataSetChanged();
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            serviceSPList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    final String selectedListItem = ((TextView) view.findViewById(R.id.serviceName)).getText().toString();
                    for(final String key: allKeys){
                        mServicesRef.child(key).child("name").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getValue()!=null && dataSnapshot.getValue(String.class).equals(selectedListItem)){
                                    final String selectedItemKey = key;
                                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                                    alertDialog.setTitle("Add Service");
                                    alertDialog.setMessage("Would you like to do with the service: " + selectedListItem);
                                    alertDialog.setButton(Dialog.BUTTON_POSITIVE,"Add", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            addServiceKey(selectedItemKey);
                                        }
                                    });
                                    alertDialog.setButton(Dialog.BUTTON_NEGATIVE,"Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    if(listSet==0){
                                        alertDialog.show();
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                }
            });
        }
    }

    public void addServiceKey(String itemKey){
        for(String key: keys){
            if(itemKey.equals(key)){
                Toast.makeText(getActivity(),"Service Already Provided",Toast.LENGTH_LONG).show();
                return;
            }
        }
        mUserRef.child("ProvidedServices").child(itemKey).setValue(1);
        setKeys(0);
        Toast.makeText(getActivity(),"Service Added",Toast.LENGTH_LONG).show();
    }

    public void removeServices() {
        if(keys!=null){
            serviceAdapter = new ServiceCustomAdapter(getActivity(), keys);
            serviceSPList = (ListView) myView.findViewById(R.id.listSPServices2);
            serviceSPList.setAdapter(serviceAdapter);
            serviceAdapter.notifyDataSetChanged();
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            serviceSPList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    final String selectedListItem = ((TextView) view.findViewById(R.id.serviceName)).getText().toString();
                    for(final String key: keys){
                        mServicesRef.child(key).child("name").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getValue()!=null && dataSnapshot.getValue(String.class).equals(selectedListItem)){
                                    final String selectedItemKey = key;
                                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                                    alertDialog.setTitle("Remove Service");
                                    alertDialog.setMessage("Would you like to do with the service: " + selectedListItem);
                                    alertDialog.setButton(Dialog.BUTTON_POSITIVE,"Remove", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            removeServiceKey(selectedItemKey);
                                        }
                                    });
                                    alertDialog.setButton(Dialog.BUTTON_NEGATIVE,"Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    if(listSet==1){
                                        alertDialog.show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            });
        }
    }

    public void removeServiceKey(String itemKey){
        for(String key: keys){
            if(itemKey.equals(key)){
                mUserRef.child("ProvidedServices").child(itemKey).removeValue();
                setKeys(1);
                setAdapterServices();
                Toast.makeText(getActivity(),"Service Removed",Toast.LENGTH_LONG).show();
            }
        }
    }
}
