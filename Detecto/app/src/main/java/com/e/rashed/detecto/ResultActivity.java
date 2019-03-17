package com.e.rashed.detecto;

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
    public String pred_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        mTextview = (TextView) findViewById(R.id.predText);
        diseaseTxt = (TextView) findViewById(R.id.predText2);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        last_entry_ref = database.getReference("last_entry");
//        last_url_ref = database.getReference("last_url");
        pred_text_ref = database.getReference("prediction/pred");

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
