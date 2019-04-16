package com.e.rashed.detecto;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {
    TextView mTextview;
    TextView diseaseTxt;
    DatabaseReference pred_text_ref;
    public String pred_result;

    //Recyclerview
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public List<Person> persons;
    public RVAdapter adapter;
    public RecyclerView rv;
    public ArrayList<String> top_4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //Get Extras
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                top_4= null;
            } else {
                top_4= extras.getStringArrayList("top_4");
            }
        } else {
            top_4= (ArrayList<String>) savedInstanceState.getSerializable("top_4");
        }

        Log.e("Ra","t_4"+ top_4.get(0));




        mTextview = (TextView) findViewById(R.id.predText);
        diseaseTxt = (TextView) findViewById(R.id.predText2);

        if(top_4.get(0).equals("Not Leaf")){
            Log.e("Ra","nhiding visibilty text");
            Intent i = new Intent(getApplicationContext(), NoLeafActivity.class);
            i.putExtra("top_4", top_4);
            finish();
            startActivity(i);
        }

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
                String pName = namesList[0];
                String dName = namesList[1];

                pName = pName.replace('_',' ');
                dName = dName.replace('_',' ');

                mTextview.setText(pName);
                diseaseTxt.setText(dName);


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
//        LinearLayoutManager llm = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        Log.e("Pd","rv init");

        adapter = new RVAdapter(persons,this);
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
//        diseases = database.getReference("AndroidApp/Apple/Diseases");

        for(int i=0;i<top_4.size();i++) {
            String n = top_4.get(i);
            String[] name = n.split(" ", 2);
            Log.e("Res", "name=" + name[0] + "--" + name[1]);

            String path = "AndroidApp/" + name[0] + "/Diseases/" + n;
            Log.e("ResultactivityD", "path=" + path);
            diseases = database.getReference(path);
//            diseases = database.getReference("AndroidApp/Apple/Diseases/Apple Black Rot");


//            String image_url = (String) dsp.child("image_url").getValue();
//            String disese_name = (String) dsp.child("Name").getValue();
//            String remedy_url = (String) dsp.child("Remedy").getValue();
//            String remedy_short = (String) dsp.child("RemedyWord").getValue();
//
//            persons.add(new Person(disese_name, remedy_short, R.drawable.apple, image_url));
//            Log.e("Resultactivity","n="+disese_name+image_url);


            diseases.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dsp) {

//                    ArrayList<String> disease_list = new ArrayList<String>();
////                    Log.e("ResultactivityD", "dsp=" + dsp.getValue());


                Log.e("ResultactivityD", "dsp=" + dsp.getValue());
                String image_url = (String) dsp.child("image_url").getValue();
                String disese_name = (String) dsp.child("Name").getValue();
                String remedy_url = (String) dsp.child("Remedy").getValue();
                String remedy_short = (String) dsp.child("RemedyWord").getValue();

                persons.add(new Person(disese_name, remedy_short, remedy_url, image_url));
                Log.e("Resultactivity", "n=" + disese_name + image_url);


                    rv.setAdapter(adapter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }




    }
}
