package ca.harshgupta.seg2105_project;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class ClientHomeFragment1 extends Fragment {
    private TextView clientText;
    View myView;
    private FirebaseUser user;
    private DatabaseReference userInfo;
    private FirebaseAuth mAuth;
    private DatabaseReference mAppointments;
    private DatabaseReference mUserOrders;

    private ListAdapter appointmentAdapter;
    private ListView appointmentList;
    private DiscreteSeekBar discreteSeekBar;
    private TextView comment;

    private List<String> keys;
    private String[] keyArray;
    private ListView clientAvailabilityHomeList;
    private AlertDialog addReview;

    private DatabaseReference mRootRef;
    private DatabaseReference mUserRef;
    private String selectedListItem;

    private addAppointmentsAsyncTask addingToListTask;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_client_home, container, false);

        keys = new ArrayList<String>();
        user = FirebaseAuth.getInstance().getCurrentUser();

        mAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();

        mUserRef = mRootRef.child("Accounts").child(user.getUid());

        mAppointments = FirebaseDatabase.getInstance().getReference().child("Appointment");
        mUserOrders = FirebaseDatabase.getInstance().getReference().child("Accounts").child(user.getUid()).child("Orders");
        clientAvailabilityHomeList = (ListView) myView.findViewById(R.id.listOfCurrentAppointments);
        instantiateKeys();
        clientAvailabilityHomeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {
                selectedListItem = ((TextView) view.findViewById(R.id.textClientAvailabilityHomeIdOrder)).getText().toString();
                mUserOrders.child(selectedListItem).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String status = dataSnapshot.getValue().toString();
                        if (status.equals("Completed"))
                            addReview(view);

                        if (status.equals("Pending")){
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


    public void onStart (){
        super.onStart();
        clientText = myView.findViewById(R.id.clientText);
        user = FirebaseAuth.getInstance().getCurrentUser();

        userInfo = FirebaseDatabase.getInstance().getReference().child("Accounts")
                .child(user.getUid());

        userInfo.child("FirstName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.getValue().toString();
                clientText.setText("Client: " + userName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError);
            }
        });

        userInfo.child("LastName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.getValue().toString();
                clientText.setText(clientText.getText().toString() + " " + userName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError);
            }
        });
        TextView temp = (TextView) myView.findViewById(R.id.currentAppointment);


            temp.setOnClickListener(new View.OnClickListener() {
                @Override
            public void onClick(View v) {
                addReview(v);
            }
            });


        LayoutInflater factory = LayoutInflater.from(myView.getContext());
        myView = factory.inflate(R.layout.client_review_dialog, null);
        addReview = new AlertDialog.Builder(myView.getContext()).create();
        addReview.setView(myView);

        Button submit = (Button) myView.findViewById(R.id.submitReview);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discreteSeekBar = myView.findViewById(R.id.discreteSeekBar);
                comment = myView.findViewById(R.id.comment);
                int rate = discreteSeekBar.getProgress();
                String commentText = comment.getText().toString();
                Review review = new Review(rate, commentText);
                addReviewFirebase(review);
                addReview.dismiss();
            }
        });

    }

    public void addReview (View view){
        addReview.show();
    }

    public void addReviewFirebase (Review review){

        Toast.makeText(myView.getContext(), "Review Successful", Toast.LENGTH_LONG).show();
        mAppointments.child(selectedListItem).child("Rating").setValue(review.getRate());
        mUserRef.child("Orders").child(selectedListItem).setValue("Rated");
        mAppointments.child(selectedListItem).child("OrderStatus").setValue("Rated");
    }

    private void instantiateKeys(){
        keys.clear();
        mUserOrders.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot uniqueKeySnapShot: dataSnapshot.getChildren()){
                    keys.add(uniqueKeySnapShot.getKey());
                }
                setClientAvailabilityAdapter();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void setClientAvailabilityAdapter (){
        keyArray = new String[keys.size()];
        keyArray = keys.toArray(keyArray);
        //appointmentAdapter = new ClientAvailabilityHomeCustomAdapter(getActivity(), keyArray);
        //clientAvailabilityHomeList.setAdapter(appointmentAdapter);
        addingToListTask = new addAppointmentsAsyncTask();
        addingToListTask.execute();
    }

    //Code used to populate list Asynchronously due to multiple nested datasnapshots in appointmentAdapter
    //Learned to use this class from
    //https://github.com/commonsguy/cw-android/blob/master/Threads/Asyncer/src/com/commonsware/android/async/AsyncDemo.java7
    //https://developer.android.com/reference/android/os/AsyncTask.html#onPostExecute(Result)
    private class addAppointmentsAsyncTask extends AsyncTask<Void, String, String> {
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
            clientAvailabilityHomeList.setAdapter(null);
        }
        @Override
        protected void onPostExecute(String result) {
            // stop the loading animation or something
            appointmentAdapter = new ClientAvailabilityHomeCustomAdapter(getActivity(), keyArray);
            clientAvailabilityHomeList.setAdapter(appointmentAdapter);
            Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
        }
    }
}
