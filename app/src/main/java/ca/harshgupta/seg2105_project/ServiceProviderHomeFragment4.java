package ca.harshgupta.seg2105_project;

import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
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

public class ServiceProviderHomeFragment4 extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser userInfo;
    private ServiceProviderAppointmentCustomAdapter SPAdapter;
    private ListAdapter appointmentAdapter;

    private DatabaseReference mRootRef;
    private DatabaseReference mUserOrders;
    private DatabaseReference mAppointments;
    private DatabaseReference mUserRef;
    private DatabaseReference mAccounts;
    private String selectedListItem;
    private String selectedListItemClient;

    private ArrayList<String> keys;
    private String[] keyArray;
    private int listSet;

    private TextView listTypeText;
    private ListView orderList;
    public static addAppointmentsSPAsyncTask addingToListSPTask;

    View myView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_service_provider_home_4, container, false);

        keys = new ArrayList<String>();
        userInfo = FirebaseAuth.getInstance().getCurrentUser();

        mAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAccounts = FirebaseDatabase.getInstance().getReference().child("Accounts");

        mUserRef = mRootRef.child("Accounts").child(userInfo.getUid());

        mAppointments = FirebaseDatabase.getInstance().getReference().child("Appointment");
        mUserOrders = FirebaseDatabase.getInstance().getReference().child("Accounts").child(userInfo.getUid()).child("Orders");
        orderList = (ListView) myView.findViewById(R.id.listCurrentOrder);
        instantiateKeys();
        orderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {
                selectedListItem = ((TextView) view.findViewById(R.id.textSPAvailabilityHomeIdOrder)).getText().toString();
                selectedListItemClient = ((TextView) view.findViewById(R.id.textSPAvailabilityHomeIdClient)).getText().toString();
                mAppointments.child(selectedListItem).child("OrderStatus").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue(String.class).equals("Pending")){
                            serviceComplete(view);
                            //Edit and remove
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
        return myView;
    }

    private void instantiateKeys(){
        keys.clear();
        mUserOrders.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot uniqueKeySnapShot: dataSnapshot.getChildren()){
                    keys.add(uniqueKeySnapShot.getKey());
                }
                setSPAppointmentAdapter();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void setSPAppointmentAdapter(){
        keyArray = new String[keys.size()];
        keyArray = keys.toArray(keyArray);
        //appointmentAdapter = new ClientAvailabilityHomeCustomAdapter(getActivity(), keyArray);
        //clientAvailabilityHomeList.setAdapter(appointmentAdapter);
        addingToListSPTask = new addAppointmentsSPAsyncTask();
        addingToListSPTask.execute();
    }

    public static void stopAsync(){
        addingToListSPTask.cancel(true);
    }

    //Code used to populate list Asynchronously due to multiple nested datasnapshots in appointmentAdapter
    //Learned to use this class from
    //https://github.com/commonsguy/cw-android/blob/master/Threads/Asyncer/src/com/commonsware/android/async/AsyncDemo.java7
    //https://developer.android.com/reference/android/os/AsyncTask.html#onPostExecute(Result)
    private class addAppointmentsSPAsyncTask extends AsyncTask<Void, String, String> {
        @Override
        protected void onPreExecute() {
            // start loading animation
        }
        @Override
        protected String doInBackground(Void... voids) {
            for(String info : keyArray){
                publishProgress(info);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "Loading";
        }
        @Override
        protected void onProgressUpdate(String... values){
            keys.clear();
            orderList.setAdapter(null);
        }
        @Override
        protected void onPostExecute(String result) {
            // stop the loading animation or something
            appointmentAdapter = new ServiceProviderAppointmentCustomAdapter(getActivity(), keyArray);
            orderList.setAdapter(appointmentAdapter);
            Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
        }
    }

    private void serviceComplete(View view) {
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Order Completed");
        alertDialog.setMessage("Is the order completed: ");
        alertDialog.setButton(Dialog.BUTTON_POSITIVE,"Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAppointments.child(selectedListItem).child("OrderStatus").setValue("Completed");
                mAccounts.child(userInfo.getUid()).child("Orders").child(selectedListItem).setValue("Completed");
                mAccounts.child(selectedListItemClient).child("Orders").child(selectedListItem).setValue("Completed");
            }
        });
        alertDialog.setButton(Dialog.BUTTON_NEGATIVE,"No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }
}
