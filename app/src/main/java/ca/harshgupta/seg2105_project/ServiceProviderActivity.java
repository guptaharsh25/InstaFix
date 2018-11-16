package ca.harshgupta.seg2105_project;

import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.view.View;
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

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ServiceProviderActivity extends Activity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mRootRef;
    private DatabaseReference mServicesRef;
    private DatabaseReference mUserRef;

    private String[] keys;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private ServiceCustomAdapter serviceAdapter;

    private Button addServices;
    private ListView serviceSPList;
    private TextView listTypeText;

    private int listSet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listSet = 1;

        setContentView(R.layout.service_layout);
        serviceSPList = (ListView) findViewById(R.id.listSPServices);
        setContentView(R.layout.activity_service_provider);

        addServices = findViewById(R.id.btnSPAdd);
        listTypeText = findViewById(R.id.textSPServices);

        mAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mServicesRef = mRootRef.child("Services");
        mUserRef = mRootRef.child("Accounts").child(user.getUid());
        //Toast.makeText(ServiceProviderActivity.this,mUserRef.child("ProvidedServices").getKey(),Toast.LENGTH_LONG).show();

        setKeys(1);
        setAdapterServices();
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
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    public void setAdapterServices(){
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                serviceAdapter = new ServiceCustomAdapter(ServiceProviderActivity.this, keys);
                serviceSPList = (ListView) findViewById(R.id.listSPServices);
                serviceSPList.setAdapter(serviceAdapter);
                serviceAdapter.notifyDataSetChanged();
            }
        },500); //1000ms = 1sec
    }

    public void setKeys(int type){
        //type = 0 all services, type = 1 provided services
        if(type == 0){
            instantiateKeys(mServicesRef);
        }else if(type == 1){
            instantiateKeys(mUserRef.child("ProvidedServices"));
        }
    }

    public void addServices(View view){
        if(listSet == 1){
            setKeys(0);
            setAdapterServices();
            addServices.setText("Back");
            listSet = 0;
            listTypeText.setText("Click the service to add: ");
        }else if(listSet == 0){
            setKeys(1);
            setAdapterServices();
            addServices.setText("Add");
            listSet = 1;
            listTypeText.setText("Your Services: ");
        }

    }

}
