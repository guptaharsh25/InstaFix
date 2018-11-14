package ca.harshgupta.seg2105_project;

import android.app.Activity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Firebase {

    public DatabaseReference getMRootRef(){
        return FirebaseDatabase.getInstance().getReference();
    }

    public DatabaseReference getMRootRefChild(String child){
        return this.getMRootRef().child(child);
    }

    public DatabaseReference getMNewUsernameRef(String uID){
        return getMRootRefChild("Accounts").child(uID);
    }

    public FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public ServiceCustomAdapter getAdapter(Activity activity, String[] keys){
        return new ServiceCustomAdapter(activity, keys);
    }

    public DatabaseReference getChild(DatabaseReference parent, String child){
        return parent.child(child);
    }

    public DatabaseReference getMAdminInitializedRef(){
        return getMRootRefChild("Admin_Initialized");
    }
}
