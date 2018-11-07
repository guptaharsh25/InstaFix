package ca.harshgupta.seg2105_project;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
    }

    public void addService(){
        final String[] serviceName = {""};
        final double[] serviceRate = {0};

        final AlertDialog.Builder serviceAdd = new AlertDialog.Builder(this);
        serviceAdd.setTitle("Add New Service");

        final EditText getServiceName = new EditText(this);
        final EditText getServiceRate = new EditText(this);

        getServiceName.setInputType(InputType.TYPE_CLASS_TEXT);
        getServiceRate.setInputType(InputType.TYPE_CLASS_TEXT);

        serviceAdd.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                serviceName[0] = getServiceName.getText().toString();
                try{
                    serviceRate[0] = Double.parseDouble(getServiceRate.getText().toString());
                } catch (Exception exception){
                    Context context = getApplicationContext();
                    CharSequence text = "Please Enter a Valid Service Rate";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });

        serviceAdd.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }
}
