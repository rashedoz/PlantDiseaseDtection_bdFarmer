package com.e.rashed.detecto;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ResultActivity extends AppCompatActivity {
    TextView mTextview;
    TextView diseaseTxt;
    DatabaseReference pred_text_ref;
    DatabaseReference last_name;
    public String pred_result;

    public String pred_name_str;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //Get Extras

        final String download_url;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                download_url= null;
            } else {
                download_url= extras.getString("download_url");
            }
        } else {
            download_url= (String) savedInstanceState.getSerializable("download_url");
        }




        mTextview = (TextView) findViewById(R.id.predText);
        diseaseTxt = (TextView) findViewById(R.id.predText2);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        last_entry_ref = database.getReference("last_entry");
//        last_url_ref = database.getReference("last_url");
        last_name = database.getReference("last_name");
        pred_text_ref = database.getReference("prediction/pred");

        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Processing..");
        progress.setMessage("অপেক্ষা করুন....");
        progress.setIcon(R.drawable.camera);
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        last_name.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pred_name_str = dataSnapshot.getValue(String.class);
                if (pred_name_str.equals(download_url)){
                    progress.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Read from the database the lastEntry
        pred_text_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                pred_result = dataSnapshot.getValue(String.class);
                Log.d("Debug", "Prediction is: " + pred_result);
                String[] namesList = pred_result.split("__");

                mTextview.setText(namesList[0]);
                diseaseTxt.setText(namesList[1]);


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Debug", "Failed to read value.", error.toException());
            }
        });
    }
}
