package com.e.rashed.detecto;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {
    TextView mTextview;
    TextView diseaseTxt;
    DatabaseReference pred_text_ref;
    DatabaseReference last_name;
    public String pred_result;

    public String pred_name_str;


    //Recyclerview
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public List<Person> persons;
    public RVAdapter adapter;
    public RecyclerView rv;

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

        progress.dismiss();

        last_name.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pred_name_str = dataSnapshot.getValue(String.class);
                if (pred_name_str.equals(download_url)){

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


        //Recyclerview

        initializeData();
        Log.e("Pd","Data initialized");

        rv = (RecyclerView)findViewById(R.id.rvResult);

        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        Log.e("Pd","rv init");

        adapter = new RVAdapter(persons);
        if (adapter.getItemCount()!=0) {
            Log.e("Pd", "adapter initialized" + persons.get(0).name);
            rv.setAdapter(adapter);
            Log.e("Pd", "adapter set" + persons.get(0).name);
        }
        else {
            Log.e("Pd", "adapter empty");
        }



    }

    private void initializeData(){
        persons = new ArrayList<>();

        DatabaseReference plant;
        DatabaseReference diseases;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        plant = database.getReference("AndroidApp");
        diseases = database.getReference("AndroidApp/Apple/Diseases");

        diseases.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<String> disease_list = new ArrayList<String>();

                for(DataSnapshot dsp: dataSnapshot.getChildren()){
                    Log.e("Resultactivity","dsp="+dsp.getValue());
                    String image_url = (String) dsp.child("image_url").getValue();
                    String disese_name = (String) dsp.child("Name").getValue();
                    String remedy_url = (String) dsp.child("Remedy").getValue();
                    String remedy_short = (String) dsp.child("RemedyWord").getValue();

                    persons.add(new Person(disese_name, remedy_short, R.drawable.apple, image_url));
                    Log.e("Resultactivity","n="+disese_name+image_url);

                }
                rv.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }
}
