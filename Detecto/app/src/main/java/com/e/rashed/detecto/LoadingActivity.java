package com.e.rashed.detecto;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class LoadingActivity extends AppCompatActivity {
    DatabaseReference last_name;
    public String pred_name_str;

    DatabaseReference top4_ref;
    ArrayList<String> top_4 = new ArrayList<String>();;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

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


        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Processing..");
        progress.setMessage("অপেক্ষা করুন....");
        progress.setIcon(R.drawable.camera);
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        last_name = database.getReference("last_name");
        top4_ref = database.getReference("prediction/top_4");

        last_name.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pred_name_str = dataSnapshot.getValue(String.class);
                if (pred_name_str.equals(download_url)){

                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    progress.dismiss();

                    get_top4();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void get_top4(){

        top4_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("Reserror","top=datachange");
                Boolean flag = false;
                Log.e("La","t4="+dataSnapshot.getValue());
                for(DataSnapshot dsp: dataSnapshot.getChildren()){
                    String s =  (String) dsp.getValue();
//                    if(s.equals("Not Leaf") && top_4.size()==0){
//                        flag = true;
//                        Log.e("La","No leaf detected");
//                        Intent i = new Intent(getApplicationContext(), NoLeafActivity.class);
//                        i.putExtra("top_4", top_4);
//                        startActivity(i);
//                    }
                    top_4.add(s);
                }
                if(top_4.size()==4) {
                    Log.e("Reserror","top=4");
                    Intent i = new Intent(getApplicationContext(), ResultActivity.class);
                    i.putExtra("top_4", top_4);
                    finish();
                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
